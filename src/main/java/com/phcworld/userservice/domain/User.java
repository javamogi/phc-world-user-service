package com.phcworld.userservice.domain;

import com.phcworld.userservice.utils.FileConvertUtils;
import com.phcworld.userservice.utils.LocalDateTimeUtils;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@ToString(exclude = "password")
@Table(name = "USERS")
@DynamicUpdate
public class User implements Serializable {

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
	
	public String getFormattedCreateDate() {
		return LocalDateTimeUtils.getTime(createDate);
	}

	public String getProfileImageData(){
		return FileConvertUtils.getFileData(profileImage);
	}
	public String getProfileImageUrl(){
		return "http://localhost:8080/image/" + profileImage;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		User user = (User) o;
		return Objects.equals(id, user.id) && Objects.equals(email, user.email) && Objects.equals(name, user.name) && authority == user.authority;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, email, name, authority);
	}

	public void modify(String password, String name, String profileImage) {
		this.password = password;
		this.name = name;
		this.profileImage = profileImage;
	}

	public void delete() {
		this.isDeleted = true;
	}
}
