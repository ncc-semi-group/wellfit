package com.example.demo.friendship.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.demo.dto.board.FollowDto;
import com.example.demo.dto.user.UserDto;

import java.util.List;
import java.util.Map;

@Mapper
public interface FollowMapper {
    List<UserDto> getFollowingByUserId(@Param("userId") int userId);

    // 유저를 팔로우하는 목록 조회
    List<UserDto> getFollowerByUserId(@Param("userId") int userId);

    // 팔로잉 중 이름으로 검색
    public List<UserDto> searchFollowingByNickname(@Param("userId") int userId, @Param("nickname") String nickname);

    // 팔로워 중 이름으로 검색
    public List<UserDto> searchFollowerByNickname(@Param("userId") int userId, @Param("nickname") String nickname);

    // 팔로우 추가
    void insertFollowing(FollowDto followDto);

    // 팔로우 삭제
    void deleteFollowing(@Param("userId1") int userId1, @Param("userId2") int userId2);
}
