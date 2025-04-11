package com.example.demo.badge.service;

import java.util.List;
import java.sql.Timestamp;
import org.springframework.stereotype.Service;

import com.example.demo.badge.mapper.UserBadgeMapper;
import com.example.demo.dto.UserBadgeDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserBadgeService {
	UserBadgeMapper userbadgeMapper;
	
	public List<UserBadgeDto> getSelectUserId(int userId){
		return userbadgeMapper.getSelectUserId(userId);
	}
	
	public void updateUserBadge(UserBadgeDto userBadge) {
		userbadgeMapper.updateUserBadge(userBadge);
	}

	public void createUserBadge(int userId, int badgeId) {
		UserBadgeDto newBadge = new UserBadgeDto();
		newBadge.setUserId(userId);
		newBadge.setBadgeId(badgeId);
		newBadge.setIsAchieved(0);
		newBadge.setCondition_count(1);
		newBadge.setAchievedDate(new Timestamp(System.currentTimeMillis()));
		userbadgeMapper.insertUserBadge(newBadge);
	}

	private UserBadgeDto getOrCreateBadge(int userId, int badgeId) {
		List<UserBadgeDto> userBadges = getSelectUserId(userId);
		UserBadgeDto badge = null;
		
		// 해당 뱃지 찾기
		for (UserBadgeDto userBadge : userBadges) {
			if (userBadge.getBadgeId() == badgeId) {
				badge = userBadge;
				break;
			}
		}
		
		// 뱃지가 없으면 새로 생성
		if (badge == null) {
			createUserBadge(userId, badgeId);
			userBadges = getSelectUserId(userId);
			for (UserBadgeDto userBadge : userBadges) {
				if (userBadge.getBadgeId() == badgeId) {
					badge = userBadge;
					break;
				}
			}
		}
		
		return badge;
	}

	// 1. 첫 글쓰기 (1회)
	public void manualUpdatePostBadge(int userId) {
		UserBadgeDto badge = getOrCreateBadge(userId, 1);
		if (badge != null) {
			int newCount = badge.getCondition_count() + 1;
			badge.setCondition_count(newCount);
			if (newCount >= 1 && badge.getIsAchieved() == 0) {
				badge.setIsAchieved(1);
			}
			updateUserBadge(badge);
		}
	}

	// 2. 구르미 (30일 연속 로그인)
	public void manualUpdateLoginBadge(int userId, boolean isAttendanceChecked) {
		UserBadgeDto badge = getOrCreateBadge(userId, 2);
		if (badge != null) {
			if (isAttendanceChecked) {
				// 출석 체크가 true인 경우에만 카운트 증가
				int newCount = badge.getCondition_count() + 1;
				badge.setCondition_count(newCount);
				if (newCount >= 30 && badge.getIsAchieved() == 0) {
					badge.setIsAchieved(1);
				}
			} else {
				// 출석 체크가 false인 경우 카운트 초기화
				badge.setCondition_count(0);
			}
			updateUserBadge(badge);
		}
	}

	// 3. 파워 목표러 (7일 연속 목표 달성)
	public void manualUpdateGoalStreakBadge(int userId, boolean isAttendanceChecked) {
		UserBadgeDto badge = getOrCreateBadge(userId, 3);
		if (badge != null) {
			if (isAttendanceChecked) {
				// 출석 체크가 true인 경우에만 카운트 증가
				int newCount = badge.getCondition_count() + 1;
				badge.setCondition_count(newCount);
				if (newCount >= 7 && badge.getIsAchieved() == 0) {
					badge.setIsAchieved(1);
				}
			} else {
				// 출석 체크가 false인 경우 카운트 초기화
				badge.setCondition_count(0);
			}
			updateUserBadge(badge);
		}
	}

	// 4. 댓글 마스터 (1000개 댓글)
	public void manualUpdateCommentBadge(int userId) {
		UserBadgeDto badge = getOrCreateBadge(userId, 4);
		if (badge != null) {
			int newCount = badge.getCondition_count() + 1;
			badge.setCondition_count(newCount);
			if (newCount >= 1000 && badge.getIsAchieved() == 0) {
				badge.setIsAchieved(1);
			}
			updateUserBadge(badge);
		}
	}

	// 5. 기록 마스터 (30일 기록)
	public void manualUpdateRecordBadge(int userId, boolean hasRecorded) {
		UserBadgeDto badge = getOrCreateBadge(userId, 5);
		if (badge != null) {
			if (hasRecorded) {
				// 식단이나 운동을 기록한 경우에만 카운트 증가
				int newCount = badge.getCondition_count() + 1;
				badge.setCondition_count(newCount);
				if (newCount >= 30 && badge.getIsAchieved() == 0) {
					badge.setIsAchieved(1);
				}
			} else {
				// 기록이 없는 경우 카운트 초기화
				badge.setCondition_count(0);
			}
			updateUserBadge(badge);
		}
	}

	// 6. 나는야 금주왕 (365일 금주)
	public void manualUpdateAlcoholFreeBadge(int userId, boolean hasNoAlcohol) {
		UserBadgeDto badge = getOrCreateBadge(userId, 6);
		if (badge != null) {
			if (hasNoAlcohol) {
				// 술이 없는 식단을 기록한 경우에만 카운트 증가
				int newCount = badge.getCondition_count() + 1;
				badge.setCondition_count(newCount);
				if (newCount >= 365 && badge.getIsAchieved() == 0) {
					badge.setIsAchieved(1);
				}
			} else {
				// 술이 포함된 경우 카운트 초기화
				badge.setCondition_count(0);
			}
			updateUserBadge(badge);
		}
	}

	// 7. 좋아요 중독자 (3000개 좋아요)
	public void manualUpdateLikeBadge(int userId) {
		UserBadgeDto badge = getOrCreateBadge(userId, 7);
		if (badge != null) {
			int newCount = badge.getCondition_count() + 1;
			badge.setCondition_count(newCount);
			if (newCount >= 3000 && badge.getIsAchieved() == 0) {
				badge.setIsAchieved(1);
			}
			updateUserBadge(badge);
		}
	}

	// 8. 건강식단 소유자 (3일 연속 건강식단)
	public void manualUpdateHealthyMealBadge(int userId, boolean isHealthyMeal) {
		UserBadgeDto badge = getOrCreateBadge(userId, 8);
		if (badge != null) {
			if (isHealthyMeal) {
				// 건강한 식단을 기록한 경우에만 카운트 증가
				int newCount = badge.getCondition_count() + 1;
				badge.setCondition_count(newCount);
				if (newCount >= 3 && badge.getIsAchieved() == 0) {
					badge.setIsAchieved(1);
				}
			} else {
				// 건강하지 않은 식단인 경우 카운트 초기화
				badge.setCondition_count(0);
			}
			updateUserBadge(badge);
		}
	}

	// 9. 파워 인싸 (1000명 친구)
	public void manualUpdateFriendBadge(int userId) {
		UserBadgeDto badge = getOrCreateBadge(userId, 9);
		if (badge != null) {
			int newCount = badge.getCondition_count() + 1;
			badge.setCondition_count(newCount);
			if (newCount >= 1000 && badge.getIsAchieved() == 0) {
				badge.setIsAchieved(1);
			}
			updateUserBadge(badge);
		}
	}

	// 10. 식단 거장자 (10가지 이상 음식)
	public void manualUpdateMealVarietyBadge(int userId) {
		UserBadgeDto badge = getOrCreateBadge(userId, 10);
		if (badge != null) {
			int newCount = badge.getCondition_count() + 1;
			badge.setCondition_count(newCount);
			if (newCount >= 10 && badge.getIsAchieved() == 0) {
				badge.setIsAchieved(1);
			}
			updateUserBadge(badge);
		}
	}

	// 11. 글작성 마스터 (500개 게시글)
	public void manualUpdatePostCountBadge(int userId) {
		UserBadgeDto badge = getOrCreateBadge(userId, 11);
		if (badge != null) {
			int newCount = badge.getCondition_count() + 1;
			badge.setCondition_count(newCount);
			if (newCount >= 500 && badge.getIsAchieved() == 0) {
				badge.setIsAchieved(1);
			}
			updateUserBadge(badge);
		}
	}

	// 12. 만보왕 (5일 연속 만보 걷기)
	public void manualUpdateWalkingBadge(int userId, boolean hasWalked10000) {
		UserBadgeDto badge = getOrCreateBadge(userId, 12);
		if (badge != null) {
			if (hasWalked10000) {
				// 만보 이상 걸은 경우에만 카운트 증가
				int newCount = badge.getCondition_count() + 1;
				badge.setCondition_count(newCount);
				if (newCount >= 5 && badge.getIsAchieved() == 0) {
					badge.setIsAchieved(1);
				}
			} else {
				// 만보 미만인 경우 카운트 초기화
				badge.setCondition_count(0);
			}
			updateUserBadge(badge);
		}
	}
}
