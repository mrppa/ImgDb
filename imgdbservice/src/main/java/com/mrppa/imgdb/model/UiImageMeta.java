package com.mrppa.imgdb.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.mrppa.imgdb.meta.entities.ImageMetaStatus;

import lombok.Data;

@Data
public class UiImageMeta {
	private String imagId;

	private String description;

	private ImageMetaStatus status;

	private LocalDateTime addedDate;

	private LocalDateTime updatedDate;

	private UiImageMetaAccess access = new UiImageMetaAccess();

	private String extension;
	
	private Map<String, String> properties = new HashMap<>();
	
	private String imageUrl;

}
