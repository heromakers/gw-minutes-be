package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.service.FileService;
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
public class FileResolver {

    @Autowired
    private FileService fileService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<FileModel> fileList(@Argument FileModel fileParam) {
        Map param = new HashMap();
        if(fileParam != null) {
            String linkInfo= fileParam.getLinkInfo();
            if(linkInfo != null && !linkInfo.isEmpty()) param.put("linkInfo", linkInfo);
            Integer linkKey = fileParam.getLinkKey();
            if(linkKey != null) param.put("linkKey", linkKey);
        }
        return fileService.getFileList(param);
    }

    @QueryMapping
    public FileModel fileById(@Argument Integer fileId) {
        return fileService.getFileInfo(fileId);
    }

    @MutationMapping
    public Result uploadFile(@Argument FileModel fileParam) {
        Result result = new Result();
        FileModel saved = fileService.uploadS3File(fileParam);
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
    public Result deleteFile(@Argument Integer fileId) {
        Result result = new Result();
        boolean isSuccess = fileService.deleteFile(fileId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(fileId));
        }
        return result;
    }
}
