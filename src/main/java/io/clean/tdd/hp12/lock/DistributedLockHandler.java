package io.clean.tdd.hp12.lock;

import io.clean.tdd.hp12.domain.common.CustomException;
import io.clean.tdd.hp12.domain.common.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DistributedLockHandler {

    @Autowired
    @Qualifier("redisLockTemplate")
    private RedisTemplate<String, String> redisLockTemplate;

    private static final String LOCK_PREFIX = "lock:";
    private static final String LOCK_DUMMY_VALUE = "1";

    public void acquireLock(String lockKeyPostfix) {
        String lockKey = LOCK_PREFIX + lockKeyPostfix;

        Boolean acquired = redisLockTemplate.opsForValue()
            .setIfAbsent(lockKey, LOCK_DUMMY_VALUE);

        if (!Boolean.TRUE.equals(acquired)) {
            throw new CustomException(ErrorCode.SEAT_ACCESS_FAILURE_ERROR);
        }
    }

    public void releaseLock(String lockKeyPostfix) {
        String lockKey = LOCK_PREFIX + lockKeyPostfix;

        String lockValue = redisLockTemplate.opsForValue().get(lockKey);

        if (LOCK_DUMMY_VALUE.equals(lockValue)) {
            redisLockTemplate.delete(lockKey);
        }
    }
}
