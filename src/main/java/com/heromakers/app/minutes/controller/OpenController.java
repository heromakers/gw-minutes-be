package com.heromakers.app.minutes.controller;

import com.heromakers.app.minutes.common.Result;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.AccountModel;
import com.heromakers.app.minutes.security.JwtTokenProvider;
import com.heromakers.app.minutes.service.OpenService;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@ApiOperation(value = "인증 전 또는 JWT 없이 접근 가능한 API", notes = "로그인, 회원 가입, SNS 로그인, 계정 찾기, 임시 비밀번호 발급")
@Slf4j
public class OpenController {
    @Autowired
    private OpenService openService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public Result login(@RequestBody AccountModel accountParam, HttpServletRequestWrapper request) {
        Result result = new Result();
        try {
            String accountKey = accountParam.getAccountKey();
            String password = accountParam.getPassword();
            if(accountKey == null || password == null || password.length() < 4) {
                result.setStatus(ResultStatus.fail);
                result.setReason("InvalidParam");
                result.setMessage("로그인 정보가 적절하지 않습니다.");
                return result;
            }
            String fcmToken = accountParam.getFcmToken();
            String refreshToken = jwtTokenProvider.generateRefreshToken();

            AccountModel accountModel = openService.login(accountKey, password, fcmToken, refreshToken);
            if(accountModel == null) {
                result.setStatus(ResultStatus.fail);
                result.setReason("NoAccountMatched");
                result.setMessage("로그인 정보가 적절하지 않습니다.");
                return result;
            }
            String signStatus = accountModel.getSignStatus();
            if("ToUpdatePassword".equals(signStatus)) {
                result.setStatus(ResultStatus.fail);
                result.setReason("ToUpdatePassword");
                result.setMessage("비밀번호를 수정해야 합니다.");
                return result;
            }
            if("Password6Month".equals(signStatus)) {
                result.setReason("Password6Month");
                result.setMessage("비밀번호를 수정한지 6개월이 지났습니다.");
            } else if(!"Success".equals(signStatus)) {
                result.setStatus(ResultStatus.fail);
                result.setReason(signStatus);
                result.setMessage("로그인 정보가 적절하지 않습니다.");
                return result;
            }

            String authToken = "";
            String[] roles = accountModel.getRoles().split(",");
            if(roles.length > 0) {
                List<String> roleList = Arrays.asList(roles);
                authToken = jwtTokenProvider.generateAuthToken(accountModel.getAccountKey(), roleList);
                result.setAuthToken(authToken);
            }
            result.setRefreshToken(refreshToken);
            result.setData(accountModel);

            request.getSession().setAttribute("loginedUser", accountModel);
            request.getSession().setAttribute("authToken", authToken);
            request.getSession().setAttribute("roles", accountModel.getRoles());

        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }

        return result;
    }

    @DeleteMapping("/logout")
    public Result logout(HttpServletRequestWrapper request) {
        Result result = new Result();
        request.getSession().invalidate();
        result.setMessage("로그아웃 되었습니다.");
        return result;
    }

    @PostMapping("/join")
    public Result join(@RequestBody AccountModel accountParam) {
        Result result = new Result();
        try {
            String accountKey = accountParam.getAccountKey();
            String password = accountParam.getPassword();
            if (accountKey == null || password == null || password.length() < 4) {
                result.setStatus(ResultStatus.fail);
                result.setReason("InvalidInfo");
                result.setMessage("가입 정보가 적절하지 않습니다.");
                return result;
            }

            AccountModel accountModel = openService.join(accountParam);
            if(accountModel == null) {
                result.setStatus(ResultStatus.fail);
                result.setReason("SaveFailed");
                result.setMessage("계정이 생성되지 않았습니다.");
                return result;
            }

            String signStatus = accountModel.getSignStatus();
            result.setReason(signStatus);
            if ("AlreadyAccount".equals(signStatus)) {
                result.setStatus(ResultStatus.fail);
                result.setMessage("이미 사용되는 계정입니다.");
                return result;
            }
            if (signStatus.contains("AlreadyPhoneRole") || signStatus.contains("AlreadyEmailRole")) {
                result.setStatus(ResultStatus.fail);
                result.setMessage("이미 사용되는 정보입니다.");
                return result;
            }
            result.setData(accountModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }

        return result;
    }

    @PostMapping("/google")
    @ApiOperation(value = "Login or Join via Google", notes = "Google 계정으로 Login 체크 후 없으면 Join 후 Account 반환")
    public Result google(@RequestBody AccountModel accountParam) {
        return this.authBySNS(accountParam, "google");
    }

    @PostMapping("/kakao")
    @ApiOperation(value = "Login or Join via Kakao", notes = "Kakao 계정으로 Login 체크 후 없으면 Join 후 Account 반환")
    public Result kakao(@RequestBody AccountModel accountParam) {
        return this.authBySNS(accountParam, "kakao");
    }

    @PostMapping("/refresh-token")
    public Result refreshToken(@RequestBody String refreshToken) {
        Result result = new Result();
        try {
            ResultStatus checkRefreshToken = jwtTokenProvider.validateToken(refreshToken);
            if(!ResultStatus.success.equals(checkRefreshToken) ) {
                result.setStatus(checkRefreshToken);
                result.setReason("InvalidRefreshToken");
                result.setMessage("인증이 만료되었습니다.");
                return result;
            }

            String newRefreshToken = jwtTokenProvider.generateRefreshToken();
            AccountModel accountModel = openService.refreshToken(refreshToken, newRefreshToken);
            if(accountModel == null) {
                result.setStatus(ResultStatus.fail);
                result.setReason("AccountNotFound");
                result.setMessage("잘못된 요청입니다.");
                return result;
            }

            String[] roles = accountModel.getRoles().split(",");
            if(roles.length > 0) {
                List<String> roleList = Arrays.asList(roles);
                result.setAuthToken(jwtTokenProvider.generateAuthToken(accountModel.getAccountKey(), roleList));
            }
            result.setRefreshToken(newRefreshToken);
            result.setData(accountModel);
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @PostMapping("/find-account")
    public Result findAccount(@RequestBody Map param) {
        Result result = new Result();

        try {
            String phone = (String) param.get("phone");
            String email = (String) param.get("email");
            if ((phone == null || phone.length() < 8) && (email == null || email.length() < 8))  {
                result.setStatus(ResultStatus.fail);
                result.setReason("InvalidParam");
                result.setMessage("잘못된 요청입니다.");
                return result;
            }
            String role = (String) param.get("role");
            String accountKey = openService.findAccount(phone, email, role);

            if(accountKey == null || accountKey.isEmpty()) {
                result.setStatus(ResultStatus.fail);
                result.setReason("AccountNotFound");
                result.setMessage("해당하는 계정이 없습니다.");
                return result;
            }
            if("NotSent".equals(accountKey)) {
                result.setStatus(ResultStatus.fail);
                result.setReason("NotSent");
                result.setMessage("계정 발송에 실패했습니다.");
                return result;
            }
            if(accountKey.startsWith("SMSSent")) {
                result.setMessage("계정을 문자로 전송했습니다.");
                result.setData(accountKey.substring(7));
            } else if(accountKey.startsWith("EmailSent")) {
                result.setMessage("계정을 이메일로 전송했습니다.");
                result.setData(accountKey.substring(9));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    @PostMapping("/temp-password")
    public Result tempPassword(@RequestBody Map param)  {
        Result result = new Result();

        try {
            String accountKey = (String) param.get("accountKey");
            if(accountKey == null || accountKey.isEmpty()) {
                result.setStatus(ResultStatus.fail);
                result.setReason("NoAccountKey");
                result.setMessage("계정을 입력해 주세요.");
                return result;
            }
            String phone = (String) param.get("phone");
            String email = (String) param.get("email");
            if ((phone == null || phone.length() < 8) && (email == null || email.length() < 8))  {
                result.setStatus(ResultStatus.fail);
                result.setReason("InvalidParam");
                result.setMessage("잘못된 요청입니다.");
                return result;
            }
            String role = (String) param.get("role");
            String password = openService.tempPassword(accountKey, phone, email, role);

            if(password == null || password.isEmpty()) {
                result.setStatus(ResultStatus.fail);
                result.setReason("AccountNotFound");
                result.setMessage("해당하는 계정이 없습니다.");
                return result;
            }
            if("NotSent".equals(password)) {
                result.setStatus(ResultStatus.fail);
                result.setReason("NotSent");
                result.setMessage("임시 비밀번호 발송에 실패했습니다.");
                return result;
            }
            if(password.startsWith("SMSSent")) {
                result.setMessage("임시 비밀번호를 문자로 전송했습니다.");
                result.setData(password.substring(7));
            } else if(password.startsWith("EmailSent")) {
                result.setMessage("임시 비밀번호를 이메일로 전송했습니다.");
                result.setData(password.substring(9));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }
        return result;
    }

    private Result authBySNS(AccountModel accountParam, String joinType) {
        Result result = new Result();
        try {
            String snsKey = accountParam.getSnsKey();
            if(snsKey == null || snsKey.isEmpty() || joinType == null || joinType.isEmpty()) {
                result.setStatus(ResultStatus.fail);
                result.setReason("InvalidSnsKey");
                result.setMessage("잘못된 요청입니다.");
                return result;
            }
            String refreshToken = jwtTokenProvider.generateRefreshToken();
            accountParam.setRefreshToken(refreshToken);

            AccountModel accountModel = openService.authBySNS(accountParam, joinType);
            if(accountModel == null) {
                result.setStatus(ResultStatus.fail);
                result.setReason("SaveFailed");
                result.setMessage("계정이 생성되지 않았습니다.");
                return result;
            }

            String[] roles = accountModel.getRoles().split(",");
            if(roles != null && roles.length > 0) {
                List<String> roleList = Arrays.asList(roles);
                result.setAuthToken(jwtTokenProvider.generateAuthToken(accountModel.getAccountKey(), roleList));
            }
            result.setRefreshToken(refreshToken);
            result.setData(accountModel);

        } catch (Exception ex) {
            ex.printStackTrace();
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage("에러가 발생하였습니다.");
        }

        return result;
    }

}
