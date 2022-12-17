package com.mrppa.imgdb.img.service.impl;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

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
    void testStoreAndRetrieve() throws ImageDbException {
        String content = "This is sample file content";
        localFileImageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        localFileImageStore.retrieveImage(outStream, "f1.txt");
        String retriedContent = outStream.toString();

        assertEquals(content, retriedContent);
    }

    @Test
    void testStoreAndRetrieveAndDelete() throws ImageDbException {
        String content = "This is sample file content";
        localFileImageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        localFileImageStore.retrieveImage(outStream, "f1.txt");

        localFileImageStore.deleteImage("f1.txt");

        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        assertThrowsExactly(ImageFileNotFoundException.class, () -> localFileImageStore.retrieveImage(outStream1, "f1.txt"));
    }

    @Test
    void whenStoreAgainShouldOverWritten() throws ImageDbException {
        String content = "This is sample file content";
        localFileImageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

        String updatedContent = "This is sample file content";
        localFileImageStore.storeImage(new ByteArrayInputStream(updatedContent.getBytes()), "f1.txt");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        localFileImageStore.retrieveImage(outStream, "f1.txt");
        String retrievedContent = outStream.toString();

        assertEquals(updatedContent, retrievedContent);
    }

    @Test
    void whenRetrieveNonExistingFileShouldFail() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        assertThrowsExactly(ImageFileNotFoundException.class, () -> localFileImageStore.retrieveImage(outStream, "non_exists.txt"));
    }

    @Test
    void whenDeleteNonExistingFileShouldFail() {
        assertThrowsExactly(ImageFileNotFoundException.class, () -> localFileImageStore.deleteImage("non_exists.txt"));
    }

}
