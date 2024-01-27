package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.InfoPopupModel;
import com.heromakers.app.minutes.service.InfoPopupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/popup")
@Slf4j
public class InfoPopupController {
    @Autowired
    private InfoPopupService infoPopupService;

    @GetMapping
    public Result getInfoPopupList(@RequestParam Map param) {
        Result result = new Result();
        List<InfoPopupModel> popupList = infoPopupService.getInfoPopupList(param);
        result.setData(popupList);
        return result;
    }

    @GetMapping("/{seq}")
    public Result getInfoPopupInfo(@PathVariable("seq") Integer seq) {
        Result result = new Result();
        InfoPopupModel popupModel = infoPopupService.getInfoPopupInfo(seq);
        if(popupModel == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(popupModel);
        }
        return result;
    }

    @PostMapping
    public Result insertInfoPopup(@RequestBody InfoPopupModel popupParam) {
        Result result = new Result();
        InfoPopupModel saved = infoPopupService.insertInfoPopup(popupParam);
        if(saved == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 저장하지 못했습니다.");
        } else {
            result.setData(saved);
        }
        return result;
    }

    @PutMapping
    public Result updateInfoPopup(@RequestBody InfoPopupModel popupParam) {
        Result result = new Result();
        boolean isSuccess = infoPopupService.updateInfoPopup(popupParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{seq}")
    public Result deleteInfoPopup(@PathVariable("seq") Integer seq) {
        Result result = new Result();
        boolean isSuccess = infoPopupService.deleteInfoPopup(seq);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

}
