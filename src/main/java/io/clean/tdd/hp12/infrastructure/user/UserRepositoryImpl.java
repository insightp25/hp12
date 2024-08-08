package io.clean.tdd.hp12.infrastructure.user;

import io.clean.tdd.hp12.domain.user.model.User;
import io.clean.tdd.hp12.domain.user.port.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public User getById(long id) {
        return userJpaRepository.findById(id)
            .orElseThrow()
            .toModel();
    }
}
