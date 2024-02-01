package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.ApiResult;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.TalkModel;
import com.heromakers.app.minutes.service.TalkService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/talk")
@Slf4j
public class TalkController {
    @Autowired
    private TalkService talkService;

    @GetMapping
    public ApiResult getTalkList(@RequestParam Map<String, String> param) {
        ApiResult result = new ApiResult();
        String useFlag = param.get("useFlag");
        if(useFlag != null) {
            if("true".equalsIgnoreCase(useFlag)) param.put("useFlag", "1");
            else if("false".equalsIgnoreCase(useFlag)) param.put("useFlag", "0");
        }
        List<TalkModel> talkList = talkService.getTalkList(param);
        result.setData(talkList);
        return result;
    }

    @GetMapping("/{talkId}")
    public ApiResult getTalkInfo(@PathVariable("talkId") Integer talkId) {
        ApiResult result = new ApiResult();
        TalkModel talkModel = talkService.getTalkInfo(talkId);
        if(talkModel == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(talkModel);
        }
        return result;
    }

    @PostMapping
    public ApiResult insertTalk(@RequestBody TalkModel talkParam) {
        ApiResult result = new ApiResult();
        TalkModel saved = talkService.insertTalk(talkParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 저장하지 못했습니다.");
        } else {
            result.setData(saved);
        }
        return result;
    }

    @PutMapping
    public ApiResult updateTalk(@RequestBody TalkModel talkParam) {
        ApiResult result = new ApiResult();
        boolean isSuccess = talkService.updateTalk(talkParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{talkId}")
    public ApiResult deleteTalk(@PathVariable("talkId") Integer talkId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = talkService.deleteTalk(talkId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

}
