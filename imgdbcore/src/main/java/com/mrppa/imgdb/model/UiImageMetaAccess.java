package com.mrppa.imgdb.model;

import lombok.Data;

@Data
public class UiImageMetaAccess {
    private AccessMode writeAccess = AccessMode.PUBLIC;

    private AccessMode readAccess = AccessMode.PUBLIC;
}
