package com.mrppa.imgdb.meta.repositories;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.ImageMetaStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface ImageMetaRepository {
    void insert(ImageMeta imageMeta);

    void update(ImageMeta imageMeta);

    Optional<ImageMeta> findById(String imageId);

    void deleteById(String imageId);

    List<ImageMeta> listByStatusAndUpdatedDateBefore(ImageMetaStatus imageMetaStatus, LocalDateTime updatedDate,
                                                      int recordLimit);
}
