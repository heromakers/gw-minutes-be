package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
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
    public Result getCodeList(@RequestParam Map param) {
        Result result = new Result();
        List<CodeModel> codeList = codeService.getCodeList(param);
        result.setData(codeList);
        return result;
    }

    @GetMapping("/{codeId}")
    public Result getCodeInfo(@PathVariable("codeId") Integer codeId) {
        Result result = new Result();
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
    public Result insertCode(@RequestBody CodeModel codeParam) {
        Result result = new Result();
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
    public Result updateCode(@RequestBody CodeModel codeParam) {
        Result result = new Result();
        boolean isSuccess = codeService.updateCode(codeParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{codeId}")
    public Result deleteCode(@PathVariable("codeId") Integer codeId) {
        Result result = new Result();
        boolean isSuccess = codeService.deleteCode(codeId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }
}
