package com.mrppa.imgdb.client.service;

import com.mrppa.imgdb.client.exception.ImageDbClientException;
import com.mrppa.imgdb.client.model.ImageCreateResponse;
import com.mrppa.imgdb.client.model.ImageRetrieveResponse;
import com.mrppa.imgdb.model.AccessMode;

import java.io.InputStream;
import java.util.Map;

public interface ImgDBClientService {
    ImageCreateResponse storeImage(InputStream imageStream, Map<String, String> metaProperties,String userKey, AccessMode readAccessMode, AccessMode writeAccessMode) throws ImageDbClientException;
    ImageRetrieveResponse getImage(String imageId, String userKey) throws ImageDbClientException;
    void deleteImage(String imageId,String userKey) throws ImageDbClientException;

}
