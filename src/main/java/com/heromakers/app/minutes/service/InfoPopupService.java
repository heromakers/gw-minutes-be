package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.mapper.InfoPopupMapper;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.model.InfoPopupModel;
import com.heromakers.app.minutes.repository.FileRepository;
import com.heromakers.app.minutes.repository.InfoPopupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class InfoPopupService {
    @Autowired
    private InfoPopupRepository infoPopupRepository;

    @Autowired
    private InfoPopupMapper infoPopupMapper;

    @Autowired
    private FileService fileService;

    @Autowired
    private FileRepository fileRepository;

    private static final String NOTICE_POPUP_IMAGE = "infoPopupImage";

    public List<InfoPopupModel> getInfoPopupList(Map param) {
        return infoPopupMapper.queryList(param);
    }

    public InfoPopupModel getInfoPopupInfo(Integer seq) {
        InfoPopupModel noticeModel = infoPopupRepository.findById(seq).orElse(null);
        if(noticeModel == null) return null;

        fileRepository.findByLinkInfoAndLinkKey(NOTICE_POPUP_IMAGE, seq).ifPresent(noticeModel::setPopupImage);
        return noticeModel;
    }

    public InfoPopupModel insertInfoPopup(InfoPopupModel popupParam) {
        popupParam.setEditedAt(Instant.now());
        InfoPopupModel saved = infoPopupRepository.save(popupParam);

        FileModel popupImage = popupParam.getPopupImage();
        if( popupImage != null && popupImage.getRealName() != null && popupImage.getBase64String() != null ) {
            popupImage.setLinkInfo(NOTICE_POPUP_IMAGE);
            popupImage.setLinkKey(saved.getSeq());
            fileService.uploadS3File(popupImage);
        }
        return saved;
    }

    public boolean updateInfoPopup(InfoPopupModel popupParam) {
        infoPopupMapper.update(popupParam);

        Integer seq = popupParam.getSeq();
        FileModel popupImage = popupParam.getPopupImage();
        if( popupImage == null || popupImage.getFileId() == null || popupImage.getFileId() == 0) {
            fileRepository.findByLinkInfoAndLinkKey(NOTICE_POPUP_IMAGE, seq).ifPresent(oldImage -> fileRepository.delete(oldImage));
            // ToDo remove file on server
        }
        if( popupImage != null && popupImage.getRealName() != null && popupImage.getBase64String() != null ) {
            popupImage.setLinkInfo(NOTICE_POPUP_IMAGE);
            popupImage.setLinkKey(seq);
            fileService.uploadS3File(popupImage);
        }
        return true;
    }

    public boolean deleteInfoPopup(Integer seq) {
        infoPopupMapper.deleteFlag(seq);
        // 논리적 삭제이기에 복원 가능성 때문에 파일을 지우지 않음
        return true;
    }
}