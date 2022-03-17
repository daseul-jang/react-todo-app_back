package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.model.UserEntity;
import com.example.todo.security.TokenProvider;
import com.example.todo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            UserEntity user = UserEntity.builder()
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    .password(passwordEncoder.encode(userDTO.getPassword()))
                    .build();

            UserEntity registeredUser = userService.create(user);
            UserDTO responseUserDTO = UserDTO.builder()
                    .email(registeredUser.getEmail())
                    .username(registeredUser.getUsername())
                    .id(registeredUser.getId())
                    .build();

            // 사용자 정보는 항상 하나이므로 리스트로 만들어야 하는 ResponseDTO를 사용하지 않고 그냥 UserDTO를 리턴
            return ResponseEntity.ok(responseUserDTO);
        } catch (Exception e) {
            ResponseDTO responseDTO = ResponseDTO.builder().error(e.getMessage()).build();
            return ResponseEntity.badRequest().body(responseDTO);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticate(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredentials(
                userDTO.getEmail(),
                userDTO.getPassword(),
                passwordEncoder);

        if(user != null) {
            final String token = tokenProvider.create(user);
            final UserDTO responseUserDTO = UserDTO.builder()
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .id(user.getId())
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .error("Login failed.")
                    .build();

            return ResponseEntity.badRequest().body(responseDTO);
        }
    }
}
