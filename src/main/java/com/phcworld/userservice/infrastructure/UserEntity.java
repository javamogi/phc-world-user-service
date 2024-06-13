package com.phcworld.userservice.infrastructure;

import com.phcworld.userservice.domain.Authority;
import com.phcworld.userservice.domain.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(exclude = "password")
@Table(name = "USERS")
@DynamicUpdate
public class UserEntity implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 50, unique = true)
	private String email;

	@Column(nullable = false, length = 100)
	private String password;

	@Column(nullable = false, length = 50, unique = true)
	private String userId;

	@Column(length = 50)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Authority authority;

	@Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP()")
	private LocalDateTime createDate;

	@Column(nullable = false)
	private LocalDateTime updateDate;

	@Column(length = 100)
	private String profileImage;

//	@ColumnDefault("false")
	@Column(nullable = false)
	private Boolean isDeleted;

	public User toModel() {
		return User.builder()
				.id(id)
				.email(email)
				.password(password)
				.userId(userId)
				.name(name)
				.authority(authority)
				.profileImage(profileImage)
				.createDate(createDate)
				.updateDate(updateDate)
				.profileImage(profileImage)
				.isDeleted(isDeleted)
				.build();
	}

	public static UserEntity from(User user) {
		return UserEntity.builder()
				.id(user.getId())
				.email(user.getEmail())
				.password(user.getPassword())
				.userId(user.getUserId())
				.name(user.getName())
				.authority(user.getAuthority())
				.createDate(user.getCreateDate())
				.updateDate(user.getUpdateDate())
				.profileImage(user.getProfileImage())
				.isDeleted(user.isDeleted())
				.build();
	}
}
