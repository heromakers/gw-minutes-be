package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.ApiResult;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.TalkModel;
import com.heromakers.app.minutes.service.TalkService;
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
public class TalkResolver {

    @Autowired
    private TalkService talkService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<TalkModel> talkList(@Argument String schTxt, @Argument Boolean useFlag, @Argument TalkModel talkParam) {
        Map<String, Object> param = new HashMap<>();
        if(schTxt != null && !schTxt.isEmpty()) param.put("schTxt", schTxt);
        if(useFlag != null) param.put("useFlag", useFlag);
        if(talkParam != null) {
            String talkKind = talkParam.getTalkKind();
            if(talkKind != null && !talkKind.isEmpty()) param.put("talkKind", talkKind);
            Integer writerId = talkParam.getWriterId();
            if(writerId != null) param.put("writerId", writerId);
            String title = talkParam.getTitle();
            if(title != null && !title.isEmpty()) param.put("title", title);
        }
        return talkService.getTalkList(param);
    }

    @QueryMapping
    public TalkModel talkById(@Argument Integer talkId) {
        return talkService.getTalkInfo(talkId);
    }

    @MutationMapping
    public ApiResult insertTalk(@Argument TalkModel talkParam) {
        ApiResult result = new ApiResult();
        try {
            TalkModel saved = talkService.insertTalk(talkParam);
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
    public ApiResult updateTalk(@Argument Integer talkId, @Argument TalkModel talkParam) {
        ApiResult result = new ApiResult();
        talkParam.setTalkId(talkId);
        boolean isSuccess = talkService.updateTalk(talkParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(talkId));
        }
        return result;
    }

    @MutationMapping
    public ApiResult deleteTalk(@Argument Integer talkId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = talkService.deleteTalk(talkId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(talkId));
        }
        return result;
    }
}
