package com.mrppa.imgdb.mappers;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.UiImageMeta;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ImageMetaMapper {
    ImageMeta convert(UiImageMeta uiImageMeta);

    UiImageMeta convert(ImageMeta imageMeta);
}
