package com.example.demo.user.mapper;

import com.example.demo.dto.UserDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface UserMapper {

    public UserDto getSelectUser(int id);
    public UserDto getSelectNickname(String nickname);
    public List<UserDto> getLikeList(int userId);
    public void mypageUpdateUser(UserDto dto);
    public void mypageupdateProfileImage(@Param("id") int id, @Param("imageUrl") String imageUrl);
    public void updateUser(UserDto dto);

    // 사용자 추가
    public void insertUser(UserDto userDto);
    public int insertUserAccount(UserDto userDto);

    // 사용자 정보 조회
    public UserDto selectUserById(@Param("id") int id);

    // 이메일로 사용자 조회
    public UserDto selectUserByEmail(@Param("email") String email);
    public UserDto getUserByEmail(@Param("email") String email);
    public String getSaltByUserId(@Param("userId") int userId);
    public void insertUserSalt(@Param("userId") int userId, @Param("salt") String salt);

    // 사용자 목록 조회
    public List<UserDto> selectAllUsers();

    // 사용자 삭제
    public void deleteUser(@Param("id") int id);

    public List<UserDto> getAllUsers();
    public UserDto getUserById(@Param("id") int id);
    public List<UserDto> searchUsersByNickname(@Param("nickname") String nickname);
}