package com.mrppa.imgdb.img.service;

import com.mrppa.imgdb.exception.ImageDbException;

import java.io.InputStream;
import java.io.OutputStream;

public interface ImageStore {
    void storeImage(InputStream inputStream, String fileName) throws ImageDbException;

    void retrieveImage(OutputStream outputStream, String fileName) throws ImageDbException;

    void deleteImage(String fileName) throws ImageDbException;

    String generateURL(String imageId) throws ImageDbException;
}
