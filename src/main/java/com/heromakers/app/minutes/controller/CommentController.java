package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.CommentModel;
import com.heromakers.app.minutes.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping
    public Result getCommentList(@RequestParam Map param) {
        Result result = new Result();
        List<CommentModel> commentList = commentService.getCommentList(param);
        result.setData(commentList);
        return result;
    }

    @GetMapping("/{commentId}")
    public Result getCommentInfo(@PathVariable("commentId") Integer commentId) {
        Result result = new Result();
        CommentModel commentModel = commentService.getCommentInfo(commentId);
        if(commentModel == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(commentModel);
        }
        return result;
    }

    @PostMapping
    public Result insertComment(@RequestBody CommentModel commentParam) {
        Result result = new Result();
        CommentModel saved = commentService.insertComment(commentParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 저장하지 못했습니다.");
        } else {
            result.setData(saved);
        }
        return result;
    }

    @PutMapping
    public Result updateComment(@RequestBody CommentModel commentParam) {
        Result result = new Result();
        boolean isSuccess = commentService.updateComment(commentParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable("commentId") Integer commentId) {
        Result result = new Result();
        boolean isSuccess = commentService.deleteComment(commentId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

}
