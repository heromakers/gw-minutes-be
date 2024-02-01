package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.ApiResult;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.CodeModel;
import com.heromakers.app.minutes.service.CodeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/code")
@Slf4j
public class CodeController {
    @Autowired
    private CodeService codeService;

    @GetMapping
    public ApiResult getCodeList(@RequestParam Map param) {
        ApiResult result = new ApiResult();
        List<CodeModel> codeList = codeService.getCodeList(param);
        result.setData(codeList);
        return result;
    }

    @GetMapping("/{codeId}")
    public ApiResult getCodeInfo(@PathVariable("codeId") Integer codeId) {
        ApiResult result = new ApiResult();
        CodeModel codeModel = codeService.getCodeInfo(codeId);
        if(codeModel == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(codeModel);
        }
        return result;
    }

    @PostMapping
    public ApiResult insertCode(@RequestBody CodeModel codeParam) {
        ApiResult result = new ApiResult();
        CodeModel saved = codeService.insertCode(codeParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 저장하지 못했습니다.");
        } else {
            result.setData(saved);
        }
        return result;
    }

    @PutMapping
    public ApiResult updateCode(@RequestBody CodeModel codeParam) {
        ApiResult result = new ApiResult();
        boolean isSuccess = codeService.updateCode(codeParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{codeId}")
    public ApiResult deleteCode(@PathVariable("codeId") Integer codeId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = codeService.deleteCode(codeId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }
}
