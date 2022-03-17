package com.example.todo.dto;

import com.example.todo.model.TodoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TodoDTO {
    private String id;
    private String title;
    private boolean checked;

    public TodoDTO(final TodoEntity entity) {
        this.id = entity.getId();
        this.title = entity.getTitle();
        this.checked = entity.isChecked();
    }

    public static TodoEntity toEntity(final TodoDTO dto) {
        return TodoEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .checked(dto.isChecked())
                .build();
    }
}
