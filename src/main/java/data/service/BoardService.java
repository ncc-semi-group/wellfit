package data.service;

import java.util.List;

import org.springframework.stereotype.Service;

import data.dto.BoardDto;
import data.mapper.BoardMapper;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BoardService {
	BoardMapper boardMapper;
	
	public List<BoardDto> getSelectUserId(int userId){
		return boardMapper.getSelectUserId(userId);
	}
}
