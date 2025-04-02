package data.service;

import java.util.List;
import org.springframework.stereotype.Service;
import data.dto.BadgeDto;
import data.mapper.BadgeMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeMapper badgeMapper;
    
    public List<BadgeDto> getAllBadges() {
        return badgeMapper.getAllBadges();
    }
} 