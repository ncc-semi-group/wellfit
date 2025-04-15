package com.example.demo.friendship.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FollowRequestMapper {
	int deleteFollowRequest(@Param("id") int id);
}
