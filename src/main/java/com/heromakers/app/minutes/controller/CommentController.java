package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.ApiResult;
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
    public ApiResult getCommentList(@RequestParam Map<String, String> param) {
        ApiResult result = new ApiResult();
        String useFlag = param.get("useFlag");
        if(useFlag != null) {
            if("true".equalsIgnoreCase(useFlag)) param.put("useFlag", "1");
            else if("false".equalsIgnoreCase(useFlag)) param.put("useFlag", "0");
        }
        List<CommentModel> commentList = commentService.getCommentList(param);
        result.setData(commentList);
        return result;
    }

    @GetMapping("/{commentId}")
    public ApiResult getCommentInfo(@PathVariable("commentId") Integer commentId) {
        ApiResult result = new ApiResult();
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
    public ApiResult insertComment(@RequestBody CommentModel commentParam) {
        ApiResult result = new ApiResult();
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
    public ApiResult updateComment(@RequestBody CommentModel commentParam) {
        ApiResult result = new ApiResult();
        boolean isSuccess = commentService.updateComment(commentParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{commentId}")
    public ApiResult deleteComment(@PathVariable("commentId") Integer commentId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = commentService.deleteComment(commentId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

}
