package com.example.demo.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.*;

@Service
public class S3Service 
{
	
	private final S3Client s3Client;
	
	 @Value("${aws.s3.bucketName}")
	    private String bucketName;
	
	@Autowired
	public S3Service(S3Client s3Client) 
	{
		this.s3Client = s3Client;
		
	}
	
	public String uploadingFile(MultipartFile file) throws IOException
	{
		String key = file.getOriginalFilename();
		
		Path tempFile = Files.createTempFile("upload",key);
		file.transferTo(tempFile);
		
		s3Client.putObject(PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build(),RequestBody.fromFile(tempFile));
		Files.delete(tempFile);
		
		return "File uploaded successfully";
				
	}

}
