package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.mapper.CommentMapper;
import com.heromakers.app.minutes.model.CommentModel;
import com.heromakers.app.minutes.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    public List<CommentModel> getCommentList(Map param) {
        return commentMapper.queryList(param);
    }

    public CommentModel getCommentInfo(Integer commentId) {
        CommentModel commentModel = commentRepository.findById(commentId).orElse(null);
        return commentModel;
    }

    public CommentModel insertComment(CommentModel commentParam) {
        commentParam.setCreatedAt(Instant.now());
        commentParam.setUpdatedAt(Instant.now());
        return commentRepository.save(commentParam);
    }

    public boolean updateComment(CommentModel commentParam) {
        commentMapper.update(commentParam);
        return true;
    }

    public boolean deleteComment(Integer commentId) {
        commentMapper.deleteFlag(commentId);
        return true;
    }
}