package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.mapper.TalkMapper;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.model.TalkModel;
import com.heromakers.app.minutes.repository.FileRepository;
import com.heromakers.app.minutes.repository.TalkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class TalkService {
    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private TalkMapper talkMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    private static final String TALK_MAIN_IMAGE = "talkMainImage";

    public List<TalkModel> getTalkList(Map param) {
        return talkMapper.queryList(param);
    }

    public TalkModel getTalkInfo(Integer talkId) {
        TalkModel talkModel = talkRepository.findById(talkId).orElse(null);
        if(talkModel == null) return null;

        fileRepository.findByLinkInfoAndLinkKey(TALK_MAIN_IMAGE, talkId).ifPresent(talkModel::setMainImage);
        return talkModel;
    }

    public TalkModel insertTalk(TalkModel talkParam) {
        talkParam.setCreatedAt(Instant.now());
        talkParam.setUpdatedAt(Instant.now());
        TalkModel saved = talkRepository.save(talkParam);

        FileModel mainImage = talkParam.getMainImage();
        if( mainImage != null && mainImage.getRealName() != null && mainImage.getBase64String() != null ) {
            mainImage.setLinkInfo(TALK_MAIN_IMAGE);
            mainImage.setLinkKey(saved.getTalkId());
            fileService.uploadS3File(mainImage);
        }
        return saved;
    }

    public boolean updateTalk(TalkModel talkParam) {
        talkMapper.update(talkParam);

        Integer talkId = talkParam.getTalkId();
        FileModel mainImage = talkParam.getMainImage();
        if( mainImage == null || mainImage.getFileId() == null || mainImage.getFileId() == 0) {
            fileRepository.findByLinkInfoAndLinkKey(TALK_MAIN_IMAGE, talkId).ifPresent(oldImage -> fileRepository.delete(oldImage));
            // ToDo remove file on server
        }
        if( mainImage != null && mainImage.getRealName() != null && mainImage.getBase64String() != null ) {
            mainImage.setLinkInfo(TALK_MAIN_IMAGE);
            mainImage.setLinkKey(talkId);
            fileService.uploadS3File(mainImage);
        }
        return true;
    }

    public boolean deleteTalk(Integer talkId) {
        talkMapper.deleteFlag(talkId);
        // 논리적 삭제이기에 복원 가능성 때문에 파일을 지우지 않음
        return true;
    }
}