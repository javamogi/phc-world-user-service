package com.phcworld.userservice.service;

import com.phcworld.userservice.controller.port.UserService;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.domain.port.UserRequest;
import com.phcworld.userservice.exception.model.DuplicationException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.messagequeue.UserProducer;
import com.phcworld.userservice.security.utils.SecurityUtil;
import com.phcworld.userservice.service.port.LocalDateTimeHolder;
import com.phcworld.userservice.service.port.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
//	private final UploadFileService uploadFileService;
	private final UserProducer userProducer;
	private final LocalDateTimeHolder localDateTimeHolder;

	@Override
	public User register(UserRequest requestUser) {
		userRepository.findByEmail(requestUser.email())
				.ifPresent(user -> {
					throw new DuplicationException();
				});

		User user = User.from(requestUser, passwordEncoder, localDateTimeHolder);
//		return userRepository.save(user);
		return userProducer.send("users", user);
	}

	@Override
	public User getUser(String userId){
		return userRepository.findByUserId(userId)
				.orElseThrow(NotFoundException::new);
	}

	@Override
	public User modify(UserRequest requestDto){
		String userId = SecurityUtil.getCurrentMemberId();
		if(!userId.equals(requestDto.userId())){
			throw new UnauthorizedException();
		}
		User user = userRepository.findByUserId(requestDto.userId())
				.orElseThrow(NotFoundException::new);
		String profileImg = user.getProfileImage();
		if(requestDto.imageName() != null){
//			profileImg = uploadFileService.registerFile(
//					userId,
//					requestDto.imageName(),
//					requestDto.imageData(),
//					FileType.USER_PROFILE_IMG);
		}
		user = user.modify(requestDto, profileImg, passwordEncoder, localDateTimeHolder);
		return userProducer.send("users", user);
	}

	@Override
	public User delete(String userId) {
		if(!SecurityUtil.matchUserId(userId) && SecurityUtil.matchAdminAuthority()){
			throw new UnauthorizedException();
		}
		User user = userRepository.findByUserId(userId)
				.orElseThrow(NotFoundException::new);
		user = user.delete();
		return userProducer.send("users", user);
	}

	@Override
	public Map<String, User> getUsers(List<String> userIds){
		return userRepository.findByUserIds(userIds)
				.stream()
				.collect(Collectors.toMap(
						User::getUserId,
						User::getUser
					)
				);
	}
}
