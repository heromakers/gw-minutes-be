package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.CommentModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends CrudRepository<CommentModel, Integer> {
    List<CommentModel> findAllByWriterIdOrderByCommentIdDesc(Integer writerId);
}
