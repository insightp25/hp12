package io.clean.tdd.hp12.domain.user.port;

import io.clean.tdd.hp12.domain.user.model.User;

public interface UserRepository {

    User getById(long id);

    User save(User user);
}
