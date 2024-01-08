package com.phcworld.userservice.repository;


import com.phcworld.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByUserId(String userId);

	@Query(nativeQuery = true, value = "SELECT * FROM USERS AS U WHERE U.USER_ID IN (:userIds)")
	List<User> findByUserId(@Param("userIds") List<String> userIds);
}
