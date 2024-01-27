package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.mapper.NoticeMapper;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.model.NoticeModel;
import com.heromakers.app.minutes.repository.FileRepository;
import com.heromakers.app.minutes.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {
    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    private static final String NOTICE_MAIN_IMAGE = "noticeMainImage";

    public List<NoticeModel> getNoticeList(Map param) {
        return noticeMapper.queryList(param);
    }

    public NoticeModel getNoticeInfo(Integer noticeId) {
        NoticeModel noticeModel = noticeRepository.findById(noticeId).orElse(null);
        if(noticeModel == null) return null;

        fileRepository.findByLinkInfoAndLinkKey(NOTICE_MAIN_IMAGE, noticeId).ifPresent(noticeModel::setMainImage);
        return noticeModel;
    }

    public NoticeModel insertNotice(NoticeModel noticeParam) {
        noticeParam.setCreatedAt(Instant.now());
        noticeParam.setUpdatedAt(Instant.now());
        NoticeModel saved = noticeRepository.save(noticeParam);

        FileModel mainImage = noticeParam.getMainImage();
        if( mainImage != null && mainImage.getRealName() != null && mainImage.getBase64String() != null ) {
            mainImage.setLinkInfo(NOTICE_MAIN_IMAGE);
            mainImage.setLinkKey(saved.getNoticeId());
            fileService.uploadS3File(mainImage);
        }
        return saved;
    }

    public boolean updateNotice(NoticeModel noticeParam) {
        noticeMapper.update(noticeParam);

        Integer noticeId = noticeParam.getNoticeId();
        FileModel mainImage = noticeParam.getMainImage();
        if( mainImage == null || mainImage.getFileId() == null || mainImage.getFileId() == 0) {
            fileRepository.findByLinkInfoAndLinkKey(NOTICE_MAIN_IMAGE, noticeId).ifPresent(oldImage -> fileRepository.delete(oldImage));
            // ToDo remove file on server
        }
        if( mainImage != null && mainImage.getRealName() != null && mainImage.getBase64String() != null ) {
            mainImage.setLinkInfo(NOTICE_MAIN_IMAGE);
            mainImage.setLinkKey(noticeId);
            fileService.uploadS3File(mainImage);
        }
        return true;
    }

    public boolean deleteNotice(Integer noticeId) {
        noticeMapper.deleteFlag(noticeId);
        // 논리적 삭제이기에 복원 가능성 때문에 파일을 지우지 않음
        return true;
    }
}