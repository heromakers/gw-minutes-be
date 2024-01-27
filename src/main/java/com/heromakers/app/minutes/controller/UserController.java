package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.AccountModel;
import com.heromakers.app.minutes.service.AccountService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {
    @Autowired
    private AccountService accountService;

    @PutMapping("/my-info")
    public Result updateAccount(@AuthenticationPrincipal UserDetails details, @RequestBody AccountModel accountParam) {
        Result result = new Result();
        AccountModel loginedUser = (AccountModel)details;
        accountParam.setAccountId(loginedUser.getAccountId());
        boolean isSuccess = accountService.updateAccount(accountParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @PutMapping("/reset-password")
    public Result resetPassword(@AuthenticationPrincipal UserDetails details, @RequestBody String password) {
        Result result = new Result();
        try {
            AccountModel loginedUser = (AccountModel)details;
            if (password == null || password.length() < 4) {
                result.setStatus(ResultStatus.fail);
                result.setReason("InvalidInfo");
                result.setMessage("입력 정보가 적절하지 않습니다.");
                return result;
            }

            boolean isSuccess = accountService.resetPassword(loginedUser.getAccountId(), password);

            if(!isSuccess) {
                result.setStatus(ResultStatus.fail);
                result.setReason("AccountNotFound");
                result.setMessage("해당하는 계정이 없습니다.");
                return result;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @PutMapping("/fcm-token")
    @ApiOperation(value = "fcm-token 수정", notes = "fcm 메세지 수신을 위한 token 수정")
    public Result updateFcmToken(@AuthenticationPrincipal UserDetails details, @RequestBody String fcmToken) {
        Result result = new Result();
        AccountModel loginedUser = (AccountModel)details;
        boolean isSuccess = accountService.updateFcmToken(loginedUser.getAccountId(), fcmToken);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @DeleteMapping("/leave")
    public Result leave(@AuthenticationPrincipal UserDetails details, @RequestBody String reason) {
        Result result = new Result();
        AccountModel loginedUser = (AccountModel)details;
        boolean isSuccess = accountService.leave(loginedUser.getAccountId(), reason);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }
}
