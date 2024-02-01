package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.ApiResult;
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
    public ApiResult getInfoPopupList(@RequestParam Map<String, String> param) {
        ApiResult result = new ApiResult();
        String useFlag = param.get("useFlag");
        if(useFlag != null) {
            if("true".equalsIgnoreCase(useFlag)) param.put("useFlag", "1");
            else if("false".equalsIgnoreCase(useFlag)) param.put("useFlag", "0");
        }
        List<InfoPopupModel> popupList = infoPopupService.getInfoPopupList(param);
        result.setData(popupList);
        return result;
    }

    @GetMapping("/{seq}")
    public ApiResult getInfoPopupInfo(@PathVariable("seq") Integer seq) {
        ApiResult result = new ApiResult();
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
    public ApiResult insertInfoPopup(@RequestBody InfoPopupModel popupParam) {
        ApiResult result = new ApiResult();
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
    public ApiResult updateInfoPopup(@RequestBody InfoPopupModel popupParam) {
        ApiResult result = new ApiResult();
        boolean isSuccess = infoPopupService.updateInfoPopup(popupParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{seq}")
    public ApiResult deleteInfoPopup(@PathVariable("seq") Integer seq) {
        ApiResult result = new ApiResult();
        boolean isSuccess = infoPopupService.deleteInfoPopup(seq);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

}
