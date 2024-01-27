package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.common.AesCbc128Crypto;
import com.heromakers.app.minutes.mapper.AccountMapper;
import com.heromakers.app.minutes.model.AccountModel;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.repository.AccountRepository;
import com.heromakers.app.minutes.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
public class OpenService implements UserDetailsService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    private static final String PROFILE_IMAGE = "accountProfileImage";

    @Override
    public UserDetails loadUserByUsername(String accountKey) throws UsernameNotFoundException {
        return accountMapper.selectByAccountKey(accountKey);
    }

    public AccountModel login(String accountKey, String password, String fcmToken, String refreshToken) throws Exception {
        AccountModel accountModel = accountRepository.findByAccountKeyAndUseFlag(accountKey, true).orElse(null);
        if(accountModel == null) {
            return null;
        }

        if(!accountModel.getPassword().equals(AesCbc128Crypto.getInstance().encrypt(password))) {
            accountModel.setSignStatus("WrongPassword");
            return accountModel;
        }

        String joinType = accountModel.getJoinType();
        boolean isPassword6Month = false;
        if("user".equals(joinType)) {
            Instant passwordAt = accountModel.getPasswordAt();
            if(passwordAt == null) {
                accountModel.setSignStatus("ToUpdatePassword");
                return accountModel;
            }
            long passDuration = passwordAt.until(Instant.now(), ChronoUnit.HOURS);
            if(passDuration >= 6 * 30 * 24L) {
                isPassword6Month = true;
            }
        }
        Integer accountId = accountModel.getAccountId();
        if(fcmToken != null && !fcmToken.isEmpty()) {
            accountModel.setFcmToken(fcmToken);
        }
        if(refreshToken != null && !refreshToken.isEmpty()) {
            accountModel.setRefreshToken(refreshToken);
        }
        accountModel.setLoginAt(Instant.now());
        accountMapper.update(accountModel);

        fileRepository.findByLinkInfoAndLinkKey(PROFILE_IMAGE, accountId).ifPresent(accountModel::setProfileImage);
        if(isPassword6Month) {
            accountModel.setSignStatus("Password6Month");
        }

        return accountModel;
    }

    public AccountModel join(AccountModel accountParam) throws Exception {
        String accountKey = accountParam.getAccountKey();
        List<AccountModel> accountList = accountMapper.selectByKeys(accountParam);

        String rolesParam = accountParam.getRoles();
        if (rolesParam == null || rolesParam.isEmpty()) {
            rolesParam = "ROLE_USER";
            accountParam.setRoles(rolesParam);
        }
        String signStatus = "";
        if (accountList != null && !accountList.isEmpty()) {
            for (AccountModel accountModel : accountList) {
                if (accountModel.getAccountKey().equals(accountKey) && accountModel.getUseFlag()) {
                    signStatus = "AlreadyAccount";
                    break;
                }
            }

            String phoneParam = accountParam.getPhone();
            String emailParam = accountParam.getEmail();
            if (signStatus.isEmpty()) {
                for (AccountModel account : accountList) {
                    String loopRoles = account.getRoles();
                    if (phoneParam != null && phoneParam.equals(account.getPhone())) {
                        signStatus += "AlreadyPhone";
                        if (loopRoles.contains(rolesParam)) {
                            signStatus += "Role";
                        }
                    }
                    if ("AlreadyPhoneRole".equals(signStatus)) break;

                    if (emailParam != null && emailParam.equals(account.getEmail())) {
                        signStatus += "AlreadyEmail";
                        if (loopRoles.contains(rolesParam)) {
                            signStatus += "Role";
                        }
                    }
                    if ("AlreadyEmailRole".equals(signStatus)) break;
                }
            }
            if (signStatus.isEmpty()) {
                accountParam.setSignStatus("Success");
            }
        }
        accountParam.setSignStatus(signStatus);
        if ("AlreadyAccount".equals(signStatus)) {
            return accountParam;
        }
        if (signStatus.contains("AlreadyPhoneRole") || signStatus.contains("AlreadyEmailRole")) {
            return accountParam;
        }

        accountParam.setPassword(AesCbc128Crypto.getInstance().encrypt(accountParam.getPassword()));
        accountParam.setCreatedAt(Instant.now());
        accountParam.setUpdatedAt(Instant.now());
        accountParam.setPasswordAt(Instant.now());
        String joinType = accountParam.getJoinType();
        if (joinType == null || joinType.isEmpty()) {
            accountParam.setJoinType("user");
        }
//        if("ROLE_USER".equals(rolesParam)) accountParam.setApproved(true);

        AccountModel accountModel = accountRepository.save(accountParam);
        if(accountModel == null) {
            return null;
        }

        FileModel profileImage = accountParam.getProfileImage();
        if( profileImage != null && profileImage.getRealName() != null && !profileImage.getRealName().isEmpty()) {
            profileImage.setLinkInfo(PROFILE_IMAGE);
            profileImage.setLinkKey( accountModel.getAccountId());
            fileService.uploadS3File(profileImage);
        }

        return accountModel;
    }

    @Transactional
    public AccountModel authBySNS(AccountModel accountParam, String joinType) throws Exception {
        if(joinType == null || joinType.isEmpty()) {
            return null;
        }
        String snsKey = accountParam.getSnsKey();
        AccountModel accountModel = accountRepository.findBySnsKeyAndJoinTypeAndUseFlag(snsKey, joinType,true).orElse(null);

        if( accountModel != null ) {
            Integer accountId = accountModel.getAccountId();
            fileRepository.findByLinkInfoAndLinkKey(PROFILE_IMAGE, accountId).ifPresent(accountModel::setProfileImage);

            String humanName = accountParam.getHumanName();
            if(humanName != null && !humanName.isEmpty()) {
                accountModel.setHumanName(humanName);
            }
            String fcmToken = accountParam.getFcmToken();
            if(fcmToken != null && !fcmToken.isEmpty()) {
                accountModel.setFcmToken(fcmToken);
            }
            String refreshToken = accountParam.getRefreshToken();
            if(refreshToken != null && !refreshToken.isEmpty()) {
                accountModel.setRefreshToken(refreshToken);
            }
            accountModel.setLoginAt(Instant.now());
            accountMapper.update(accountModel);
//            accountRepository.save(accountModel);
        } else {
            accountParam.setJoinType(joinType);

            String accountKey = accountParam.getAccountKey();
            if(accountKey == null || accountKey.isEmpty()) {
                accountParam.setAccountKey(snsKey.length() > 30 ? snsKey.substring(0, 30) : snsKey);
            }

            String rolesParam = accountParam.getRoles();
            if(rolesParam == null || rolesParam.isEmpty()) {
                accountParam.setRoles("ROLE_USER");
            }
            accountParam.setPassword(snsKey);
            accountParam.setUseFlag(true);
            accountParam.setCreatedAt(Instant.now());
            accountParam.setUpdatedAt(Instant.now());
            accountParam.setLoginAt(Instant.now());
            accountParam.setPasswordAt(Instant.now());

            accountModel = accountRepository.save(accountParam);
            if(accountModel == null) {
                return null;
            }
            Integer accountId = accountModel.getAccountId();

            FileModel profileImage = accountParam.getProfileImage();
            if( profileImage != null && profileImage.getRealName() != null && !profileImage.getRealName().isEmpty() ) {
                profileImage.setLinkInfo(PROFILE_IMAGE);
                profileImage.setLinkKey(accountId);
                profileImage = fileService.uploadS3File(profileImage);
                accountModel.setProfileImage(profileImage);
            }
        }
        return accountModel;
    }

    public AccountModel refreshToken(String nowRefreshToken, String newRefreshToken) throws Exception {
        AccountModel accountVo = accountRepository.findByRefreshTokenAndUseFlag(nowRefreshToken, true).orElse(null);
        if(accountVo == null) {
            return null;
        }

        accountVo.setRefreshToken(newRefreshToken);
        accountMapper.update(accountVo);

        return accountVo;
    }

    public String findAccount(String phone, String email, String role) throws Exception {
        if (role == null || role.isEmpty()) {
            role = "ROLE_USER";
        }

        AccountModel accountParam = new AccountModel();
//        accountParam.setRoles(role);
        if(phone != null && !phone.isEmpty()) accountParam.setPhone(phone);
        if(email != null && !email.isEmpty()) accountParam.setEmail(email);

        List<AccountModel> accountList = accountMapper.selectByKeys(accountParam);
        if (accountList == null || accountList.isEmpty()) {
            return null;
        }
        String accountKey = "";
        String sentResult = "NotSent";
        for(AccountModel accountVo : accountList) {
            String loopRoles = accountVo.getRoles();
            if (phone != null && phone.equals(accountVo.getPhone()) && loopRoles.contains(role)) {
                accountKey = accountVo.getAccountKey();
                // SMS 전송
                sentResult = "SMSSent";
                break;
            }
            if (email != null && email.equals(accountVo.getEmail()) && loopRoles.contains(role)) {
                accountKey = accountVo.getAccountKey();
                // Mail 전송
                sentResult = "EmailSent";
                break;
            }
        }
        if (accountKey.isEmpty()) {
            return null;
        }

        if("NotSent".equals(sentResult)) {
            return "NotSent";
        }
        return sentResult + accountKey;
    }

    public String tempPassword(String accountKey, String phone, String email, String role) throws Exception {
        if (role == null || role.isEmpty()) {
            role = "ROLE_USER";
        }

        AccountModel accountVo = accountRepository.findByAccountKeyAndUseFlag(accountKey, true).orElse(null);
        if(accountVo == null) {
            return null;
        }
        String roles = accountVo.getRoles();
        if(!roles.contains(role)) {
            return null;
        }

        String tempPassword = String.valueOf(Math.round(Math.random() * 100)) + String.valueOf(System.currentTimeMillis()).substring(4);
        accountMapper.updatePassword(accountVo.getAccountId(), AesCbc128Crypto.getInstance().encrypt(tempPassword), "Y");

        String sentResult = "NotSent";
        if (phone != null && phone.equals(accountVo.getPhone()) ) {
            // SMS 전송
            sentResult = "SMSSent";
        } else if (email != null && email.equals(accountVo.getEmail())) {
            // Mail 전송
            sentResult = "EmailSent";
        }

        if("NotSent".equals(sentResult)) {
            return "NotSent";
        }
        return sentResult + tempPassword;
    }

}
