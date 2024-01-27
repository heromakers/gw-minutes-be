package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.TalkModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkRepository extends CrudRepository<TalkModel, Integer> {
    List<TalkModel> findAllByTalkKindOrderByTalkIdDesc(String talkKind);

    List<TalkModel> findAllByWriterIdOrderByTalkIdDesc(Integer writerId);
}
