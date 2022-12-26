package com.mrppa.imgdb.img.service.impl;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.exception.ImageFileStoreException;
import com.mrppa.imgdb.img.service.ImageStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;

import java.io.*;

public class LocalFileImageStore implements ImageStore {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileImageStore.class);

    @Value("${imageDb.localImageStore.baseUrl}")
    String localImageBaseUrl;

    @Value("${imageDb.localImageStore.basePath}")
    String basePath;

    public LocalFileImageStore(){
        super();
    }

    /**
     * For unit tests only
     * @param basePath
     */
    public LocalFileImageStore(String basePath){
        this.basePath=basePath;
    }

    @PostConstruct
    void init() {
        File baseDir = new File(basePath);
        boolean dirCreated = baseDir.mkdirs();
        LOGGER.debug("base director created:{}", dirCreated);
    }

    @Override
    public void storeImage(InputStream inputStream, String fileName) throws ImageDbException {
        LOGGER.debug("Storing Image {} ", fileName);
        File targetFile = new File(basePath + File.separator + fileName);
        try {
            FileCopyUtils.copy(inputStream, new FileOutputStream(targetFile));
        } catch (IOException e) {
            throw new ImageFileStoreException("Error storing image", e);
        }

    }

    @Override
    public void retrieveImage(OutputStream outputStream, String fileName) throws ImageDbException {
        File targetFile = new File(basePath + File.separator + fileName);
        if (!targetFile.exists()) {
            throw new ImageFileNotFoundException("Image file not found");
        }
        try {
            FileCopyUtils.copy(new FileInputStream(targetFile), outputStream);
        } catch (IOException e) {
            throw new ImageFileStoreException("Error retrieving image", e);
        }
    }

    @Override
    public void deleteImage(String fileName) throws ImageDbException {
        File targetFile = new File(basePath + File.separator + fileName);
        if (!targetFile.exists()) {
            throw new ImageFileNotFoundException("Image file not found");
        }
        boolean deleted = targetFile.delete();
        LOGGER.info("File {} deleted:{}", fileName, deleted);
    }

    @Override
    public String generateURL(String imageId) {
        return localImageBaseUrl + "/" + imageId;
    }
}
