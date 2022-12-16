package com.mrppa.imgdb.rest;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.mappers.ImageMetaMapper;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.entities.ImageMetaStatus;
import com.mrppa.imgdb.meta.services.ImageMetaService;
import com.mrppa.imgdb.model.ImgDbResponse;
import com.mrppa.imgdb.model.Operation;
import com.mrppa.imgdb.model.UiImageMeta;
import com.mrppa.imgdb.services.AccessControlService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/img")
public class ImgRestController {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImgRestController.class);
	private static final String MESSAGE_NO_RECORD = " No matching record found for Id ";

	@Autowired
	ImageMetaMapper imageMetaMapper;

	@Autowired
	ImageMetaService imageMetaService;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	ImageStore imageStore;

	@Autowired
	AccessControlService accessControlService;

	@PostMapping(value = "/meta")
	public ResponseEntity<ImgDbResponse<UiImageMeta>> createImageMeta(@RequestBody UiImageMeta uiImageMeta,
			@RequestHeader(value = "userKey", defaultValue = "") String userKey) throws ImageDbException {
		LOGGER.debug("adding new imagemeta with imageMeta:{}", uiImageMeta);
		ImageMeta imageMeta = imageMetaMapper.convert(uiImageMeta);

		imageMeta.setImagId(null);
		imageMeta.setHashedUserKey(passwordEncoder.encode(userKey));
		imageMeta.setStatus(ImageMetaStatus.CREATED);

		imageMeta = imageMetaService.save(imageMeta);

		return metaOkResponse(imageMeta);
	}

	@PutMapping(value = "/meta/{imageId}")
	public ResponseEntity<ImgDbResponse<UiImageMeta>> updateImageMeta(@RequestBody UiImageMeta uiImageMeta,
			@RequestHeader(value = "userKey", defaultValue = "") String userKey,
			@PathVariable("imageId") String imageId) throws ImageDbException {
		LOGGER.debug("updating imagemeta with imageid:{}, imageMeta:{}", imageId, uiImageMeta);

		if (!imageId.equals(uiImageMeta.getImagId())) {
			return metaFailResponse(HttpStatus.BAD_REQUEST, "ImageId does not match with the body");
		}

		Optional<ImageMeta> optImageMetaFromDb = imageMetaService.get(imageId);
		if (optImageMetaFromDb.isEmpty()) {
			return metaFailResponse(HttpStatus.NOT_FOUND, MESSAGE_NO_RECORD);
		}
		ImageMeta imageMetaFromDb = optImageMetaFromDb.get();

		accessControlService.validateRowLevelAccess(userKey, imageMetaFromDb, Operation.MODIFY);

		ImageMeta imageMeta = imageMetaMapper.convert(uiImageMeta);
		imageMeta.setHashedUserKey(imageMetaFromDb.getHashedUserKey());
		imageMeta.setStatus(imageMetaFromDb.getStatus());
		imageMeta.setAddedDate(imageMetaFromDb.getAddedDate());
		imageMeta.setVersion(imageMetaFromDb.getVersion());

		imageMeta = imageMetaService.save(imageMeta);

		return metaOkResponse(imageMeta);
	}

	@GetMapping(value = "/meta/{imageId}")
	public ResponseEntity<ImgDbResponse<UiImageMeta>> retrieveImageMeta(@PathVariable("imageId") String imageId,
			@RequestHeader(value = "userKey", defaultValue = "") String userKey) throws ImageDbException {
		LOGGER.debug("get imagemeta with imageid:{}", imageId);

		Optional<ImageMeta> optImageMetaFromDb = imageMetaService.get(imageId);
		if (optImageMetaFromDb.isEmpty()) {
			return metaFailResponse(HttpStatus.NOT_FOUND, MESSAGE_NO_RECORD);
		}
		ImageMeta imageMetaFromDb = optImageMetaFromDb.get();

		accessControlService.validateRowLevelAccess(userKey, imageMetaFromDb, Operation.VIEW);

		return metaOkResponse(imageMetaFromDb);
	}

	@PutMapping(value = "/{imageId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ImgDbResponse<UiImageMeta>> uploadImage(
			@RequestPart(value = "file", required = true) MultipartFile multipartFile,
			@RequestHeader(value = "userKey", defaultValue = "") String userKey,
			@PathVariable("imageId") String imageId) throws ImageDbException, IOException {
		LOGGER.debug("uploading new image with imageid:{}", imageId);

		Optional<ImageMeta> optImageMetaFromDb = imageMetaService.get(imageId);
		if (optImageMetaFromDb.isEmpty()) {
			return metaFailResponse(HttpStatus.NOT_FOUND, MESSAGE_NO_RECORD);
		}
		ImageMeta imageMetaFromDb = optImageMetaFromDb.get();

		accessControlService.validateRowLevelAccess(userKey, imageMetaFromDb, Operation.MODIFY);

		imageStore.storeImage(multipartFile.getInputStream(), imageId);

		imageMetaFromDb.setStatus(ImageMetaStatus.ACTIVE);
		imageMetaFromDb = imageMetaService.save(imageMetaFromDb);

		return metaOkResponse(imageMetaFromDb);
	}

	@GetMapping(value = "/{imageId}")
	public ResponseEntity<Void> getImage(@PathVariable("imageId") String imageId,
			@RequestHeader(value = "userKey", defaultValue = "") String userKey, HttpServletResponse response)
			throws ImageDbException, IOException {
		LOGGER.trace("retriving an image. id:{}", imageId);

		Optional<ImageMeta> optImageMetaFromDb = imageMetaService.get(imageId);
		if (optImageMetaFromDb.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		ImageMeta imageMetaFromDb = optImageMetaFromDb.get();

		accessControlService.validateRowLevelAccess(userKey, imageMetaFromDb, Operation.VIEW);

		imageStore.retriveImage(response.getOutputStream(), imageId);

		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/{imageId}")
	public ResponseEntity<String> deleteImage(@PathVariable("imageId") String imageId,
			@RequestHeader(value = "userKey", defaultValue = "") String userKey) throws ImageDbException {
		LOGGER.trace("delete an image. id:{}", imageId);

		Optional<ImageMeta> optImageMetaFromDb = imageMetaService.get(imageId);
		if (optImageMetaFromDb.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		ImageMeta imageMetaFromDb = optImageMetaFromDb.get();

		accessControlService.validateRowLevelAccess(userKey, imageMetaFromDb, Operation.MODIFY);

		imageMetaFromDb.setStatus(ImageMetaStatus.PENDING_DELETE);
		imageMetaService.save(imageMetaFromDb);

		return ResponseEntity.ok().build();
	}

	private ResponseEntity<ImgDbResponse<UiImageMeta>> metaOkResponse(ImageMeta imageMeta) throws ImageDbException {
		UiImageMeta uiImageMeta = imageMetaMapper.convert(imageMeta);
		uiImageMeta.setImageUrl(imageStore.generateURL(imageMeta.getImagId()));
		return ResponseEntity.ok(ImgDbResponse.<UiImageMeta>builder().data(uiImageMeta).build());
	}

	private ResponseEntity<ImgDbResponse<UiImageMeta>> metaFailResponse(HttpStatus httpStatus, String error) {
		return ResponseEntity.status(httpStatus)
				.body(ImgDbResponse.<UiImageMeta>builder().success(false).message(error).build());
	}

}