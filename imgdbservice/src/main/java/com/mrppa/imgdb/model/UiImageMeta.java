package com.mrppa.imgdb.model;

import com.mrppa.imgdb.meta.entities.ImageMetaStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class UiImageMeta {
    private String imageId;

    private String description;

    private ImageMetaStatus status;

    private LocalDateTime addedDate;

    private LocalDateTime updatedDate;

    private UiImageMetaAccess access = new UiImageMetaAccess();

    private String extension;

    private Map<String, String> properties = new HashMap<>();

    private String imageUrl;

}
