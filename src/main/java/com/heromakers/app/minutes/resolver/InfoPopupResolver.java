package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.InfoPopupModel;
import com.heromakers.app.minutes.service.InfoPopupService;
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
public class InfoPopupResolver {

    @Autowired
    private InfoPopupService infoPopupService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<InfoPopupModel> infoPopupList(@Argument String isActive, @Argument String schTxt, @Argument Boolean showFlag, @Argument InfoPopupModel infoPopupParam) {
        Map<String, Object> param = new HashMap<>();
        if(isActive != null && !isActive.isEmpty()) param.put("isActive", isActive);
        if(schTxt != null && !schTxt.isEmpty()) param.put("schTxt", schTxt);
        if(showFlag != null) param.put("showFlag", showFlag);
        if(infoPopupParam != null) {
            String kind = infoPopupParam.getKind();
            if(kind != null && !kind.isEmpty()) param.put("kind", kind);
            String title = infoPopupParam.getTitle();
            if(title != null && !title.isEmpty()) param.put("title", title);
        }
        return infoPopupService.getInfoPopupList(param);
    }

    @QueryMapping
    public InfoPopupModel infoPopupById(@Argument Integer seq) {
        return infoPopupService.getInfoPopupInfo(seq);
    }

    @MutationMapping
    public Result insertInfoPopup(@Argument InfoPopupModel infoPopupParam) {
        Result result = new Result();
        try {
            InfoPopupModel saved = infoPopupService.insertInfoPopup(infoPopupParam);
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
    public Result updateInfoPopup(@Argument Integer seq, @Argument InfoPopupModel infoPopupParam) {
        Result result = new Result();
        infoPopupParam.setSeq(seq);
        boolean isSuccess = infoPopupService.updateInfoPopup(infoPopupParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(seq));
        }
        return result;
    }

    @MutationMapping
    public Result deleteInfoPopup(@Argument Integer seq) {
        Result result = new Result();
        boolean isSuccess = infoPopupService.deleteInfoPopup(seq);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(seq));
        }
        return result;
    }
}
