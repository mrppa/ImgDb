package com.mrppa.imgdb.meta.entities;

import com.mrppa.imgdb.model.ImageMetaStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageMeta {
    private String imageId;
    private String description;
    private ImageMetaStatus status;
    private LocalDateTime addedDate;
    private LocalDateTime updatedDate;
    private String hashedUserKey;
    @Builder.Default
    private ImageMetaAccess access = new ImageMetaAccess();
    private String extension;
    @Builder.Default
    private Map<String, String> properties = new HashMap<>();
}
