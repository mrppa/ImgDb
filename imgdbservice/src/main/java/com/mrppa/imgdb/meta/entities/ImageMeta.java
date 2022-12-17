package com.mrppa.imgdb.meta.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageMeta {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String imageId;

    private String description;

    @Enumerated
    private ImageMetaStatus status;

    @CreationTimestamp
    private LocalDateTime addedDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @Version
    private int version;

    @Column
    private String hashedUserKey;

    @Column(insertable = false)
    @Builder.Default
    private ImageMetaAccess access = new ImageMetaAccess();

    private String extension;

    @Builder.Default
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, String> properties = new HashMap<>();

}
