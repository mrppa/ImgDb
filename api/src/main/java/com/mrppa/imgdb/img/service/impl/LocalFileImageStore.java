package com.mrppa.imgdb.img.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.exception.ImageFileStoreException;
import com.mrppa.imgdb.img.service.ImageStore;

public class LocalFileImageStore implements ImageStore {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	private final String basePath;

	@Value("${imagedb.localImageStore.baseUrl}")
	private String localImageBaseUrl;

	public LocalFileImageStore(String basePath) {
		this.basePath = basePath;
		File baseDir = new File(basePath);
		baseDir.mkdirs();
	}

	@Override
	public void storeImage(InputStream inputStream, String fileName) throws ImageDbException {
		LOGGER.debug("Storing Image {} ", fileName);
		File targetFile = new File(basePath + File.separator + fileName);
		try {
			FileCopyUtils.copy(inputStream, new FileOutputStream(targetFile));
		} catch (IOException e) {
			LOGGER.error("Error storing image", e);
			throw new ImageFileStoreException("Error storing image", e);
		}

	}

	@Override
	public void retriveImage(OutputStream outputStream, String fileName) throws ImageDbException {
		File targetFile = new File(basePath + File.separator + fileName);
		if (!targetFile.exists()) {
			throw new ImageFileNotFoundException("Image file not found");
		}
		try {
			FileCopyUtils.copy(new FileInputStream(targetFile), outputStream);
		} catch (IOException e) {
			LOGGER.error("Error retriving image", e);
			throw new ImageFileStoreException("Error retriving image", e);
		}
	}

	@Override
	public void deleteImage(String fileName) throws ImageDbException {
		File targetFile = new File(basePath + File.separator + fileName);
		if (!targetFile.exists()) {
			throw new ImageFileNotFoundException("Image file not found");
		}
		targetFile.delete();
	}

	@Override
	public String generateURL(String imageId) throws ImageDbException {
		return localImageBaseUrl + "/" + imageId;
	}
}
