package com.heromakers.app.minutes.mapper;

import com.heromakers.app.minutes.model.AccountModel;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountMapper extends BaseMapper<AccountModel, Integer> {
    public List<AccountModel> selectByKeys(AccountModel accountVo);

    public AccountModel selectByAccountKey(String account);

    public AccountModel selectByPhone(String phone);

    public AccountModel selectByEmail(String email);

    public int updateLoginAt(Integer accountId);

    public int updatePassword(Integer accountId, String password, String isTempPass);

    public int updateFcmToken(Integer accountId, String fcmToken);

    public int leave(Integer accountId, String reason);
}