package com.example.demo.configuration;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Configuration {

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of("us-east-1"))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(
                                "AKIA4T4OB43QCRV4ORXK", 
                                "TUpvRSqurc+pArYoXNjoGKVOLIUGrN3Pgsdeb4Eq"))
                )
                .build();
    }
}
