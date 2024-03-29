package com.heromakers.app.minutes.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.heromakers.app.minutes.common.ApiResult;
import com.heromakers.app.minutes.common.ResultStatus;
import com.heromakers.app.minutes.model.AccountModel;
import com.heromakers.app.minutes.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class AccountResolver {

    @Autowired
    private AccountService accountService;

    @Autowired
    private HttpServletRequest request;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @QueryMapping
    public List<AccountModel> accountList(@Argument String schTxt, @Argument Boolean useFlag, @Argument AccountModel accountParam) {
        log.info("++++ token :: " + request.getHeader("X-AUTH-TOKEN"));

        Map<String, Object> param = new HashMap<>();
        if(schTxt != null && !schTxt.isEmpty()) param.put("schTxt", schTxt);
        if(useFlag != null) param.put("useFlag", useFlag ? 1 : 0);
        if(accountParam != null) {
            String humanName = accountParam.getHumanName();
            if(humanName != null && !humanName.isEmpty()) param.put("humanName", humanName);
            String joinType = accountParam.getJoinType();
            if(joinType != null && !joinType.isEmpty()) param.put("joinType", joinType);
        }
        return accountService.getAccountList(param);
    }

    @QueryMapping
//    @PreAuthorize("@webSecurity.checkAuthority('ROLE_ADMIN,ROLE_USER', #env)")
    public AccountModel accountById(@Argument Integer accountId) {
        return accountService.getAccountInfo(accountId);
    }

    @MutationMapping
    public ApiResult insertAccount(@Argument AccountModel accountParam, @Argument String password) {
        ApiResult result = new ApiResult();
        try {
            accountParam.setPassword(password);
            AccountModel saved = accountService.insertAccount(accountParam);
            if(saved == null) {
                result.setStatus(ResultStatus.fail);
                result.setMessage("데이터를 저장하지 못했습니다.");
            } else {
                objectMapper.registerModule(new JavaTimeModule());
                result.setData(objectMapper.writeValueAsString(saved));
            }
        } catch (Exception e) {
            result.setStatus(ResultStatus.error);
            result.setReason("Exception");
            result.setMessage(e.getMessage());
        }

        return result;
    }

    @MutationMapping
    public ApiResult updateAccount(@Argument Integer accountId, @Argument AccountModel accountParam) {
        ApiResult result = new ApiResult();
        accountParam.setAccountId(accountId);
        boolean isSuccess = accountService.updateAccount(accountParam);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(accountId));
        }
        return result;
    }

    @MutationMapping
    public ApiResult deleteAccount(@Argument Integer accountId) {
        ApiResult result = new ApiResult();
        boolean isSuccess = accountService.deleteAccount(accountId);
        if(!isSuccess) {
            result.setStatus(ResultStatus.error);
            result.setMessage("에러가 발생하였습니다.");
        } else {
            result.setData(String.valueOf(accountId));
        }
        return result;
    }
}
