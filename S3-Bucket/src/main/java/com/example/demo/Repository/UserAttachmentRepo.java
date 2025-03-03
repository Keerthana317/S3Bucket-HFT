package com.example.demo.Repository;

import com.example.demo.model.UserAttachmentID;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserAttachmentRepo extends JpaRepository<UserAttachmentID, Long> {
	
	Optional<UserAttachmentID> findByFileName(String filename);

}

