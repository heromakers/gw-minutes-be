package com.heromakers.app.minutes.mapper;

import com.heromakers.app.minutes.model.FileModel;
import org.springframework.stereotype.Repository;

@Repository
public interface FileMapper extends BaseMapper<FileModel, Integer> {
    public int delete(int fileId);
}