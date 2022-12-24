package com.mrppa.imgdb.client.service.impl;

import com.mrppa.imgdb.client.exception.ImageDbClientException;
import com.mrppa.imgdb.client.model.ImageCreateResponse;
import com.mrppa.imgdb.client.model.ImageRetrieveResponse;
import com.mrppa.imgdb.client.service.ImgDBClientService;
import com.mrppa.imgdb.model.AccessMode;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integrationTest")
class ImgDBClientServiceImplIntegrationTest {

    private final ImgDBClientService imgDBClientService = new ImgDBClientServiceImpl("http://localhost:8080/api/v1");

    private final Client client = ClientBuilder.newClient();

    @Test
    public void testSuccessPath() throws ImageDbClientException, IOException {

        //test image upload
        ImageCreateResponse imageCreateResponse = imgDBClientService.storeImage(new ByteArrayInputStream(file_content),
                Map.of("k1", "val1"), "myKey", AccessMode.PUBLIC, AccessMode.RESTRICTED);
        assertNotNull(imageCreateResponse);
        assertNotNull(imageCreateResponse.getImageId());
        assertNotNull(imageCreateResponse.getImageUrl());
        assertTrue(imageCreateResponse.isSuccess());

        //test get image meta
        ImageRetrieveResponse imageRetrieveResponse = imgDBClientService.getImage(imageCreateResponse.getImageId(), null);
        assertNotNull(imageRetrieveResponse);
        assertNotNull(imageRetrieveResponse.getImageId());
        assertNotNull(imageRetrieveResponse.getImageUrl());

        //test image Url
        Response imageRetrieval = client.target(imageRetrieveResponse.getImageUrl()).request(MediaType.APPLICATION_JSON).get();
        assertEquals(Response.Status.Family.SUCCESSFUL, Response.Status.fromStatusCode(imageRetrieval.getStatus()).getFamily());
        InputStream imageInputStream = imageRetrieval.readEntity(InputStream.class);
        byte[] retrievedImage = IOUtils.toByteArray(imageInputStream);
        assertArrayEquals(file_content, retrievedImage);

        //test image delete
        imgDBClientService.deleteImage(imageCreateResponse.getImageId(), "myKey");
    }

    @Test
    public void writeAccessDenyWhenReadPublicAndWriteAccessRestricted() throws ImageDbClientException {

        //upload image with read-public, write
        ImageCreateResponse imageCreateResponse = imgDBClientService.storeImage(new ByteArrayInputStream(file_content),
                Map.of("k1", "val1"), "myKey", AccessMode.PUBLIC, AccessMode.RESTRICTED);
        assertNotNull(imageCreateResponse);
        assertNotNull(imageCreateResponse.getImageId());
        assertNotNull(imageCreateResponse.getImageUrl());
        assertTrue(imageCreateResponse.isSuccess());

        //test get image meta
        ImageRetrieveResponse imageRetrieveResponse = imgDBClientService.getImage(imageCreateResponse.getImageId(), null);
        assertNotNull(imageRetrieveResponse);
        assertNotNull(imageRetrieveResponse.getImageId());
        assertNotNull(imageRetrieveResponse.getImageUrl());

        //test image delete
        assertThrowsExactly(ImageDbClientException.class, () -> imgDBClientService.deleteImage(imageCreateResponse.getImageId(), null));
    }

    @Test
    public void readWriteAccessDenyWhenReadRestrictedAndWriteAccessRestricted() throws ImageDbClientException {

        //upload image with read-public, write
        ImageCreateResponse imageCreateResponse = imgDBClientService.storeImage(new ByteArrayInputStream(file_content),
                Map.of("k1", "val1"), "myKey", AccessMode.RESTRICTED, AccessMode.RESTRICTED);
        assertNotNull(imageCreateResponse);
        assertNotNull(imageCreateResponse.getImageId());
        assertNotNull(imageCreateResponse.getImageUrl());
        assertTrue(imageCreateResponse.isSuccess());

        //test get image meta
        assertThrowsExactly(ImageDbClientException.class, () -> imgDBClientService.getImage(imageCreateResponse.getImageId(), null));

        //test image delete
        assertThrowsExactly(ImageDbClientException.class, () -> imgDBClientService.deleteImage(imageCreateResponse.getImageId(), null));
    }

    private static final byte[] file_content = "This is a sample content".getBytes();

}