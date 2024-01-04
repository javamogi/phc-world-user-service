package com.phcworld.userservice.service;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import com.phcworld.userservice.dto.LoginUserRequestDto;
import com.phcworld.userservice.dto.SuccessResponseDto;
import com.phcworld.userservice.dto.UserRequestDto;
import com.phcworld.userservice.dto.UserResponseDto;
import com.phcworld.userservice.exception.model.DuplicationException;
import com.phcworld.userservice.exception.model.NotFoundException;
import com.phcworld.userservice.exception.model.UnauthorizedException;
import com.phcworld.userservice.jwt.TokenProvider;
import com.phcworld.userservice.jwt.dto.TokenDto;
import com.phcworld.userservice.jwt.service.CustomUserDetailsService;
import com.phcworld.userservice.repository.UserRepository;
import com.phcworld.userservice.security.utils.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final CustomUserDetailsService userDetailsService;
	private final TokenProvider tokenProvider;
//	private final UploadFileService uploadFileService;

	public User registerUser(UserRequestDto requestUser) {
		boolean isFind = userRepository.findByEmail(requestUser.email())
				.isPresent();
		if(isFind){
			throw new DuplicationException();
		}

		User user = User.builder()
				.email(requestUser.email())
				.name(requestUser.name())
				.password(passwordEncoder.encode(requestUser.password()))
				.authority(Authority.ROLE_USER)
				.createDate(LocalDateTime.now())
				.profileImage("blank-profile-picture.png")
				.build();

		return userRepository.save(user);
	}

	public TokenDto tokenLogin(LoginUserRequestDto requestUser) {
		// 비밀번호 확인 + spring security 객체 생성 후 JWT 토큰 생성
		Authentication authentication = SecurityUtil.getAuthentication(requestUser, userDetailsService, passwordEncoder);

		// 토큰 발급
		return tokenProvider.generateTokenDto(authentication);
	}

	public UserResponseDto getLoginUserInfo(){
		Long userId = SecurityUtil.getCurrentMemberId();
		User user = userRepository.findById(userId)
				.orElseThrow(NotFoundException::new);
		return UserResponseDto.of(user);
	}

	public UserResponseDto getUserInfo(Long userId){
		User user = userRepository.findById(userId)
				.orElseThrow(NotFoundException::new);
		return UserResponseDto.of(user);
	}

	public UserResponseDto modifyUserInfo(UserRequestDto requestDto){
        Long userId = SecurityUtil.getCurrentMemberId();
		if(!userId.equals(requestDto.id())){
			throw new UnauthorizedException();
		}
		User user = userRepository.findById(requestDto.id())
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
		return UserResponseDto.of(user);
	}

	public SuccessResponseDto deleteUser(Long id) {
		Long userId = SecurityUtil.getCurrentMemberId();
		Authority authorities = SecurityUtil.getAuthorities();

		if(!userId.equals(id) && authorities != Authority.ROLE_ADMIN){
			throw new UnauthorizedException();
		}
		User user = userRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		user.delete();

		return SuccessResponseDto.builder()
				.statusCode(200)
				.message("삭제 성공")
				.build();
	}

	public String logout() {
		return "로그아웃";
	}

	public TokenDto getNewToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return tokenProvider.generateTokenDto(authentication);
	}
}
