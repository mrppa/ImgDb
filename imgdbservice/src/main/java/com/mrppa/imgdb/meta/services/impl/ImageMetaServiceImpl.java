package com.mrppa.imgdb.meta.services.impl;

import com.github.philippheuer.snowflake4j.SnowflakeGenerator;
import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.meta.repositories.ImageMetaRepository;
import com.mrppa.imgdb.meta.services.ImageMetaService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
public class ImageMetaServiceImpl implements ImageMetaService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ImageMetaServiceImpl.class);

    @Autowired
    ImageMetaRepository imageMetaRepository;

    @PostConstruct
    void init(){
        SnowflakeGenerator.setInstance(SnowflakeGenerator.builder().epochOffset(0L).nodeId(1).build());
    }

    @Override
    public ImageMeta insert(ImageMeta imageMeta) {
        LOGGER.debug("Inserting ImageMeta {}", imageMeta);

        String imageId = Long.toString(SnowflakeGenerator.getInstance().nextSnowflake().getId());
        imageMeta.setImageId(imageId);

        imageMeta.setAddedDate(LocalDateTime.now());
        imageMeta.setUpdatedDate(imageMeta.getAddedDate());

        imageMetaRepository.insert(imageMeta);
        LOGGER.debug("ImageMeta inserted. {}", imageMeta);
        return imageMeta;
    }

    @Override
    public void update(ImageMeta imageMeta) {
        LOGGER.debug("Updating ImageMeta {}", imageMeta);

        imageMeta.setUpdatedDate(LocalDateTime.now());

        imageMetaRepository.update(imageMeta);
        LOGGER.debug("ImageMeta updated. {}", imageMeta);
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
