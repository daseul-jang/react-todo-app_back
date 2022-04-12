package com.example.todo.service;

import com.example.todo.model.UserEntity;
import com.example.todo.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserEntity create(final UserEntity userEntity) {
        if(userEntity == null || userEntity.getEmail() == null
        || userEntity.equals("") || userEntity.getEmail().equals("")) {
            throw new RuntimeException("Invalid arguments");
        }

        final String email = userEntity.getEmail();

        // 이메일 중복일때
        if(userRepository.existsByEmail(email)) {
            log.warn("Email already exists {}", email);
            throw new RuntimeException("Email already exists");
        }

        return userRepository.save(userEntity);
    }

    public boolean emailCheck(final String email) {
        return userRepository.existsByEmail(email);
    }

    public UserEntity getByCredentials(final String email, final String password,
                                       final PasswordEncoder encoder) {
        final UserEntity originalUser = userRepository.findByEmail(email);

        // matches 메서드를 이용해 패스워드가 같은지 확인
        if(originalUser != null && encoder.matches(password, originalUser.getPassword())) {
            return originalUser;
        }

        return null;
    }
}
