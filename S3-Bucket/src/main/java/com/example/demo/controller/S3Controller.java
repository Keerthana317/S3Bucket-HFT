package com.example.demo.controller;

import com.example.demo.Repository.UserAttachmentRepo;
import com.example.demo.model.UserAttachmentID;
import com.example.demo.service.S3Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class S3Controller {

    private final S3Service s3Service;
    private final UserAttachmentRepo attachmentRepo;
    

    public S3Controller(S3Service s3Service, UserAttachmentRepo attachmentRepo) {
        this.s3Service = s3Service;
		this.attachmentRepo = attachmentRepo;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam(value = "userId") Long userId) 
    {
        try {
            String message = s3Service.uploadFileAndCreateAttachment(file, userId);
            return new ResponseEntity<>(message, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @RestControllerAdvice
	public class GlobalExceptionHandler {
 
	    @ExceptionHandler(MaxUploadSizeExceededException.class)
	    public ResponseEntity<String> handleFileSizeLimitExceeded(MaxUploadSizeExceededException ex) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File size exceeds the maximum limit of 5 MB.");
	    }
	}
 
    @GetMapping("/list")
    public ResponseEntity<List<UserAttachmentID>> listFiles(@RequestParam(defaultValue = "1") int page,
                                                             @RequestParam(defaultValue = "20") int size,
                                                             @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            List<UserAttachmentID> files = s3Service.getFiles(page, size, sortOrder);
            return new ResponseEntity<>(files, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<?> downloadFile(@PathVariable String filename,
                                          @RequestParam("userId") Long userId) {
        try {
            byte[] fileContent = s3Service.downloadFileForUser(filename, userId);
 
            if (fileContent == null) {
                Optional<UserAttachmentID> attachmentid = attachmentRepo.findByFileName(filename);
                if (attachmentid.isPresent()) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("User ID " + userId + " does not have access to the file: " + filename);
                } else {
                    // If the file doesn't exist
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("No records found with the given filename: " + filename);
                }
            }
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);
            String successMessage = "You can download your file from the browser.";
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(successMessage); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }
 
 
}

