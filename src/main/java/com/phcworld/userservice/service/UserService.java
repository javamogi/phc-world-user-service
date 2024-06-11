package com.phcworld.userservice.service;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.infrastructure.UserEntity;
import com.phcworld.userservice.controller.port.SuccessResponseDto;
import com.phcworld.userservice.domain.port.UserRequestDto;
import com.phcworld.userservice.controller.port.UserResponseDto;
import com.phcworld.userservice.exception.model.DuplicationException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.messagequeue.UserProducer;
import com.phcworld.userservice.infrastructure.UserJpaRepository;
import com.phcworld.userservice.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
//@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserJpaRepository userRepository;
	private final PasswordEncoder passwordEncoder;
//	private final UploadFileService uploadFileService;
	private final UserProducer userProducer;

	public UserEntity registerUser(UserRequestDto requestUser) {
		userRepository.findByEmail(requestUser.email())
				.ifPresent(user -> {
					throw new DuplicationException();
				});

		UserEntity user = UserEntity.builder()
				.email(requestUser.email())
				.name(requestUser.name())
				.password(passwordEncoder.encode(requestUser.password()))
				.authority(Authority.ROLE_USER)
				.profileImage("blank-profile-picture.png")
				.userId(UUID.randomUUID().toString())
				.isDeleted(false)
				.build();

		userProducer.send("users", user);

//		return userRepository.save(user);
		return user;
	}

	public UserResponseDto getUserInfo(String userId){
		UserEntity user = userRepository.findByUserId(userId)
				.orElseThrow(NotFoundException::new);
		return UserResponseDto.of(user);
	}

	public UserResponseDto modifyUserInfo(UserRequestDto requestDto){
		String userId = SecurityUtil.getCurrentMemberId();
		if(!userId.equals(requestDto.userId())){
			throw new UnauthorizedException();
		}
		UserEntity user = userRepository.findByUserId(requestDto.userId())
				.orElseThrow(NotFoundException::new);
		String profileImg = user.getProfileImage();
		if(requestDto.imageName() != null){
//			profileImg = uploadFileService.registerFile(
//					userId,
//					requestDto.imageName(),
//					requestDto.imageData(),
//					FileType.USER_PROFILE_IMG);
		}
		user.modify(passwordEncoder.encode(requestDto.password()), requestDto.name(), profileImg);

		userProducer.send("users", user);

		return UserResponseDto.of(user);
	}

	public SuccessResponseDto deleteUser(String userId) {
		String securityUserId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();

		if(!securityUserId.equals(userId) && authorities != Authority.ROLE_ADMIN){
			throw new UnauthorizedException();
		}
		UserEntity user = userRepository.findByUserId(userId)
				.orElseThrow(NotFoundException::new);
		user.delete();
		userProducer.send("users", user);

		return SuccessResponseDto.builder()
				.statusCode(200)
				.message("삭제 성공")
				.build();
	}

	public Map<String, UserResponseDto> getUsersByUserIdList(List<String> userIds){
		return userRepository.findByUserId(userIds)
				.stream()
				.collect(Collectors.toMap(
						UserEntity::getUserId,
					UserResponseDto::of)
				);
	}
}
