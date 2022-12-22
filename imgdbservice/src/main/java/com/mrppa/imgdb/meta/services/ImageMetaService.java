package com.mrppa.imgdb.meta.services;

import com.mrppa.imgdb.meta.entities.ImageMeta;

import java.util.Optional;

public interface ImageMetaService {

    ImageMeta insert(ImageMeta imageMeta);

    void update(ImageMeta imageMeta);

    Optional<ImageMeta> get(String imageId);

    void delete(String imageId);
}
