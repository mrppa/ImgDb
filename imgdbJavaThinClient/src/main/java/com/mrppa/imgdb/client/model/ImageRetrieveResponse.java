package com.mrppa.imgdb.client.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ImageRetrieveResponse {
    String imageId;
    String imageUrl;
    Map<String,String> metaDataProperties=new HashMap<>();
}
