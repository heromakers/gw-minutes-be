package com.heromakers.app.minutes.mapper;

import com.heromakers.app.minutes.model.NoticeModel;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeMapper extends BaseMapper<NoticeModel, Integer> {
    public int increaseReadCount(Integer noticeId);
}
