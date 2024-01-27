package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.NoticeModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepository extends CrudRepository<NoticeModel, Integer> {
    List<NoticeModel> findAllByNoticeKindOrderByNoticeIdDesc(String noticeKind);
}
