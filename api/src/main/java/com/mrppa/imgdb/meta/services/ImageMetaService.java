package com.mrppa.imgdb.meta.services;

import java.util.Optional;

import com.mrppa.imgdb.exception.ImageMetaException;
import com.mrppa.imgdb.meta.entities.ImageMeta;

public interface ImageMetaService {

	ImageMeta save(ImageMeta imageMeta) throws ImageMetaException;

	Optional<ImageMeta> get(String imageId) throws ImageMetaException;

	void delete(String imageId) throws ImageMetaException;

}
