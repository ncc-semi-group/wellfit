package data.service;

import java.util.List;

import org.springframework.stereotype.Service;

import data.dto.CommentDto;
import data.mapper.CommentMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CommentService {
	CommentMapper commentMapper;
	
	public List<CommentDto> getSelectUserId(int userId){
		return commentMapper.getSelectUserId(userId);
	}
}
