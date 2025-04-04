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
	
	public void mypageUpdateUser(UserDto dto) {
		userMapper.mypageUpdateUser(dto);
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
            String fileName = id + "_" + System.currentTimeMillis() + extension;
            
            // 업로드 디렉토리 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    throw new IOException("디렉토리 생성 실패: " + uploadDir);
                }
            }
            
            // 파일 저장
            File dest = new File(directory, fileName);
            file.transferTo(dest);
            
            // DB에 저장할 이미지 URL
            String imageUrl = "/upload/profile/" + fileName;
            
            // DB 업데이트
            userMapper.mypageupdateProfileImage(id, imageUrl);
            
            return imageUrl;
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
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

    public UserDto login(String id, String password) {
        UserDto user = userMapper.getUserById(id);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
