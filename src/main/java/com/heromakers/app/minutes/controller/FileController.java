package com.heromakers.app.minutes.controller;


import com.heromakers.app.minutes.common.ApiResult;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@Slf4j
public class FileController {
    @Autowired
    private FileService fileService;

    @GetMapping
    public ApiResult getFileList(@RequestParam Map param) {
        ApiResult result = new ApiResult();
        List<FileModel> fileList = fileService.getFileList(param);
        result.setData(fileList);
        return result;
    }

    @GetMapping("/{fileId}")
    public ApiResult getFileInfo(@PathVariable("fileId") Integer fileId) {
        ApiResult result = new ApiResult();
        FileModel fileModel = fileService.getFileInfo(fileId);
        if(fileModel == null){
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(fileModel);
        }
        return result;
    }

    @PostMapping
    public ApiResult uploadFile(@RequestBody FileModel fileParam) {
        ApiResult result = new ApiResult();
        FileModel saved = fileService.uploadS3File(fileParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("파일을 업로드하지 못했습니다.");
        } else {
            result.setData(saved);
        }
        return result;
    }

    @DeleteMapping("/{fileId}")
    public ApiResult deleteFile(@PathVariable("fileId") Integer fileId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = fileService.deleteFile(fileId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }
}