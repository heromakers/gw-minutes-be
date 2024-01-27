package com.heromakers.app.minutes.service;

import com.heromakers.app.minutes.common.AwsS3;
import com.heromakers.app.minutes.mapper.FileMapper;
import com.heromakers.app.minutes.model.FileModel;
import com.heromakers.app.minutes.repository.FileRepository;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class FileService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FileMapper fileMapper;

    @Value("${upload.path}")
    private String UPLOAD_PATH;

    @Value("${file.baseUrl}")
    private String FILE_BASE_URL;

    public List<FileModel> getFileList(Map param) {
        return fileMapper.queryList(param);
    }

    public FileModel getFileInfo(Integer fileId) {
        return fileRepository.findById(fileId).orElse(null);
    }

    public FileModel insertFile(FileModel fileParam) {
        fileParam.setSavedAt(Instant.now());
        return fileRepository.save(fileParam);
    }

    public boolean deleteFile(Integer fileId) {
        fileMapper.delete(fileId);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public FileModel uploadS3File(FileModel fileParam) {
        if(fileParam == null) return null;

        byte[] fileByte = fileParam.getBytes();
        if(fileByte == null || fileByte.length == 0) {
            fileByte = Base64.decodeBase64(fileParam.getBase64String());
        }
        if(fileByte == null || fileByte.length == 0) {
            return null;
        }

        String fileRealName = fileParam.getRealName();
        if(fileRealName == null || fileRealName.isEmpty()) fileRealName = "file";
        fileRealName = fileRealName.replace("C:\\fakepath", "").replace("C:fakepath", "");
        int fidx = fileRealName.lastIndexOf(File.separatorChar);
        if(fidx > 0) {
            fileRealName = fileRealName.substring(fidx);
        }

        String fileSavedName = String.valueOf(System.currentTimeMillis());
        String extension = "";
        String contentType = "";

        fidx = fileRealName.lastIndexOf(".");
        if(fidx > 0) {
            extension = fileRealName.substring(fidx+1).toLowerCase();
            fileSavedName = fileSavedName + "." + extension;
        }
        InputStream is = new ByteArrayInputStream(fileByte);
        if( "png".equals(extension) || "jpg".equals(extension) || "gif".equals(extension) || "bmp".equals(extension) ) {
            contentType = "image/"+extension;
        } else {
            contentType = "application/octet-stream";
        }

        String fileUrl = AwsS3.getInstance().upload(fileSavedName, is, contentType, fileByte.length);
        if(fileUrl == null) return null;

        fileParam.setFileUrl(fileUrl);
        fileParam.setFileSize((long) fileByte.length);
        fileParam.setSavedAt(Instant.now());
        return fileRepository.save(fileParam);
    }

    public boolean deleteS3File(FileModel fileModel) {
        if(fileModel == null) return false;
        String fileKey = fileModel.getFileUrl(); // .replace(FILE_BASE_URL, "");
        int fidx = fileKey.lastIndexOf(File.separatorChar);
        if(fidx > 0) {
            AwsS3.getInstance().delete(fileKey.substring(fidx));
        }

        Integer fileId = fileModel.getFileId();
        fileMapper.delete(fileId);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public FileModel uploadFile(FileModel fileParam) throws IOException {
        if(fileParam == null) return null;

        byte[] fileByte = fileParam.getBytes();
        if(fileByte == null || fileByte.length == 0) {
            fileByte = Base64.decodeBase64(fileParam.getBase64String());
        }
        if(fileByte == null || fileByte.length == 0) {
            return null;
        }

        String fileRealName = fileParam.getRealName();
        if(fileRealName == null || fileRealName.isEmpty()) fileRealName = "file";

        fileRealName = fileRealName.replace("C:\\fakepath", "").replace("C:fakepath", "");
        int fidx = fileRealName.lastIndexOf(File.separatorChar);
        if(fidx > 0) {
            fileRealName = fileRealName.substring(fidx);
        }
        String extension = "";
        fidx = fileRealName.lastIndexOf(".");
        if(fidx > 0) {
            extension = fileRealName.substring(fidx+1).toLowerCase();
        }

        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("uuuu")
                .withZone(ZoneId.systemDefault());
        String thisYear = formatter.format(Instant.now());

        String fileSavedName = String.valueOf(System.currentTimeMillis());
        if(!extension.isEmpty()) {
            fileSavedName = fileSavedName + "." + extension;
        }

        String filePath = UPLOAD_PATH + File.separatorChar + thisYear + File.separatorChar + fileSavedName;
        String fileUrl = FILE_BASE_URL + thisYear + "/" + fileSavedName;

        File file = new File(filePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(fileByte);
        outputStream.flush();
        outputStream.close();

        fileParam.setFileUrl(fileUrl);
        fileParam.setFileSize((long) fileByte.length);
        fileParam.setSavedAt(Instant.now());

        return fileRepository.save(fileParam);
    }
}