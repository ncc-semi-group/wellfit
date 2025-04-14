package com.example.demo.user.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.config.PasswordHashingUtil;
import com.example.demo.dto.user.UserDto;
import com.example.demo.user.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;

	public UserDto login(String email, String password) {
		UserDto user = userMapper.getUserByEmail(email);
		if (user == null) return null;

		String salt = userMapper.getSaltByUserId(user.getId());
		if (salt == null) return null;

		String hashedInput = PasswordHashingUtil.hashPassword(password, salt);
		if (hashedInput.equals(user.getPassword())) {
			return user;
		}
		return null;
	}

	public boolean signup(String email, String password, String nickname) {
		if (email == null || email.trim().isEmpty()
				|| password == null || password.trim().isEmpty()
				|| nickname == null || nickname.trim().isEmpty()) {
			return false;
		}

		// 이메일 중복 체크
		if (userMapper.getUserByEmail(email) != null) {
			return false;
		}

		String salt = PasswordHashingUtil.generateSalt();
		String hashedPassword = PasswordHashingUtil.hashPassword(password, salt);

		UserDto user = new UserDto();
		user.setEmail(email);
		user.setPassword(hashedPassword);
		user.setNickname(nickname);

		int result = userMapper.insertUserAccount(user); // user 저장
		userMapper.insertUserSalt(user.getId(), salt); // salt 저장

		return result > 0;
	}

	public String getSaltByUserId(int userId) {
		return userMapper.getSaltByUserId(userId);
	}
	
    @Value("${file.upload-dir}")
    private String uploadDir;
	
	public UserDto getSelectUser(int id) {
		UserDto dto = userMapper.getSelectUser(id);
		return dto;
	}
	
    public UserDto getSelectNickname(String nickname) {
        UserDto dto = userMapper.getSelectNickname(nickname);
        return dto;
    }
	
	public List<UserDto> getLikeList(int userId) {
		return userMapper.getLikeList(userId);
	}
	
	public String updateProfileImage(MultipartFile file, int id) throws IOException {
        try {
            // 파일 확장자 확인
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // 허용된 확장자인지 확인
            if (!isAllowedExtension(extension)) {
                throw new IllegalArgumentException("지원하지 않는 파일 형식입니다: " + extension);
            }
            
            // 파일 이름 생성 (userId + 타임스탬프 + 확장자)
            String fileName = "profile_" + id + "_" + System.currentTimeMillis() + extension;
            
            // 실제 파일 시스템 경로 설정
            String projectRoot = System.getProperty("user.dir");
            String uploadPath = projectRoot + "/src/main/resources/static/images/profile";
            File directory = new File(uploadPath);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new IOException("디렉토리 생성 실패: " + uploadPath);
                }
            }
            
            // 파일 저장
            File dest = new File(directory, fileName);
            file.transferTo(dest);
            
            // DB에 저장할 이미지 URL (상대 경로)
            String imageUrl = "/images/profile/" + fileName;
            
            // DB 업데이트
            userMapper.mypageupdateProfileImage(id, imageUrl);
            
            System.out.println("프로필 이미지 업데이트 완료 - URL: " + imageUrl);
            return imageUrl;
            
        } catch (IOException e) {
            System.err.println("프로필 이미지 업데이트 실패: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            throw e;
        }
    }

	private boolean isAllowedExtension(String extension) {
        String[] allowedExtensions = {".jpg", ".jpeg", ".png", ".gif"};
        extension = extension.toLowerCase();
        for (String allowed : allowedExtensions) {
            if (extension.equals(allowed)) {
                return true;
            }
        }
        return false;
    }
    
    // ==========================
	 // 사용자 추가
    public void addUser(UserDto userDto) {
        userMapper.insertUser(userDto);
    }

    // 사용자 정보 조회 (ID로 검색)
    public UserDto getUserById(int id) {
        return userMapper.selectUserById(id);
    }

    // 이메일로 사용자 조회
    public UserDto getUserByEmail(String email) {
        return userMapper.selectUserByEmail(email);
    }

    // 사용자 정보 업데이트
    public void mypageUpdateUser(UserDto userDto) {
        userMapper.mypageUpdateUser(userDto);
    }

    // 사용자 목록 조회
    public List<UserDto> getAllUsers() {
        return userMapper.selectAllUsers();
    }

    // 사용자 삭제
    public void deleteUser(int id) {
        userMapper.deleteUser(id);
    }
    
    // ===================================
    
    public List<UserDto> searchUsersByNickname(String nickname) {
        try {
            return userMapper.searchUsersByNickname(nickname);
        } catch (Exception e) {
            System.out.println("닉네임 검색 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public void insertUser(UserDto userDto) {
    	userMapper.insertUser(userDto);
    }
}
