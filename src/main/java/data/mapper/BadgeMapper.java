package data.mapper;

import org.apache.ibatis.annotations.Mapper;
import data.dto.BadgeDto;
import java.util.List;

@Mapper
public interface BadgeMapper {
    public List<BadgeDto> getAllBadges();
} 