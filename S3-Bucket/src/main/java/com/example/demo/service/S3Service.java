package com.example.demo.service;

import com.example.demo.Repository.UserAttachmentRepo;
import com.example.demo.Repository.UserRepository;
import com.example.demo.model.User;
import com.example.demo.model.UserAttachmentID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final UserRepository userRepository;
    private final UserAttachmentRepo attachmentRepo;
    

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    public S3Service(S3Client s3Client, UserRepository userRepository, UserAttachmentRepo attachmentRepo) {
        this.s3Client = s3Client;
        this.userRepository = userRepository;
        this.attachmentRepo = attachmentRepo;
    }

    public String uploadFileAndCreateAttachment(MultipartFile file, Long userId) throws IOException {
        String fileType = file.getContentType();
        if (!fileType.equals("image/jpeg") && !fileType.equals("image/png") && !fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            throw new IllegalArgumentException("Invalid file type. Only JPG, PNG, and DOCX files are allowed.");
        }

        Optional<User> ById = userRepository.findById(userId);
        if (!ById.isPresent()) {
            throw new IllegalArgumentException("Invalid User ID.");
        }

        String fileName = file.getOriginalFilename();
        try (var inputStream = file.getInputStream()) {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));
        }

        // Create and save the user attachment metadata
        UserAttachmentID attachment = new UserAttachmentID();
        attachment.setFileName(fileName);
        attachment.setFileType(fileType);
        attachment.setLastUpdatedOn(LocalDateTime.now());
        attachment.setUser(ById.get());
        attachmentRepo.save(attachment);

        return "File uploaded and attachment saved successfully.";
    }

 
    public List<UserAttachmentID> getFiles(int page, int size, String sortOrder) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("lastUpdatedOn").descending());
        return attachmentRepo.findAll(pageable).getContent();
    }
    
    public byte[] downloadFileForUser(String filename, Long userId) throws IOException {
        // Find file by filename
        Optional<UserAttachmentID> attachmentIdOptional = attachmentRepo.findByFileName(filename);
        if (!attachmentIdOptional.isPresent()) {
            return null;
        }
        UserAttachmentID userAttachmentId = attachmentIdOptional.get();
 
        if (!userAttachmentId.getUser().getId().equals(userId)) {
            return null;
        }
        try (InputStream inputStream = s3Client.getObject(
                GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(userAttachmentId.getFileName())
                    .build())) {
            
            return inputStream.readAllBytes();  
        } catch (S3Exception e) {
            throw new IOException("Error occurred while fetching file from S3: " + e.awsErrorDetails().errorMessage());
        }
    }
}
