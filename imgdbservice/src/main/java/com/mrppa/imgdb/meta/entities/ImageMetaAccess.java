package com.mrppa.imgdb.meta.entities;

import com.mrppa.imgdb.model.AccessMode;
import lombok.*;

@Data
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImageMetaAccess {
    @Builder.Default
    private AccessMode writeAccess = AccessMode.PUBLIC;
    @Builder.Default
    private AccessMode readAccess = AccessMode.PUBLIC;
}
