package com.mrppa.imgdb.meta.services.impl;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;
import com.mrppa.imgdb.meta.services.ImageMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class ImageMetaServiceImpl implements ImageMetaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageMetaServiceImpl.class);

    @Autowired
    ImageMetaRepository imageMetaRepository;

    @Override
    public ImageMeta save(ImageMeta imageMeta) {
        LOGGER.debug("Saving ImageMeta {}", imageMeta);
        imageMeta = imageMetaRepository.save(imageMeta);
        LOGGER.debug("ImageMeta saved. {}", imageMeta);
        return imageMeta;
    }

    @Override
    public Optional<ImageMeta> get(String imageId) {
        LOGGER.debug("Retrieve ImageMeta for id {}", imageId);
        return imageMetaRepository.findById(imageId);
    }

    @Override
    public void delete(String imageId) {
        LOGGER.debug("delete ImageMeta by id {}", imageId);
        imageMetaRepository.deleteById(imageId);
    }

}
