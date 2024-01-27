package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.NoticeModel;
import com.heromakers.app.minutes.service.NoticeService;
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
public class NoticeResolver {

    @Autowired
    private NoticeService noticeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<NoticeModel> noticeList(@Argument String schTxt, @Argument Boolean useFlag, @Argument NoticeModel noticeParam) {
        Map param = new HashMap();
        if(schTxt != null && !schTxt.isEmpty()) param.put("schTxt", schTxt);
        if(useFlag != null) param.put("useFlag", useFlag);
        if(noticeParam != null) {
            String noticeKind = noticeParam.getNoticeKind();
            if(noticeKind != null && !noticeKind.isEmpty()) param.put("noticeKind", noticeKind);
            String title = noticeParam.getTitle();
            if(title != null && !title.isEmpty()) param.put("title", title);
        }
        return noticeService.getNoticeList(param);
    }

    @QueryMapping
    public NoticeModel noticeById(@Argument Integer noticeId) {
        return noticeService.getNoticeInfo(noticeId);
    }

    @MutationMapping
    public Result insertNotice(@Argument NoticeModel noticeParam) {
        Result result = new Result();
        NoticeModel saved = noticeService.insertNotice(noticeParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            try {
                objectMapper.registerModule(new JavaTimeModule());
                result.setData(objectMapper.writeValueAsString(saved));
            } catch (JsonProcessingException je) {
                je.printStackTrace();
            }
        }
        return result;
    }

    @MutationMapping
    public Result updateNotice(@Argument Integer noticeId, @Argument NoticeModel noticeParam) {
        Result result = new Result();
        noticeParam.setNoticeId(noticeId);
        boolean isSuccess = noticeService.updateNotice(noticeParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(noticeId));
        }
        return result;
    }

    @MutationMapping
    public Result deleteNotice(@Argument Integer noticeId) {
        Result result = new Result();
        boolean isSuccess = noticeService.deleteNotice(noticeId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(noticeId));
        }
        return result;
    }
}
