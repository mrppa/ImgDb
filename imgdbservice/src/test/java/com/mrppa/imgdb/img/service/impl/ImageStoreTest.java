package com.mrppa.imgdb.img.service.impl;

import com.mrppa.imgdb.exception.ImageDbException;
import com.mrppa.imgdb.exception.ImageFileNotFoundException;
import com.mrppa.imgdb.img.service.ImageStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ImageStoreTest {
    protected ImageStore imageStore;

    abstract ImageStore initImageStore() throws Exception;

    @BeforeAll
    void startUp() throws Exception {
        this.imageStore=initImageStore();
    }

    @Test
    void testStoreAndRetrieve() throws ImageDbException {
        String content = "This is sample file content";
        imageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        imageStore.retrieveImage(outStream, "f1.txt");
        String retriedContent = outStream.toString();

        assertEquals(content, retriedContent);
    }

    @Test
    void testStoreAndRetrieveAndDelete() throws ImageDbException {
        String content = "This is sample file content";
        imageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        imageStore.retrieveImage(outStream, "f1.txt");

        imageStore.deleteImage("f1.txt");

        ByteArrayOutputStream outStream1 = new ByteArrayOutputStream();
        assertThrowsExactly(ImageFileNotFoundException.class, () -> imageStore.retrieveImage(outStream1, "f1.txt"));
    }

    @Test
    void whenStoreAgainShouldOverWritten() throws ImageDbException {
        String content = "This is sample file content";
        imageStore.storeImage(new ByteArrayInputStream(content.getBytes()), "f1.txt");

        String updatedContent = "This is sample file content";
        imageStore.storeImage(new ByteArrayInputStream(updatedContent.getBytes()), "f1.txt");

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        imageStore.retrieveImage(outStream, "f1.txt");
        String retrievedContent = outStream.toString();

        assertEquals(updatedContent, retrievedContent);
    }

    @Test
    void whenRetrieveNonExistingFileShouldFail() {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        assertThrowsExactly(ImageFileNotFoundException.class, () -> imageStore.retrieveImage(outStream, "non_exists.txt"));
    }

    @Test
    void whenDeleteNonExistingFileShouldFail() {
        assertThrowsExactly(ImageFileNotFoundException.class, () -> imageStore.deleteImage("non_exists.txt"));
    }

}
