package com.phcworld.userservice.infrastructure;

import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.exception.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRedisRepositoryImpl {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String HASH_KEY = "USERS";
    private final String NAME_KEY = "NAME:";
    private final String EMAIL_KEY = "EMAIL";

    public UserRedisEntity save(User user) {
        UserRedisEntity saveUser = UserRedisEntity.from(user);
        redisTemplate.opsForHash().put(HASH_KEY, user.getUserId(), saveUser);
        redisTemplate.opsForSet().add(NAME_KEY + user.getName(), user.getUserId());
        redisTemplate.opsForHash().put(EMAIL_KEY, user.getEmail(), user.getUserId());
        return saveUser;
    }

    public Optional<UserRedisEntity> findByUserId(String userId) {
        HashOperations<String, String, Object> hops = redisTemplate.opsForHash();
        UserRedisEntity userRedisEntity = (UserRedisEntity) hops.get(HASH_KEY, userId);
        return Optional.ofNullable(userRedisEntity);
    }

    public List<UserRedisEntity> findByName(String name) {
        Set<Object> members = redisTemplate.opsForSet().members(NAME_KEY + name);
        if(Objects.isNull(members) || members.isEmpty()){
            return new ArrayList<>();
        }
        return members.stream()
                .map(userId -> (UserRedisEntity) redisTemplate.opsForHash().get(HASH_KEY, (String) userId))
                .toList();
    }

    public Optional<UserRedisEntity> findByEmail(String email) {
        HashOperations<String, String, Object> hops = redisTemplate.opsForHash();
        String userId = (String) hops.get(EMAIL_KEY, email);
        if(Objects.isNull(userId) || userId.isEmpty()){
            throw new NotFoundException();
        }
        UserRedisEntity userRedisEntity = (UserRedisEntity) hops.get(HASH_KEY, userId);
        return Optional.ofNullable(userRedisEntity);
    }

    public List<UserRedisEntity> findByUserIds(List<String> userIds) {
        HashOperations<String, String, Object> hops = redisTemplate.opsForHash();
        List<UserRedisEntity> list = new ArrayList<>();
        for (String userId : userIds){
            UserRedisEntity userRedisEntity = (UserRedisEntity) hops.get(HASH_KEY, userId);
            if(Objects.nonNull(userRedisEntity)){
                list.add(userRedisEntity);
            }
        }
        return list;
    }

}
