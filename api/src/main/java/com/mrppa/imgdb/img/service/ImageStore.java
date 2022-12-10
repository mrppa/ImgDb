package com.mrppa.imgdb.img.service;

import java.io.InputStream;
import java.io.OutputStream;

import com.mrppa.imgdb.exception.ImageDbException;

public interface ImageStore {
	public void storeImage(InputStream inputStream, String fileName) throws ImageDbException;

	public void retriveImage(OutputStream outputStream, String fileName) throws ImageDbException;

	public void deleteImage(String fileName) throws ImageDbException;
}
