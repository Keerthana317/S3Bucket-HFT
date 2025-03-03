//package com.example.demo.controller;
//
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.demo.Repository.UserRepository;
//import com.example.demo.model.User;
//
//
//@RestController
//public class UserController {
//
//	private  final UserRepository userRepository;
//
//	
//	public UserController(UserRepository userRepository) {
//		this.userRepository = userRepository;
//	}
//
//	
//	@PostMapping("addUser")
//	public User saveUser(@RequestBody User  user) {
//		User save = userRepository.save(user);
//		return save;
//		
//	}
//	
//}

package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) throws Exception {
        String responseMessage = userService.registerUser(user);
        if (responseMessage.contains("already exists")) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(responseMessage);
        }

        return ResponseEntity.ok(responseMessage);
    }
}
