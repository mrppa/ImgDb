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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.meta.entities.AccessMode;
import com.mrppa.imgdb.meta.entities.ImageMetaAccess;
import com.mrppa.imgdb.services.ImageDbService;

@RestController
@RequestMapping("/api/v1/img")
public class ImgRestController {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ImageDbService imageDbService;

	@GetMapping("/ping")
	public String ping() {
		return "ping";
	}

	@PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String addImage(@RequestPart(value = "file", required = true) MultipartFile multipartFile,
			@RequestParam(value = "userKey", defaultValue = "") String userKey,
			@RequestParam(value = "readAccess", defaultValue = "PUBLIC") AccessMode readAccess,
			@RequestParam(value = "writeAccess", defaultValue = "PUBLIC") AccessMode writeAccess)
			throws ImageDbException, IOException {
		LOGGER.debug("adding new image. readAccess:{} , writeAccess:{}", readAccess, writeAccess);
		ImageMetaAccess imageMetaAccess = new ImageMetaAccess();
		imageMetaAccess.setReadAccess(readAccess);
		imageMetaAccess.setWriteAccess(writeAccess);
		String imageId = imageDbService.addImage(multipartFile.getInputStream(), userKey, imageMetaAccess);
		return imageId;
	}

	@GetMapping(value = "/{imageId}")
	public void getImage(@PathParam("imageId") String imageId,
			@RequestParam(value = "userKey", defaultValue = "") String userKey, HttpServletResponse response)
			throws ImageDbException, IOException {
		LOGGER.trace("retriving an image. id:{}", imageId);
		imageDbService.retriveImage(imageId, response.getOutputStream(), userKey);
	}

	@DeleteMapping(value = "/{imageId}")
	public void deleteImage(@PathParam("imageId") String imageId,
			@RequestParam(value = "userKey", defaultValue = "") String userKey) throws ImageDbException, IOException {
		LOGGER.trace("delete an image. id:{}", imageId);
		imageDbService.deleteImage(imageId, userKey);
	}
}
