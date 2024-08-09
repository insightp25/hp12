package io.clean.tdd.hp12.infrastructure.user.entity;

import io.clean.tdd.hp12.domain.user.model.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    public static UserEntity from(User user) {
        UserEntity userEntity = new UserEntity();
        userEntity.id = user.id();

        return userEntity;
    }

    public User toModel() {
        return User.builder()
            .id(id)
            .build();
    }
}
