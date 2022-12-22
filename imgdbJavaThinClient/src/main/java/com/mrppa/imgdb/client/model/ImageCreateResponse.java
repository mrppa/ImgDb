package com.mrppa.imgdb.client.model;

import lombok.Data;

@Data
public class ImageCreateResponse {
    boolean success=false;
    String imageId;
    String imageUrl;
}
