package com.mrppa.imgdb.mappers;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.UiImageMeta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageMetaMapper {

    @Mapping(target = "hashedUserKey", ignore = true)
    ImageMeta convert(UiImageMeta uiImageMeta);

    @Mapping(target = "imageUrl", ignore = true)
    UiImageMeta convert(ImageMeta imageMeta);
}
