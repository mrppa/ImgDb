package com.mrppa.imgdb.meta.entities;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class ImageMetaAccess {
	private AccessMode writeAccess = AccessMode.PUBLIC;
	private AccessMode readAccess = AccessMode.PUBLIC;
}
