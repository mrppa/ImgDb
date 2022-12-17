package com.mrppa.imgdb.meta.entities;

import com.mrppa.imgdb.model.AccessMode;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Data
@Embeddable
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetaAccess {

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccessMode writeAccess = AccessMode.PUBLIC;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AccessMode readAccess = AccessMode.PUBLIC;
}
