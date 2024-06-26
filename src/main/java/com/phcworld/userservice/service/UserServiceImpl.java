package com.phcworld.userservice.service;

import com.phcworld.userservice.controller.port.UserService;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.UserRequest;
import com.phcworld.userservice.exception.model.DeletedEntityException;
import com.phcworld.userservice.exception.model.DuplicationException;
import com.phcworld.userservice.exception.model.ForbiddenException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.security.utils.SecurityUtil;
import com.phcworld.userservice.service.port.LocalDateTimeHolder;
import com.phcworld.userservice.service.port.UserProducer;
import com.phcworld.userservice.service.port.UserRepository;
import com.phcworld.userservice.service.port.UuidHolder;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.network.DelayedResponseAuthenticationException;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@Builder
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
//	private final UploadFileService uploadFileService;
	private final UserProducer userProducer;
	private final LocalDateTimeHolder localDateTimeHolder;
	private final UuidHolder uuidHolder;

//	public UserServiceImpl(@Qualifier("jpaUserRepository") UserRepository userRepository,
	public UserServiceImpl(@Qualifier("redisUserRepository") UserRepository userRepository,
						   PasswordEncoder passwordEncoder,
						   UserProducer userProducer,
						   LocalDateTimeHolder localDateTimeHolder,
						   UuidHolder uuidHolder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userProducer = userProducer;
		this.localDateTimeHolder = localDateTimeHolder;
		this.uuidHolder = uuidHolder;
	}

	@Override
	@Transactional
	public User register(UserRequest requestUser) {
		userRepository.findByEmail(requestUser.email())
				.ifPresent(user -> {
					throw new DuplicationException();
				});

		User user = User.from(requestUser,
				passwordEncoder,
				localDateTimeHolder,
				uuidHolder);
//		return userRepository.save(user);
		user = userRepository.save(user);
		return userProducer.send("users", user);
	}

	@Override
	@Transactional(readOnly = true)
	public User getUserByUserId(String userId){
		User user = userRepository.findByUserId(userId)
				.orElseThrow(NotFoundException::new);
		if(user.isDeleted()){
			throw new DeletedEntityException();
		}
		return user;
	}

	@Override
	@Transactional
	public User modify(UserRequest requestDto){
		String userId = SecurityUtil.getCurrentMemberId();
		if(!userId.equals(requestDto.userId())){
			throw new ForbiddenException();
		}
		User user = userRepository.findByUserId(requestDto.userId())
				.orElseThrow(NotFoundException::new);
		if(user.isDeleted()){
			throw new DeletedEntityException();
		}
		String profileImg = user.getProfileImage();
		if(requestDto.imageName() != null){
//			profileImg = uploadFileService.registerFile(
//					userId,
//					requestDto.imageName(),
//					requestDto.imageData(),
//					FileType.USER_PROFILE_IMG);
		}
		user = user.modify(requestDto, profileImg, passwordEncoder, localDateTimeHolder);
		userRepository.save(user);
		return userProducer.send("users", user);
	}

	@Override
	@Transactional
	public User delete(String userId) {
		if(!SecurityUtil.matchUserId(userId) && SecurityUtil.matchAdminAuthority()){
			throw new ForbiddenException();
		}
		User user = userRepository.findByUserId(userId)
				.orElseThrow(NotFoundException::new);
		if(user.isDeleted()){
			throw new DeletedEntityException();
		}
		user = user.delete();
		userRepository.save(user);
		return userProducer.send("users", user);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, User> getUsers(List<String> userIds){
		return userRepository.findByUserIds(userIds)
				.stream()
				.collect(Collectors.toMap(
								User::getUserId,
								Function.identity()
						)
				);
	}

	@Override
	@Transactional(readOnly = true)
	public List<User> getUserByName(String name) {
		return userRepository.findByName(name);
	}
}
