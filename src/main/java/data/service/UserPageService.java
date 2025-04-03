package data.service;

import org.springframework.stereotype.Service;
import data.dto.UserDto;
import data.mapper.UserPageMapper;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPageService {
    private final UserPageMapper userPageMapper;

    public UserDto getUserProfile(int userId) {
        return userPageMapper.getUserProfile(userId);
    }
    
    public List<UserDto> getOtherUsers(int currentUserId) {
        return userPageMapper.getOtherUsers(currentUserId);
    }
}