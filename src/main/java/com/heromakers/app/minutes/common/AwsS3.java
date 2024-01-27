package com.heromakers.app.minutes.common;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

public class AwsS3 {
    private AmazonS3 s3Client;
    final private Regions clientRegion = Regions.AP_NORTHEAST_2;
    final private String bucket = "frenz-common";
    final private String accessKey = "AKIAY2LUT4A55THC5R5U";
    final private String secretKey = "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";

    private AwsS3() {
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(clientRegion)
                .build();
    }

    //singleton pattern
    private static AwsS3 instance = null;
    
    public static AwsS3 getInstance() {
        if (instance == null) {
            instance = new AwsS3();
        }
        return instance;
    }

    public String upload(String fileKey, File file) {
        return uploadToS3(new PutObjectRequest(bucket, fileKey, file)).toString();
    }

    public String upload(String fileKey, InputStream is, String contentType, long contentLength) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        objectMetadata.setContentLength(contentLength);
        return uploadToS3(new PutObjectRequest(bucket, fileKey, is, objectMetadata)).toString();
    }

    private URL uploadToS3(PutObjectRequest putObjectRequest) {
        try {
            s3Client.putObject(putObjectRequest);
        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s3Client.getUrl(bucket, putObjectRequest.getKey());
    }

    public void copy(String originKey, String targetKey) {
        try {
            //Copy 객체 생성
            CopyObjectRequest copyObjRequest = new CopyObjectRequest(
                    bucket,
                    originKey,
                    bucket,
                    targetKey
            );
            //Copy
            s3Client.copyObject(copyObjRequest);
            System.out.println(String.format("Finish copying [%s] to [%s]", originKey, targetKey));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    public void delete(String fileKey) {
        try {
            //Delete 객체 생성
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, fileKey);
            //Delete
            s3Client.deleteObject(deleteObjectRequest);
            System.out.println(String.format("[%s] deletion complete", fileKey));

        } catch (AmazonServiceException e) {
            e.printStackTrace();
        } catch (SdkClientException e) {
            e.printStackTrace();
        }
    }

    public String getPresignedUrl(String filePath) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, 1);
        return s3Client.generatePresignedUrl(bucket, filePath, calendar.getTime(), HttpMethod.POST).toString();
    }
}