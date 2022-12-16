package com.mrppa.imgdb.img.service;

import java.io.InputStream;
import java.io.OutputStream;

import com.mrppa.imgdb.exception.ImageDbException;

public interface ImageStore {
	void storeImage(InputStream inputStream, String fileName) throws ImageDbException;

	void retriveImage(OutputStream outputStream, String fileName) throws ImageDbException;

	void deleteImage(String fileName) throws ImageDbException;

	String generateURL(String imageId) throws ImageDbException;
}
