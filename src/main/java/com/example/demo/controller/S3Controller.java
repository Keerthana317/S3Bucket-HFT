package com.example.demo.controller;

import java.io.IOException;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.Service.S3Service;

@RestController
@ComponentScan(basePackages = {"com.example.demo","com.example.demo.Service","com.example.demo.configuration","com.example.demo.controller"})
public class S3Controller {
	
	private final FileService fileService;
	
	public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }
	
	@PostMapping("/upload")
	public ResponseEntity<String>uploadFile(@RequestParam("file") MultipartFile file) throws IOException
	{
		String result = fileService.uploadFile(file);
		return ResponseEntity.ok(result);
	}
}
