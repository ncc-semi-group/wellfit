package data.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import data.dto.UserDto;
import java.util.List;

@Mapper
public interface UserMapper {
	public UserDto getSelectUser(int id);
	public UserDto getSelectNickname(String nickname);
	public List<UserDto> getLikeList(int userId);
	public void mypageUpdateUser(UserDto dto);
	public void mypageupdateProfileImage(@Param("id") int id, @Param("imageUrl") String imageUrl);
	public void updateUser(UserDto dto);
}
