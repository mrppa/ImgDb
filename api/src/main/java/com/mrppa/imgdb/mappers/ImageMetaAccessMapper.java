package com.mrppa.imgdb.mappers;

import org.mapstruct.Mapper;

import com.mrppa.imgdb.meta.entities.ImageMetaAccess;
import com.mrppa.imgdb.model.UiImageMetaAccess;

@Mapper(componentModel = "spring")
public interface ImageMetaAccessMapper {
	ImageMetaAccess convert(UiImageMetaAccess uiImageMetaAccess);

	UiImageMetaAccess convert(ImageMetaAccess imageMetaAccess);
}
