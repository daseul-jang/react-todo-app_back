package com.example.todo.controller;

import com.example.todo.dto.ResponseDTO;
import com.example.todo.dto.TodoDTO;
import com.example.todo.model.TodoEntity;
import com.example.todo.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @GetMapping("/test")
    public ResponseEntity<?> testTodo() {
        String str = service.testService();
        List<String> list = new ArrayList<>();
        list.add(str);
        ResponseDTO<String> response = ResponseDTO.<String>builder().data(list).build();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            // 1. DTO를 TodoEntity로 변환
            TodoEntity entity = TodoDTO.toEntity(dto);

            // 2. id를 null로 초기화. 생성 당시에는 id가 없어야 함.
            entity.setId(null);

            // 3. 아이디 설정.
            entity.setUserId(userId);

            // 4. 서비스를 이용해 Todo 엔티티 생성.
            List<TodoEntity> entities = service.create(entity);

            // 5. 자바 스트림을 이용, 리턴된 엔티티 리스트를 TodoDTO 리스트로 변환.
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

            // 6. 변환된 TodoDTO 리스트를 이용해 ResponseDTO를 초기화.
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            // 7. ResponseDTO를 리턴.
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 8. 예외가 있는 경우 dto 대신 error에 메세지를 넣어 리턴.
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 해당 사용자의 Todolist를 보여줌
    @GetMapping
    public ResponseEntity<?> retrieveTodoList(@AuthenticationPrincipal String userId) {
        // 서비스 클래스의 retrieve 메서드를 사용해 Todo 리스트를 가져옴.
        List<TodoEntity> entities = service.retrieve(userId);
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<?> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        TodoEntity entity = TodoDTO.toEntity(dto);
        entity.setUserId(userId);   // 아이디 설정

        // 서비스를 이용해 entity를 수정.
        List<TodoEntity> entities = service.update(entity);
        List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());

        ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            TodoEntity entity = TodoDTO.toEntity(dto);
            entity.setUserId(userId);

            List<TodoEntity> entities = service.delete(entity);
            List<TodoDTO> dtos = entities.stream().map(TodoDTO::new).collect(Collectors.toList());
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().data(dtos).build();

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            String error = e.getMessage();
            ResponseDTO<TodoDTO> response = ResponseDTO.<TodoDTO>builder().error(error).build();
            return ResponseEntity.badRequest().body(response);
        }
    }
}
