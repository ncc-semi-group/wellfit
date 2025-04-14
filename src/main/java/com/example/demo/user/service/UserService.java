package com.example.demo.user.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.dto.UserDto;
import com.example.demo.user.mapper.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserMapper userMapper;
	
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

    public UserDto login(String email, String password) {
        UserDto user = userMapper.getUserById(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    // ==========================
   
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
