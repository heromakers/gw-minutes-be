package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.CodeModel;
import com.heromakers.app.minutes.service.CodeService;
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
public class CodeResolver {

    @Autowired
    private CodeService codeService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<CodeModel> codeList(@Argument String schTxt, @Argument Boolean useFlag, @Argument CodeModel codeParam) {
        Map param = new HashMap();
        if(schTxt != null && !schTxt.isEmpty()) param.put("schTxt", schTxt);
        if(useFlag != null) param.put("useFlag", useFlag);
        if(codeParam != null) {
            String parentCode = codeParam.getParentCode();
            if(parentCode != null && !parentCode.isEmpty()) param.put("parentCode", parentCode);
            String codeLabel = codeParam.getCodeLabel();
            if(codeLabel != null && !codeLabel.isEmpty()) param.put("codeLabel", codeLabel);
        }
        return codeService.getCodeList(param);
    }

    @QueryMapping
    public CodeModel codeById(@Argument Integer codeId) {
        return codeService.getCodeInfo(codeId);
    }

    @MutationMapping
    public Result insertCode(@Argument CodeModel codeParam) {
        Result result = new Result();
        CodeModel saved = codeService.insertCode(codeParam);
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
    public Result updateCode(@Argument Integer codeId, @Argument CodeModel codeParam) {
        Result result = new Result();
        codeParam.setCodeId(codeId);
        boolean isSuccess = codeService.updateCode(codeParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(codeId));
        }
        return result;
    }

    @MutationMapping
    public Result deleteCode(@Argument Integer codeId) {
        Result result = new Result();
        boolean isSuccess = codeService.deleteCode(codeId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(codeId));
        }
        return result;
    }
}
