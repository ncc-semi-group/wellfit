package com.example.demo.record.service;

import com.example.demo.dto.record.FoodRecordsDto;
import com.example.demo.dto.statistics.DailyStatisticsDto;
import com.example.demo.dto.user.UserDto;
import com.example.demo.record.mapper.RecordMapper;
import com.example.demo.record.mapper.SearchMapper;
import com.example.demo.util.KcalDataConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecordService {
    
    private final RecordMapper recordMapper;
    private final SearchMapper searchMapper;
    
    @Transactional
    public List<FoodRecordsDto> getFoodRecords (int userId, Date date) {
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setUserId(userId);
        foodRecords.setDate(date);
        return recordMapper.getFoodRecords(foodRecords);
    }
    
    @Transactional
    public Map<String, Object> getUserInfo(int userId, Date sqlDate) {
        DailyStatisticsDto dailyStatistics = new DailyStatisticsDto();
        dailyStatistics.setUserId(userId);
        dailyStatistics.setDate(sqlDate.toLocalDate());
        List<DailyStatisticsDto> weightInfo = recordMapper.getWeightInfoByStatistics(dailyStatistics);
        
        Map<String, Object> userInfoMap = new HashMap<>();
        
        // 유저 정보
        UserDto user = recordMapper.getUserById(userId);
        
        // 체중, 목표체중 : 현재 날짜, 이전 날짜, 미래 날짜 3경우로 나눠서 처리
        LocalDate currentDate = LocalDate.now();
        if (sqlDate.toLocalDate().isEqual(currentDate)) {
            // 현재 날짜와 같을 때 : 유저 테이블에서 조회
            // 현재, 목표 체중 통계 테이블에 업데이트
            dailyStatistics.setWeight(0);
            if (!weightInfo.isEmpty()) {
                if (weightInfo.get(0).getDate().equals(sqlDate.toLocalDate())) {
                    dailyStatistics.setWeight(weightInfo.get(0).getWeight());
                }
            }
            dailyStatistics.setGoalWeight(user.getGoalWeight());
        } else if (sqlDate.toLocalDate().isAfter(currentDate)) {
            // 현재 날짜보다 미래 날짜일 때 : 유저 테이블에서 조회
            dailyStatistics.setWeight(0);
            dailyStatistics.setGoalWeight(0);
        } else {
            // 현재 날짜보다 이전 날짜일 때 : 통계 테이블에서 조회 시도, 없으면 유저 테이블에서 조회
            if (!weightInfo.isEmpty()) {
                user.setCurrentWeight(weightInfo.get(0).getWeight());
                if (weightInfo.get(0).getDate().equals(sqlDate.toLocalDate())) {
                    dailyStatistics.setWeight(weightInfo.get(0).getWeight());
                } else {
                    dailyStatistics.setWeight(0);
                }
            } else {
                dailyStatistics.setWeight(0);
            }
            
            // 목표 체중 기본값은 통계 테이블에서 조회 날 ~ 오늘 사이 값이 있으면 가져오고, 없다면 유저 테이블에서 조회
            dailyStatistics.setGoalWeight(user.getGoalWeight());
            Map<String, Date> dateArray = new HashMap<>();
            dateArray.put("startDate", sqlDate);
            dateArray.put("endDate", Date.valueOf(currentDate.minusDays(1)));
            List<DailyStatisticsDto> goalWeightStatistics = recordMapper.getGoalWeightInfoByStatistics(dateArray);
            
            if (!goalWeightStatistics.isEmpty()) {
                for (DailyStatisticsDto info : goalWeightStatistics) {
                    if (info.getDate().isAfter(sqlDate.toLocalDate().minusDays(1))) {
                        user.setGoalWeight(info.getGoalWeight());
                        dailyStatistics.setGoalWeight(info.getGoalWeight());
                        break;
                    }
                }
            }
        }
        
        // 과거 체중 정보
        float pastMonthWeight = 0;
        float pastWeekWeight = 0;
        if (!weightInfo.isEmpty()) {
            for (DailyStatisticsDto info : weightInfo) {
                if (info.getDate().isBefore(sqlDate.toLocalDate().minusMonths(1).plusDays(1))) {
                    pastMonthWeight = info.getWeight();
                    break;
                }
            }
            for (DailyStatisticsDto info : weightInfo) {
                if (info.getDate().isBefore(sqlDate.toLocalDate().minusWeeks(1).plusDays(1))) {
                    pastWeekWeight = info.getWeight();
                    break;
                }
            }
        }
        
        
        // 목표 칼로리 정보
        Map<String, Object> kcalDataMap = KcalDataConverter.getKcalData(user);
        int targetKcal = (int) kcalDataMap.get("targetKcal");
        String userTarget = (String) kcalDataMap.get("userTarget");
        
        // 통계 테이블에 목표 칼로리 추가
        // 통계 반영
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("date", sqlDate);
        
        // 통계 정보가 없으면 추가
        int count = searchMapper.getStatisticsCount(params);
        if (count == 0) searchMapper.addDailyStatistics(params);
        
        // 통계 정보 업데이트
        dailyStatistics.setRecommendKcal(targetKcal);
        dailyStatistics.setTarget(userTarget);
        recordMapper.updateDailyStatistics(dailyStatistics);
        
        // 유저 정보에 추가
        userInfoMap.put("user", user);
        userInfoMap.put("pastMonthWeight", pastMonthWeight);
        userInfoMap.put("pastWeekWeight", pastWeekWeight);
        
        return userInfoMap;
    }
    
    @Transactional
    public void updateUserWeight(int userId, Date sqlDate, float weight, float goalWeight) {
        // 유저 정보 업데이트
        UserDto user = new UserDto();
        user.setId(userId);
        user.setCurrentWeight(weight);
        recordMapper.updateUserWeight(user);
        
        // 유저 정보 가져오기
        user = recordMapper.getUserById(userId);
        
        // 목표 칼로리 정보 계산
        Map<String, Object> kcalDataMap = KcalDataConverter.getKcalData(user);
        int targetKcal = (int) kcalDataMap.get("targetKcal");
        String userTarget = (String) kcalDataMap.get("userTarget");
        
        // 통계 업데이트
        DailyStatisticsDto dailyStatistics = new DailyStatisticsDto();
        dailyStatistics.setUserId(userId);
        dailyStatistics.setDate(sqlDate.toLocalDate());
        dailyStatistics.setWeight(weight);
        dailyStatistics.setRecommendKcal(targetKcal);
        dailyStatistics.setTarget(userTarget);
        dailyStatistics.setGoalWeight(goalWeight);
        recordMapper.updateDailyStatistics(dailyStatistics);
        
        // 성취 여부 검증 (치팅데이 설정 하였으면 패스)
        boolean isCheating = cheatingCheck(userId, sqlDate);
        if (!isCheating) achievedCheck(userId, sqlDate);
    }
    
    // 성취 여부 검증
    @Transactional
    public void achievedCheck(int userId, Date sqlDate) {
        
        // 해당 날짜의 칼로리 합 계산
        FoodRecordsDto foodRecords = new FoodRecordsDto();
        foodRecords.setUserId(userId);
        foodRecords.setDate(sqlDate);
        Integer totalKcal = recordMapper.getTotalKcal(foodRecords);
        if (totalKcal == null) totalKcal = 0;
        
        
        // 통계에서 섭취 가능 칼로리, 목표, 달성 여부
        DailyStatisticsDto dailyStatistics = recordMapper.getArchivedDataByStatistics(foodRecords);
        int recommendKcal = dailyStatistics.getRecommendKcal();
        String target = dailyStatistics.getTarget();
        boolean isAchieved = dailyStatistics.isAchieved();

        
        boolean achieved = false;
        // 목표 달성 여부 확인
        if (target.equals("gain")) {
            if (totalKcal >= recommendKcal * 0.9) achieved = true;
        } else {
            if (totalKcal <= recommendKcal * 1.1) achieved = true;
        }
        
        if (totalKcal == 0) {
            // 칼로리 기록이 없으면 성취 여부를 false로 설정
            achieved = false;
        }
        
        // 조건부 업데이트
        if (achieved != isAchieved) {
            dailyStatistics.setAchieved(achieved);
            recordMapper.updateAchieved(dailyStatistics);
        }
        
    }
    
    // 치팅데이 여부 검증
    @Transactional
    public boolean cheatingCheck(int userId, Date sqlDate) {
        // 통계에서 치팅 여부 확인
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("date", sqlDate);
        int cheatingKcal = recordMapper.getCheatingKcalByStatistics(params);
        
        return cheatingKcal > 0;
    }
    
    // 치팅데이 설정
    @Transactional
    public void useCheatPoint(int userId, Date sqlDate, int cheatingKcal, int cheatPoint) {
        // 유저 정보 업데이트
        UserDto user = new UserDto();
        user.setId(userId);
        user.setCheatingDay(cheatPoint);
        recordMapper.updateCheatingDay(user);
        
        // 통계 업데이트
        DailyStatisticsDto dailyStatistics = new DailyStatisticsDto();
        dailyStatistics.setUserId(userId);
        dailyStatistics.setDate(sqlDate.toLocalDate());
        dailyStatistics.setCheatingKcal(cheatingKcal);
        dailyStatistics.setAchieved(true);
        recordMapper.updateCheatingKcal(dailyStatistics);
    }
    
    // 치팅 포인트 업데이트
    @Transactional
    public void updateCheatPoint(LocalDate yesterday) {
        // 통계 테이블에서 date : 전날, is_achieved : true인 userId 가져오기
        DailyStatisticsDto dailyStatistics = new DailyStatisticsDto();
        dailyStatistics.setDate(yesterday);
        List<Integer> userIds = recordMapper.getAchievedUsersByStatistics(dailyStatistics);
        
        // userId 마다 userDto 가져오기
        for (Integer userId : userIds) {
            UserDto user = recordMapper.getUserById(userId);
            
            // 유틸 클래스에서 amr 계산
            Map<String, Object> kcalDataMap = KcalDataConverter.getKcalData(user);
            int amr = (int) kcalDataMap.get("amr");
            
            // 부여할 치팅 포인트 계산 (amr * 0.05)
            int plusCheatPoint = (int) (amr * 0.05);
            
            // 유저의 치팅포인트 + 부여할 치팅포인트, 1000 이상이면 1000으로 설정
            int newCheatPoint = user.getCheatingDay() + plusCheatPoint;
            if (newCheatPoint > 1000) newCheatPoint = 1000;
            
            // 유저 정보 업데이트
            user.setCheatingDay(newCheatPoint);
            recordMapper.updateCheatingDay(user);
        }
    }
    
}
