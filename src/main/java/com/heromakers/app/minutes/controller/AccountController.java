package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.ApiResult;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.AccountModel;
import com.heromakers.app.minutes.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
@Slf4j
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping
    public ApiResult getAccountList(@RequestParam Map<String, String> param) {
        ApiResult result = new ApiResult();
        String useFlag = param.get("useFlag");
        if(useFlag != null) {
            if("true".equalsIgnoreCase(useFlag)) param.put("useFlag", "1");
            else if("false".equalsIgnoreCase(useFlag)) param.put("useFlag", "0");
        }
        List<AccountModel> accountList = accountService.getAccountList(param);
        result.setData(accountList);
        return result;
    }

    @GetMapping("/{accountId}")
    public ApiResult getAccountInfo(@PathVariable("accountId") Integer accountId) {
        ApiResult result = new ApiResult();
        AccountModel accountModel = accountService.getAccountInfo(accountId);
        if(accountModel == null) {
            result.setStatus(ResultStatus.fail);
            result.setMessage("데이터를 조회하지 못했습니다.");
        } else {
            result.setData(accountModel);
        }
        return result;
    }

    @PostMapping
    public ApiResult insertAccount(@RequestBody AccountModel accountParam) {
        ApiResult result = new ApiResult();
        try {
            AccountModel saved = accountService.insertAccount(accountParam);
            if(saved == null) {
                result.setStatus(ResultStatus.fail);
                result.setMessage("데이터를 저장하지 못했습니다.");
            } else {
                result.setData(saved);
            }
        } catch (Exception e) {
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @PutMapping
    public ApiResult updateAccount(@RequestBody AccountModel accountParam) {
        ApiResult result = new ApiResult();
        try {
            accountService.updateAccount(accountParam);
        } catch (Exception e) {
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/{accountId}")
    public ApiResult deleteAccount(@PathVariable("accountId") Integer accountId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = accountService.deleteAccount(accountId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }
}
