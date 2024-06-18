package com.phcworld.userservice.infrastructure;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
	Optional<UserEntity> findByEmail(String email);
	Optional<UserEntity> findByUserId(String userId);

	@Query(nativeQuery = true, value = "SELECT * FROM users AS U WHERE U.USER_ID IN (:userIds)")
	List<UserEntity> findByUserIds(@Param("userIds") List<String> userIds);
}
