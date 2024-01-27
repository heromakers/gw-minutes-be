package com.heromakers.app.minutes.repository;

import com.heromakers.app.minutes.model.AccountModel;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<AccountModel, Integer> {
    Optional<AccountModel> findByAccountKeyAndUseFlag(String accountKey, Boolean useFlag);

    Optional<AccountModel> findBySnsKeyAndJoinTypeAndUseFlag(String snsKey, String joinType, Boolean useFlag);

    Optional<AccountModel> findByRefreshTokenAndUseFlag(String refreshToken, Boolean useFlag);

//    @Query(value = "UPDATE tb_account SET fcm_token = :fcmToken WHERE account_id = :accountId", nativeQuery = true)
//    void updateFcmToken(Integer accountId, String fcmToken);
}
