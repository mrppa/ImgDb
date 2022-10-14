package com.mrppa.imgdb.meta.entities;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import lombok.Data;

@Data
@Embeddable
public class ImageMetaAccess {
	
	@Enumerated(EnumType.STRING)
	private AccessMode writeAccess = AccessMode.PUBLIC;
	
	@Enumerated(EnumType.STRING)
	private AccessMode readAccess = AccessMode.PUBLIC;
}
