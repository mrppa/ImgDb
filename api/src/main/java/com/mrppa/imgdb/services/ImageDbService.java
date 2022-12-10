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
import com.mrppa.imgdb.meta.entities.ImageMetaStatus;
import com.mrppa.imgdb.meta.services.ImageMetaService;
import com.mrppa.imgdb.model.Operation;

@Service
public class ImageDbService {
	private final Logger LOGGER = LoggerFactory.getLogger(ImageDbService.class);

	@Autowired
	ImageMetaService imageMetaService;

	@Autowired
	ImageStore imageStore;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	AccessControlService accessControlService;

	/**
	 * Add Image
	 *
	 * @param inputStream
	 * @param imageMeta
	 * @param userKey
	 * @return imageId imageMeta
	 * @throws ImageDbException
	 */
	public ImageMeta addImage(InputStream inputStream, ImageMeta imageMeta, CharSequence userKey)
			throws ImageDbException {

		LOGGER.debug("Adding image with imageMeta:{}", imageMeta);

		// initial imageMeta storing
		imageMeta.setImagId(null);
		imageMeta.setHashedUserKey(passwordEncoder.encode(userKey));
		imageMeta = imageMetaService.save(imageMeta);

		try {
			// Store actual image
			imageStore.storeImage(inputStream, imageMeta.getImagId());

			// Update image meta
			imageMeta.setStatus(ImageMetaStatus.ACTIVE);
			imageMeta = imageMetaService.save(imageMeta);

			LOGGER.debug("Adding image completed with ImageMeta:{}", imageMeta);
		} catch (Exception e) {
			LOGGER.debug("Image Store failed . Cleanup to be triggered for {}", imageMeta.getImagId());
			triggerCleanupImage(imageMeta.getImagId());
			throw new ImageDbException("Adding image failed", e);
		}

		return imageMeta;
	}

	/**
	 *
	 * @param imageId
	 * @param userKey
	 * @throws ImageFileNotFoundException
	 * @throws ImageDbException
	 */
	public void deleteImage(String imageId, CharSequence userKey) throws ImageFileNotFoundException, ImageDbException {
		LOGGER.debug("Deleting image with imageId:{}", imageId);

		Optional<ImageMeta> optImageMeta = imageMetaService.get(imageId);
		if (optImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("Imagemeta not found");
		}
		ImageMeta imageMeta = optImageMeta.get();

		accessControlService.validateRowLevelAccess(userKey, imageMeta, Operation.MODIFY);

		triggerCleanupImage(imageId);

	}

	/**
	 *
	 * @param imageId
	 * @param outputStream
	 * @param userKey
	 * @throws ImageFileNotFoundException
	 * @throws ImageDbException
	 */
	public void retrieveImage(String imageId, OutputStream outputStream, CharSequence userKey)
			throws ImageFileNotFoundException, ImageDbException {
		LOGGER.debug("Retriving image with imageId:{}", imageId);

		Optional<ImageMeta> optImageMeta = imageMetaService.get(imageId);
		if (optImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("ImageMeta not found");
		}
		ImageMeta imageMeta = optImageMeta.get();

		accessControlService.validateRowLevelAccess(userKey, imageMeta, Operation.VIEW);

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

	/**
	 * Retrieve Image by Id
	 *
	 * @param imageId
	 * @param userKey
	 * @return
	 * @throws ImageFileNotFoundException
	 * @throws ImageDbException
	 */
	public ImageMeta retrieveImageMetaData(String imageId, CharSequence userKey)
			throws ImageFileNotFoundException, ImageDbException {
		LOGGER.debug("Retriving imageMeta with imageId:{}", imageId);

		Optional<ImageMeta> optImageMeta = imageMetaService.get(imageId);
		if (optImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("ImageMeta not found");
		}
		ImageMeta imageMeta = optImageMeta.get();

		accessControlService.validateRowLevelAccess(userKey, imageMeta, Operation.VIEW);

		return imageMeta;
	}

	public ImageMeta updateImageMetaData(ImageMeta imageMeta, CharSequence userKey)
			throws ImageFileNotFoundException, ImageDbException {
		LOGGER.debug("Updating imageMeta with  imageMeta:{}", imageMeta);

		Optional<ImageMeta> optDbImageMeta = imageMetaService.get(imageMeta.getImagId());
		if (optDbImageMeta.isEmpty()) {
			throw new ImageFileNotFoundException("ImageMeta not found in DB");
		}

		accessControlService.validateRowLevelAccess(userKey, optDbImageMeta.get(), Operation.VIEW);

		imageMeta.setHashedUserKey(optDbImageMeta.get().getHashedUserKey());
		imageMetaService.save(imageMeta);

		return imageMeta;
	}

	public void triggerCleanupImage(String imageId) {
		try {
			imageMetaService.delete(imageId);
		} catch (Exception e) {
			LOGGER.error("Error cleaning up image MetaData for {}", imageId);
		}
		try {
			imageStore.deleteImage(imageId);
		} catch (Exception e) {
			LOGGER.error("Error cleaning up image for {}", imageId);
		}
	}

}
