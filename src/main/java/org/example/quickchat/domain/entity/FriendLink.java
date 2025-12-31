package org.example.quickchat.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class FriendLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @JsonIgnore // 얘는 json으로 보낼때 안보낸다는 뜻
    @ManyToOne
    Member source;
    @ManyToOne
    @JoinColumn(name="targetId") // 생략 가능! (단, 아래 필드명 + _id 여야함) - 위에 source 처럼!
    Member target;
    String alias;
}
