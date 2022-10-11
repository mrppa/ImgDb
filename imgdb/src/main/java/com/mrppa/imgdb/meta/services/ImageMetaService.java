package com.mrppa.imgdb.meta.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;

@Transactional
@Service
public class ImageMetaService {

	ImageMetaRepository imageMetaRepository;

	public ImageMeta save(ImageMeta imageMeta) {
		imageMeta = imageMetaRepository.save(imageMeta);
		return imageMeta;
	}

	public Optional<ImageMeta> get(String imageId) {
		Optional<ImageMeta> optImageMeta = imageMetaRepository.findById(imageId);
		return optImageMeta;
	}

	public void delete(String imageId) {
		imageMetaRepository.deleteById(imageId);
	}
}
