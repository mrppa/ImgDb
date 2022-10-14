package com.mrppa.imgdb.services;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.entities.ImageMetaAccess;
import com.mrppa.imgdb.meta.entities.ImageMetaStatus;
import com.mrppa.imgdb.meta.services.ImageMetaService;

@Service
public class ImageDbService {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ImageMetaService imageMetaService;

	@Autowired
	ImageStore imageStore;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AccessControlService accessControlService;

	public String addImage(InputStream inputStream, CharSequence userKey, ImageMetaAccess access)
			throws ImageDbException {
		ImageMeta imageMeta = new ImageMeta();
		imageMeta.setStatus(ImageMetaStatus.UPLOADING);
		if (access != null) {
			imageMeta.setAccess(access);
		}

		String hashedUserKey = passwordEncoder.encode(userKey == null ? "" : userKey);
		imageMeta.setHashedUserKey(hashedUserKey);

		imageMeta = imageMetaService.save(imageMeta);

		try {
			imageStore.storeImage(inputStream, imageMeta.getImagId());

			imageMeta.setStatus(ImageMetaStatus.ACTIVE);
			imageMetaService.save(imageMeta);
		} catch (ImageDbException e) {
			LOGGER.error("Error storing image", e);
			imageMetaService.delete(imageMeta.getImagId());
			throw e;
		}

		return imageMeta.getImagId();
	}

	public void replaceImage(String imageId, InputStream inputStream, String userKey) throws ImageDbException {
		Optional<ImageMeta> optImageMeta = imageMetaService.get(imageId);
		if (optImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("Image not found for id " + imageId);
		}
		ImageMeta imageMeta = optImageMeta.get();
		if (ImageMetaStatus.ACTIVE != imageMeta.getStatus()) {
			throw new ImageDbException("Image cannot be altered this time ");
		}
		accessControlService.validateRowLevelAccess(userKey, imageMeta, "replace");

		imageMeta.setStatus(ImageMetaStatus.UPLOADING_MODIFICATION);

		imageMeta = imageMetaService.save(imageMeta);

		try {
			imageStore.storeImage(inputStream, imageMeta.getImagId());
		} catch (ImageDbException e) {
			LOGGER.error("Error storing image", e);
			imageMeta.setStatus(ImageMetaStatus.ACTIVE);
			imageMetaService.save(imageMeta);
			throw e;
		}
	}

	public void retriveImage(String imageId, OutputStream outputStream, String userKey) throws ImageDbException {
		Optional<ImageMeta> optImageMeta = imageMetaService.get(imageId);
		if (optImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("Imagemeta not found");
		}
		ImageMeta imageMeta = optImageMeta.get();

		accessControlService.validateRowLevelAccess(userKey, imageMeta, "replace");

		if (ImageMetaStatus.ACTIVE != imageMeta.getStatus()) {
			throw new ImageDbException("Image fetch image at this time ");
		}

		try {
			imageStore.retriveImage(outputStream, optImageMeta.get().getImagId());
		} catch (ImageFileNotFoundException e) {
			LOGGER.info("Iamge for id {} not found. Meta to be deleted", optImageMeta.get().getImagId());
			imageMetaService.delete(optImageMeta.get().getImagId());
			throw new ImageFileNotFoundException("Imagemeta ready to delete");
		}

	}

	public void deleteImage(String imageId, String userKey) throws ImageDbException {
		Optional<ImageMeta> optImageMeta = imageMetaService.get(imageId);
		if (optImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("Imagemeta not found");
		}
		ImageMeta imageMeta = optImageMeta.get();

		accessControlService.validateRowLevelAccess(userKey, imageMeta, "delete");

		imageMetaService.delete(imageId);

		try {
			imageStore.deleteImage(imageId);
		} catch (ImageFileNotFoundException e) {
			LOGGER.info("Iamge for id {} not found", optImageMeta.get().getImagId());
		}

	}

}
