package data.service;

import java.util.List;

import org.springframework.stereotype.Service;

import data.dto.UserBadgeDto;
import data.mapper.UserBadgeMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserBadgeService {
	UserBadgeMapper userbadgeMapper;
	
	public List<UserBadgeDto> getSelectUserId(int userId){
		return userbadgeMapper.getSelectUserId(userId);
	}
}
