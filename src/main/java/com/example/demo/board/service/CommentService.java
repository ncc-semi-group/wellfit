package com.example.demo.board.service;

import com.example.demo.board.mapper.CommentMapper;
import com.example.demo.badge.service.UserBadgeService;
import com.example.demo.dto.board.CommentDto;
import com.example.demo.dto.user.UserBadgeDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentMapper commentMapper;
    private final UserBadgeService userBadgeService;

    @Autowired
    public CommentService(CommentMapper commentMapper, UserBadgeService userBadgeService) {
        this.commentMapper = commentMapper;
        this.userBadgeService = userBadgeService;
    }

    // 모든 댓글 조회
    public List<CommentDto> getAllComments() {
        return commentMapper.selectAllComments();
    }

    // 특정 게시판의 댓글 조회
    public List<CommentDto> getCommentsByBoardId(int boardId) {
        return commentMapper.selectCommentsByBoardId(boardId);
    }

    // 댓글 삽입
    public void addComment(CommentDto commentDto) {
        commentMapper.insertComment(commentDto);
        
        // 댓글 작성 후 뱃지 조건 체크
        checkAndUpdateCommentBadge(commentDto.getUserId());
    }

    // 댓글 수정
    public void updateComment(CommentDto commentDto) {
        commentMapper.updateComment(commentDto);
    }

    // 댓글 삭제
    public void deleteComment(int id) {
        commentMapper.deleteComment(id);
    }
    
	public List<CommentDto> getSelectUserId(int userId){
		return commentMapper.getSelectUserId(userId);
	}

    // 댓글 관련 뱃지 체크 및 업데이트
    private void checkAndUpdateCommentBadge(int userId) {
        // 사용자의 댓글 뱃지 정보 조회
        List<UserBadgeDto> userBadges = userBadgeService.getSelectUserId(userId);
        
        // 댓글 마스터 뱃지 찾기 (badge_id = 4)
        UserBadgeDto commentBadge = null;
        for (UserBadgeDto badge : userBadges) {
            if (badge.getBadgeId() == 4) { // 댓글 마스터 뱃지
                commentBadge = badge;
                break;
            }
        }
        
        // 댓글 마스터 뱃지가 없으면 새로 생성
        if (commentBadge == null) {
            userBadgeService.createUserBadge(userId, 4); // 4는 댓글 마스터 뱃지의 ID
            userBadges = userBadgeService.getSelectUserId(userId); // 새로 생성된 뱃지 정보 조회
            for (UserBadgeDto badge : userBadges) {
                if (badge.getBadgeId() == 4) {
                    commentBadge = badge;
                    break;
                }
            }
        }

        if (commentBadge != null) {
            // 기존 카운트에 1 추가
            int newCount = commentBadge.getCondition_count() + 1;
            commentBadge.setCondition_count(newCount);
            
            // 목표 달성 체크 (1000개 댓글 작성 시 뱃지 획득)
            if (newCount >= 1000 && commentBadge.getIsAchieved() == 0) {
                commentBadge.setIsAchieved(1);
            }
            
            // 뱃지 정보 업데이트
            userBadgeService.updateUserBadge(commentBadge);
        }
    }
}
