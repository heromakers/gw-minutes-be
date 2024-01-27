package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.common.AesCbc128Crypto;
import com.heromakers.app.minutes.mapper.AccountMapper;
import com.heromakers.app.minutes.model.AccountModel;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.repository.AccountRepository;
import com.heromakers.app.minutes.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    private static final String PROFILE_IMAGE = "accountProfileImage";

    public List<AccountModel> getAccountList(Map param) {
        return accountMapper.queryList(param);
    }

    public AccountModel getAccountInfo(Integer accountId) {
        AccountModel accountModel = accountRepository.findById(accountId).orElse(null);
        if(accountModel == null) return null;

        fileRepository.findByLinkInfoAndLinkKey(PROFILE_IMAGE, accountId).ifPresent(accountModel::setProfileImage);
        return accountModel;
    }

    public AccountModel insertAccount(AccountModel accountParam) throws Exception {
        String joinType = accountParam.getJoinType();
        if(joinType == null || joinType.isEmpty()) {
            accountParam.setJoinType("user");
        }
        String password = accountParam.getPassword();
        if(password == null || password.isEmpty()) {
            password = accountParam.getAccountKey();
        }
        accountParam.setPassword(AesCbc128Crypto.getInstance().encrypt(password));

        accountParam.setCreatedAt(Instant.now());
        accountParam.setUpdatedAt(Instant.now());
        accountParam.setPasswordAt(Instant.now());
        AccountModel saved = accountRepository.save(accountParam);

        FileModel profileImage = accountParam.getProfileImage();
        if( profileImage != null && profileImage.getRealName() != null && profileImage.getBase64String() != null) {
            profileImage.setLinkInfo(PROFILE_IMAGE);
            profileImage.setLinkKey( saved.getAccountId());
            fileService.uploadS3File(profileImage);
        }
        return saved;
    }

    public boolean updateAccount(AccountModel accountParam) {
        accountMapper.update(accountParam);

        Integer accountId = accountParam.getAccountId();
        FileModel profileImage = accountParam.getProfileImage();
        if( profileImage == null || profileImage.getFileId() == null || profileImage.getFileId() == 0) {
            fileRepository.findByLinkInfoAndLinkKey(PROFILE_IMAGE, accountId).ifPresent(oldImage -> fileRepository.delete(oldImage));
            // ToDo remove file on server
        }
        if( profileImage != null && profileImage.getRealName() != null && profileImage.getBase64String() != null ) {
            profileImage.setLinkInfo(PROFILE_IMAGE);
            profileImage.setLinkKey(accountId);
            fileService.uploadS3File(profileImage);
        }
        return true;
    }

    public boolean deleteAccount(Integer accountId) {
        accountMapper.deleteFlag(accountId);
        return true;
    }

    public boolean updateFcmToken(Integer accountId, String fcmToken) {
        accountMapper.updateFcmToken(accountId, fcmToken);
        return true;
    }

    public boolean resetPassword(Integer accountId, String password) throws Exception {
        accountMapper.updatePassword(accountId, AesCbc128Crypto.getInstance().encrypt(password), "N");
        return true;
    }

    public boolean leave(Integer accountId, String reason) {
        accountMapper.leave(accountId, reason);
        return true;
    }
}
