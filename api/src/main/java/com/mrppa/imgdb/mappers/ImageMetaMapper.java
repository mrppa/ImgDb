package com.mrppa.imgdb.mappers;

import org.mapstruct.Mapper;

import com.mrppa.imgdb.meta.entities.ImageMeta;
import com.mrppa.imgdb.model.UiImageMeta;

@Mapper(componentModel = "spring")
public interface ImageMetaMapper {
	ImageMeta convert(UiImageMeta uiImageMeta);

	UiImageMeta convert(ImageMeta imageMeta);
}
