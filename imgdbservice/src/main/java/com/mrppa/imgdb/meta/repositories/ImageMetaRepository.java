package com.mrppa.imgdb.meta.repositories;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import java.util.Optional;


public interface ImageMetaRepository {
    void insert(ImageMeta imageMeta);
    void update(ImageMeta imageMeta);
    Optional<ImageMeta> findById(String imageId);
    void deleteById(String imageId);
}
