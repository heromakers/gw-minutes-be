package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
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
    public Result getAccountList(@RequestParam Map param) {
        Result result = new Result();
        List<AccountModel> accountList = accountService.getAccountList(param);
        result.setData(accountList);
        return result;
    }

    @GetMapping("/{accountId}")
    public Result getAccountInfo(@PathVariable("accountId") Integer accountId) {
        Result result = new Result();
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
    public Result insertAccount(@RequestBody AccountModel accountParam) {
        Result result = new Result();
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
    public Result updateAccount(@RequestBody AccountModel accountParam) {
        Result result = new Result();
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
    public Result deleteAccount(@PathVariable("accountId") Integer accountId) {
        Result result = new Result();
        boolean isSuccess = accountService.deleteAccount(accountId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }
}
