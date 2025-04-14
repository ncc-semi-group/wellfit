package com.example.demo.board.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.board.service.BoardService;
import com.example.demo.dto.board.BoardDto;
import com.example.demo.dto.board.CommentDto;
import com.example.demo.dto.board.LikeRequestDto;
import com.example.demo.dto.user.UserDto;

import org.springframework.web.bind.annotation.ResponseBody;


@RestController
public class PostController {

	@Autowired
	BoardService boardService;


	@GetMapping("api/comments/{boardId}")
	@ResponseBody
	public ResponseEntity<Map<String, Object>> getComments(@PathVariable("boardId") int boardId) {
	    try {
	        // boardId에 해당하는 댓글을 가져오는 서비스 호출
	        List<CommentDto> comments = boardService.getCommentByBoardId(boardId);
	        
	        // 댓글이 존재하지 않으면 빈 리스트를 반환할 수도 있지만, 예외 처리가 필요할 수 있습니다.
	        Map<String, Object> response = new HashMap<>();
	        response.put("comments", comments);
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace(); // 로그 확인을 위해 추가
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Collections.singletonMap("error", "댓글을 불러오는 중 오류가 발생했습니다."));
	    }
	}

	@PostMapping("/api")
	public void addComment(@RequestBody CommentDto commentDto) {
		boardService.addComment(commentDto);
	}
	
	
	
	// 좋아요 처리
	  @PostMapping("/api/{postId}/like-toggle")
	    public ResponseEntity<Map<String, Object>> toggleLike(
	            @PathVariable("postId") int postId,
	            @RequestBody LikeRequestDto likeRequest) {

	        boolean liked = boardService.toggleLike(postId, likeRequest.getUserId());
	        int likesCount = boardService.getLikeCount(postId);

	        Map<String, Object> response = new HashMap<>();
	        response.put("likesCount", likesCount);
	        response.put("liked", liked);

	        return ResponseEntity.ok(response);
	    }
	  
	  
	  
	  // 팔로우 상태 토글 (팔로우/팔로우 취소)
	    @PostMapping("/api/follow/toggle")
	    public boolean toggleFollow(@RequestParam("userId") int userId, @RequestParam("targetUserId") int targetUserId) {
	        return boardService.toggleFollow(userId, targetUserId);
	    }

	    // 특정 사용자의 팔로워 수 가져오기
	    @GetMapping("/api/follow/followers")
	    public int getFollowers(@RequestParam("targetUserId") int targetUserId) {
	        return boardService.getFollowerCount(targetUserId);
	    }
  
	    
	    @GetMapping("/api/following-users")
	    public ResponseEntity<List<Long>> getFollowingUserIds(@RequestParam("userId") Long userId) {
	        List<Long> followingUserIds = boardService.getFollowingUserIds(userId);
	        return ResponseEntity.ok(followingUserIds);
	    }
	    
	    
	    
	    
	    
	 // 팔로워 수 기준 추천 유저 5명 가져오기
	    @GetMapping("/api/recommended-users")
	    public ResponseEntity<List<UserDto>> getRecommendedUsers(@RequestParam("userId") int userId) {
	        List<UserDto> recommendedUsers = boardService.getRecommendedUsersByFollowers(userId);

	        if (recommendedUsers != null && !recommendedUsers.isEmpty()) {
	            return new ResponseEntity<>(recommendedUsers, HttpStatus.OK);
	        } else {
	            return new ResponseEntity<>(new ArrayList<>(), HttpStatus.OK);
	        }
	    }
	    
	    
	    
	    
	    @GetMapping("/api/boards/top-liked")
	    public List<BoardDto> getTopLikedBoards() {
	        return boardService.getTopLikedBoardsWithImages();
	    }
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	    
	   
	@GetMapping("api/with-details")
	public List<BoardDto> getAllBoardsWithDetails(@RequestParam(name = "userId", required = false, defaultValue = "2") int userId) 
	{
		return boardService.getAllBoardWithDetails(userId);
	
	}
	
}
