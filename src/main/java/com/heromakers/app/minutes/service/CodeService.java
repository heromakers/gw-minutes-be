package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.mapper.CodeMapper;
import com.heromakers.app.minutes.model.CodeModel;
import com.heromakers.app.minutes.repository.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class CodeService {
    @Autowired
    private CodeRepository codeRepository;

    @Autowired
    private CodeMapper codeMapper;

    public List<CodeModel> getCodeList(Map param) {
        return codeMapper.queryList(param);
    }

    public CodeModel getCodeInfo(Integer codeId) {
        return codeRepository.findById(codeId).orElse(null);
    }

    public CodeModel insertCode(CodeModel codeParam) {
        codeParam.setEditedAt(Instant.now());
        return codeRepository.save(codeParam);
    }

    public boolean updateCode(CodeModel codeParam) {
        codeMapper.update(codeParam);
        return true;
    }

    public boolean deleteCode(Integer codeId) {
        codeMapper.deleteFlag(codeId);
        return true;
    }

}