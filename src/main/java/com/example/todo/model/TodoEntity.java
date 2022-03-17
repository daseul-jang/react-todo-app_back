package com.example.todo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Builder
@NoArgsConstructor  // 빈 생성자 자동 생성
@AllArgsConstructor // 매개변수가 있는 생성자 자동 생성
@Data               // getter/setter 생성
@Entity
@Table(name = "Todo")
public class TodoEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;      // 해당 오브젝트의 아이디
    private String userId;  // 해당 오브젝트를 생성한 사용자의 아이디
    private String title;   // Todo 타이틀
    private boolean checked;   // Todo 완료시 체크
}
