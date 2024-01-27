package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.NoticeModel;
import com.heromakers.app.minutes.service.NoticeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
@Slf4j
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    @GetMapping
    public Result getNoticeList(@RequestParam Map param) {
        Result result = new Result();
        List<NoticeModel> noticeList = noticeService.getNoticeList(param);
        result.setData(noticeList);
        return result;
    }

    @GetMapping("/{noticeId}")
    public Result getNoticeInfo(@PathVariable("noticeId") Integer noticeId) {
        Result result = new Result();
        NoticeModel noticeModel = noticeService.getNoticeInfo(noticeId);
        if(noticeModel == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(noticeModel);
        }
        return result;
    }

    @PostMapping
    public Result insertNotice(@RequestBody NoticeModel noticeParam) {
        Result result = new Result();
        NoticeModel saved = noticeService.insertNotice(noticeParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 저장하지 못했습니다.");
        } else {
            result.setData(saved);
        }
        return result;
    }

    @PutMapping
    public Result updateNotice(@RequestBody NoticeModel noticeParam) {
        Result result = new Result();
        boolean isSuccess = noticeService.updateNotice(noticeParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{noticeId}")
    public Result deleteNotice(@PathVariable("noticeId") Integer noticeId) {
        Result result = new Result();
        boolean isSuccess = noticeService.deleteNotice(noticeId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

}
