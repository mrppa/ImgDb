package com.mrppa.imgdb.img.service.impl;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.exception.ImageFileStoreException;
import com.mrppa.imgdb.img.service.ImageStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class S3ImageStore implements ImageStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(S3ImageStore.class);

    @Value("${imageDb.s3ImageStore.accessKey}")
    private String accessKey;
    @Value("${imageDb.s3ImageStore.accessSecret}")
    private String accessSecret;
    @Value("${imageDb.s3ImageStore.region}")
    private String region;
    @Value("${imageDb.s3ImageStore.bucketName}")
    private String bucketName;
    @Value("${imageDb.s3ImageStore.baseUrl}")
    private String baseUrl;

    private AmazonS3 s3client;

    public S3ImageStore(String accessKey, String accessSecret, String region, String bucketName, String baseUrl) {
        this.accessKey = accessKey;
        this.accessSecret = accessSecret;
        this.region = region;
        this.bucketName = bucketName;
        this.baseUrl = baseUrl;
    }

    public S3ImageStore() {
    }

    @PostConstruct
    private void init() {
        LOGGER.info("Initializing S3ImageStore with accessKey:{} , region:{} , bucketName:{} , baseUrl:{}"
                , accessKey, region, bucketName, baseUrl);
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);

        s3client = AmazonS3ClientBuilder.standard().
                withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.valueOf(region)).build();

        if (!s3client.doesBucketExistV2(bucketName)) {
            throw new RuntimeException("S3 bucket" + bucketName + " does not exists");
        }
        LOGGER.debug("s3 client created");
    }


    @Override
    public void storeImage(InputStream inputStream, String fileName) throws ImageDbException {
        s3client.putObject(bucketName, fileName, inputStream, null);
    }

    @Override
    public void retrieveImage(OutputStream outputStream, String fileName) throws ImageDbException {
        if (!s3client.doesObjectExist(bucketName, fileName)) {
            throw new ImageFileNotFoundException("Image file not found");
        }
        S3Object s3Object = s3client.getObject(bucketName, fileName);
        try (InputStream in = s3Object.getObjectContent()) {
            IOUtils.copy(in, outputStream);
        } catch (IOException e) {
            throw new ImageFileStoreException("Error retrieving image", e);
        }
    }

    @Override
    public void deleteImage(String fileName) throws ImageDbException {
        if (!s3client.doesObjectExist(bucketName, fileName)) {
            throw new ImageFileNotFoundException("Image file not found");
        }
        s3client.deleteObject(bucketName, fileName);
    }

    @Override
    public String generateURL(String fileName) throws ImageDbException {
        return baseUrl + fileName;
    }
}
