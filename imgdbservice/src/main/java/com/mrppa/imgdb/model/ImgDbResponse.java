package com.mrppa.imgdb.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ImgDbResponse<T> {
	@Builder.Default
	private boolean success = true;
	private String message;
	private T data;
}
