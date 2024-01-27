package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.CodeModel;
import com.heromakers.app.minutes.model.CommentModel;
import com.heromakers.app.minutes.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class CommentResolver {

    @Autowired
    private CommentService commentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<CommentModel> commentList(@Argument String schTxt, @Argument Boolean useFlag, @Argument CommentModel commentParam) {
        Map<String, Object> param = new HashMap<>();
        if(schTxt != null && !schTxt.isEmpty()) param.put("schTxt", schTxt);
        if(useFlag != null) param.put("useFlag", useFlag);
        if(commentParam != null) {
            String parentKind = commentParam.getParentKind();
            if(parentKind != null && !parentKind.isEmpty()) param.put("parentKind", parentKind);
            Integer parentId = commentParam.getParentId();
            if(parentId != null) param.put("parentId", parentId);
            Integer writerId = commentParam.getWriterId();
            if(writerId != null) param.put("writerId", writerId);
            String contents = commentParam.getContents();
            if(contents != null && !contents.isEmpty()) param.put("contents", contents);
        }
        return commentService.getCommentList(param);
    }

    @QueryMapping
    public CommentModel commentById(@Argument Integer commentId) {
        return commentService.getCommentInfo(commentId);
    }

    @MutationMapping
    public Result insertComment(@Argument CommentModel commentParam) {
        Result result = new Result();
        try {
            CommentModel saved = commentService.insertComment(commentParam);
            if(saved == null) {
                result.setStatus(ResultStatus.fail);
                result.setMessage("데이터를 저장하지 못했습니다.");
            } else {
                objectMapper.registerModule(new JavaTimeModule());
                result.setData(objectMapper.writeValueAsString(saved));
            }
        } catch (Exception e) {
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage(e.getMessage());
        }
        return result;
    }

    @MutationMapping
    public Result updateComment(@Argument Integer commentId, @Argument CommentModel commentParam) {
        Result result = new Result();
        commentParam.setCommentId(commentId);
        boolean isSuccess = commentService.updateComment(commentParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(commentId));
        }
        return result;
    }

    @MutationMapping
    public Result deleteComment(@Argument Integer commentId) {
        Result result = new Result();
        boolean isSuccess = commentService.deleteComment(commentId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(commentId));
        }
        return result;
    }
}
