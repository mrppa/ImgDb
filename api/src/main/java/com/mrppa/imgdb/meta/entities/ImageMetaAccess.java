package com.mrppa.imgdb.meta.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
