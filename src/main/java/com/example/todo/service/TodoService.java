package com.example.todo.service;

import com.example.todo.model.TodoEntity;
import com.example.todo.persistence.TodoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TodoService {

    @Autowired
    private TodoRepository repository;

    public String testService() {
        // TodoEntity 생성
        TodoEntity entity = TodoEntity.builder().title("My first todo item").build();
        // TodoEntity 저장
        repository.save(entity);
        // TodoEntity 검색
        TodoEntity savedEntity = repository.findById(entity.getId()).get();
        return savedEntity.getTitle();
    }

    public List<TodoEntity> create(final TodoEntity entity, Sort sort) {
        validate(entity);
        repository.save(entity);

        log.info("Entity ID : {} is saved.", entity.getId());

        return repository.findByUserId(entity.getUserId(), sort);
    }

    private void validate(final TodoEntity entity) {
        if(entity == null) {
            log.warn("Entity cannot be null.");
            throw new RuntimeException("Entity cannot be null.");
        }

        if(entity.getUserId() == null) {
            log.warn("Unknown user.");
            throw new RuntimeException("Unknown user.");
        }
    }

    // 해당 사용자의 ToDoList 출력
    public List<TodoEntity> retrieve(final String userId, Sort sort) {
        return repository.findByUserId(userId, sort);
    }

    public List<TodoEntity> update(final TodoEntity entity, Sort sort) {
        // 1. 저장할 엔티티가 유효한지 확인.
        validate(entity);

        // 2. 넘겨받은 엔티티의 id를 이용해 TodoEntity를 가져옴. (업데이트는 원본이 있어야 가능)
        final Optional<TodoEntity> original = repository.findById(entity.getId());

        original.ifPresent(todo -> {
            // 3. 반환된 TodoEntity가 존재하면 값을 새 entity 값으로 덮어 씌운다. (update)
            todo.setTitle(entity.getTitle());
            todo.setChecked(entity.isChecked());

            // 4. DB에 새 값 저장.
            repository.save(todo);
        });

        // 사용자의 모든 TodoList 리턴.
        return retrieve(entity.getUserId(), sort);
    }

    public List<TodoEntity> delete(final TodoEntity entity, Sort sort) {
        validate(entity);

        try {
            repository.delete(entity);
        } catch (Exception e) {
            log.error("error deleting entity ", entity.getId(), e);

            throw new RuntimeException("error deleting entity " + entity.getId());
        }

        return retrieve(entity.getUserId(), sort);
    }
}