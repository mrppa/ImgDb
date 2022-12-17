package com.mrppa.imgdb.model;

import lombok.*;

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
