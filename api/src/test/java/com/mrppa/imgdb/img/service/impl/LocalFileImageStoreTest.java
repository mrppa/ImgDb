package com.mrppa.imgdb.img.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;

class LocalFileImageStoreTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileImageStoreTest.class);
	static LocalFileImageStore localFileImageStore;

	@BeforeAll
	static void startUp() throws IOException {
		String tmpdir = Files.createTempDirectory("_tmpImageDir").toFile().getAbsolutePath();
		LOGGER.info("Running Local File Image test on {}", tmpdir);
		localFileImageStore = new LocalFileImageStore(tmpdir);
	}

	@Test
	void testStoreAndRetrive() throws ImageDbException {
		String content = "This is sample file content";
		localFileImageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		localFileImageStore.retriveImage(outStream, "f1.txt");
		String retrivedContent = new String(outStream.toByteArray());

		assertEquals(content, retrivedContent);
	}

	@Test
	void testStoreAndRetriveAndDelete() throws ImageDbException {
		String content = "This is sample file content";
		localFileImageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		localFileImageStore.retriveImage(outStream, "f1.txt");

		localFileImageStore.deleteImage("f1.txt");

		ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
		assertThrowsExactly(ImageFileNotFoundException.class, () -> {
			localFileImageStore.retriveImage(outStream1, "f1.txt");
		});
	}

	@Test
	void whenStoreAgainShouldOverWritten() throws ImageDbException {
		String content = "This is sample file content";
		localFileImageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

		String updatedContent = "This is sample file content";
		localFileImageStore.storeImage(new ByteArrayInputStream(updatedContent.getBytes()), "f1.txt");

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		localFileImageStore.retriveImage(outStream, "f1.txt");
		String retrivedContent = new String(outStream.toByteArray());

		assertEquals(updatedContent, retrivedContent);
	}

	@Test
	void whenRetriveNonExistingFileShouldFail() throws ImageDbException {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		assertThrowsExactly(ImageFileNotFoundException.class, () -> {
			localFileImageStore.retriveImage(outStream, "non_exists.txt");
		});
	}

	@Test
	void whenDeleteNonExistingFileShouldFail() throws ImageDbException {
		assertThrowsExactly(ImageFileNotFoundException.class, () -> {
			localFileImageStore.deleteImage("non_exists.txt");
		});
	}

}
