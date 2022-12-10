package com.mrppa.imgdb.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.mappers.ImageMetaMapper;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.UiImageMeta;
import com.mrppa.imgdb.services.ImageDbService;

@RestController
@RequestMapping("/api/v1/img")
public class ImgRestController {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ImageDbService imageDbService;

	@Autowired
	ImageMetaMapper imageMetaMapper;

	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String addImage(@RequestPart(value = "file", required = true) MultipartFile multipartFile,
			@RequestParam(value = "userKey", defaultValue = "") String userKey) throws ImageDbException, IOException {
		LOGGER.debug("adding new image.");
		ImageMeta imageMeta = imageDbService.addImage(multipartFile.getInputStream(), ImageMeta.builder().build(),
				userKey);
		return imageMeta.getImagId();
	}

	@GetMapping(value = "/{imageId}")
	public void getImage(@PathParam("imageId") String imageId,
			@RequestParam(value = "userKey", defaultValue = "") String userKey, HttpServletResponse response)
			throws ImageDbException, IOException {
		LOGGER.trace("retriving an image. id:{}", imageId);
		imageDbService.retrieveImage(imageId, response.getOutputStream(), userKey);
	}

	@GetMapping(value = "/meta/{imageId}")
	public UiImageMeta getImage(@PathParam("imageId") String imageId,
			@RequestParam(value = "userKey", defaultValue = "") String userKey) throws ImageDbException, IOException {
		LOGGER.trace("retriving an image. id:{}", imageId);
		ImageMeta imageMeta = imageDbService.retrieveImageMetaData(imageId, userKey);
		return imageMetaMapper.convert(imageMeta);
	}

	@PutMapping(value = "/meta/{imageId}")
	public UiImageMeta updateImage(@PathParam("imageId") String imageId, @RequestBody ImageMeta imageMeta,
			@RequestParam(value = "userKey", defaultValue = "") String userKey) throws ImageDbException, IOException {
		LOGGER.trace("updating an image. id:{}, imageMeta:{}", imageId, imageMeta);

		if (imageId.equals(imageMeta.getImagId())) {
			throw new ImageDbException("ImageId does not match with the imageMeta");
		}

		ImageMeta updatedImageMeta = imageDbService.updateImageMetaData(imageMeta, imageId);
		return imageMetaMapper.convert(updatedImageMeta);
	}

	@DeleteMapping(value = "/{imageId}")
	public void deleteImage(@PathParam("imageId") String imageId,
			@RequestParam(value = "userKey", defaultValue = "") String userKey) throws ImageDbException, IOException {
		LOGGER.trace("delete an image. id:{}", imageId);
		imageDbService.deleteImage(imageId, userKey);
	}
}
