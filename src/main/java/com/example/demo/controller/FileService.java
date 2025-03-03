package com.example.demo.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Bean
	public String uploadFile(MultipartFile file) {
		return result;
	}

}
