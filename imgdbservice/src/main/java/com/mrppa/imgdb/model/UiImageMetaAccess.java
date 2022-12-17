package com.mrppa.imgdb.model;

import com.mrppa.imgdb.meta.entities.AccessMode;
import lombok.Data;

@Data
public class UiImageMetaAccess {
    private AccessMode writeAccess = AccessMode.PUBLIC;

    private AccessMode readAccess = AccessMode.PUBLIC;
}
