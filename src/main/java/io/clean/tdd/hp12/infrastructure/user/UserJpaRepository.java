package io.clean.tdd.hp12.infrastructure.user;

import io.clean.tdd.hp12.infrastructure.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
}
