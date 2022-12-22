package com.mrppa.imgdb.client.service.impl;

import com.mrppa.imgdb.client.exception.ImageDbClientException;
import com.mrppa.imgdb.client.model.ImageCreateResponse;
import com.mrppa.imgdb.client.model.ImageDbResponseImageMeta;
import com.mrppa.imgdb.client.model.ImageRetrieveResponse;
import com.mrppa.imgdb.client.service.ImgDBClientService;
import com.mrppa.imgdb.model.AccessMode;
import com.mrppa.imgdb.model.ImgDbResponse;
import com.mrppa.imgdb.model.UiImageMeta;
import com.mrppa.imgdb.model.UiImageMetaAccess;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Logger;

public class ImgDBClientServiceImpl implements ImgDBClientService {

    private static final Logger logger = Logger.getLogger(ImgDBClientServiceImpl.class.getName());
    private final String baseUrl;
    private final Client client = ClientBuilder.newClient();

    public ImgDBClientServiceImpl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public ImageCreateResponse storeImage(InputStream imageStream, Map<String, String> metaProperties, String userKey, AccessMode readAccessMode, AccessMode writeAccessMode) throws ImageDbClientException {
        logger.fine("Store image with readAccessMode:" + readAccessMode + " writeAccessMode:" + writeAccessMode);
        ImageCreateResponse imageCreateResponse = new ImageCreateResponse();

        //Create Meta data
        UiImageMetaAccess uiImageMetaAccess = new UiImageMetaAccess();
        uiImageMetaAccess.setReadAccess(readAccessMode);
        uiImageMetaAccess.setWriteAccess(writeAccessMode);

        UiImageMeta uiImageMeta = new UiImageMeta();
        uiImageMeta.setAccess(uiImageMetaAccess);
        uiImageMeta.setProperties(metaProperties);

        ImgDbResponse<UiImageMeta> imgDbResponse;
        try (Response metaCreateResponse = client.target(baseUrl + "/img/meta").request(MediaType.APPLICATION_JSON).header("userKey", userKey).post(Entity.entity(uiImageMeta, MediaType.APPLICATION_JSON_TYPE))) {

            if (!Response.Status.fromStatusCode(metaCreateResponse.getStatus()).getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                throw new ImageDbClientException("Image meta creation failed with status " + metaCreateResponse.getStatus());
            }

            imgDbResponse = metaCreateResponse.readEntity(ImageDbResponseImageMeta.class);
        }
        imageCreateResponse.setImageUrl(imgDbResponse.getData().getImageUrl());
        imageCreateResponse.setImageId(imgDbResponse.getData().getImageId());


        //upload image
        File imageFile = createTemporaryUploadFile(imageStream, imgDbResponse.getData().getImageId());

        MultiPart multiPart = new MultiPart();
        FileDataBodyPart filePart = new FileDataBodyPart("file", imageFile, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        multiPart.bodyPart(filePart);

        try (Response imageUploadResponse = client.target(baseUrl + "/img/" + imageCreateResponse.getImageId()).request(MediaType.APPLICATION_JSON).header("userKey", userKey).put(Entity.entity(multiPart, MediaType.MULTIPART_FORM_DATA))) {

            if (!Response.Status.fromStatusCode(imageUploadResponse.getStatus()).getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                throw new ImageDbClientException("Image upload failed with status " + imageUploadResponse.getStatus());
            }
        }
        imageCreateResponse.setSuccess(true);
        logger.fine("store image successful. " + imageCreateResponse);
        return imageCreateResponse;
    }

    private File createTemporaryUploadFile(InputStream imageStream, String filePrefix) throws ImageDbClientException {
        File tempFile;

        try {
            tempFile = File.createTempFile("imgTemp" + filePrefix, null);
            tempFile.deleteOnExit();
        } catch (IOException e) {
            throw new ImageDbClientException("Error while creating temporary file ", e);
        }

        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(imageStream.readAllBytes());
        } catch (IOException e) {
            throw new ImageDbClientException("Error while writing image stream to temporary file ", e);
        }
        return tempFile;
    }

    @Override
    public ImageRetrieveResponse getImage(String imageId, String userKey) throws ImageDbClientException {
        logger.fine("retrieve image meta imageId:" + imageId);
        ImageRetrieveResponse imageRetrieveResponse = new ImageRetrieveResponse();

        Response getImageMetaResponse = client.target(baseUrl + "/img/meta/" + imageId).request(MediaType.APPLICATION_JSON).header("userKey", userKey).get();

        if (!Response.Status.fromStatusCode(getImageMetaResponse.getStatus()).getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
            throw new ImageDbClientException("Image get meta failed with status " + getImageMetaResponse.getStatus());
        }

        ImgDbResponse<UiImageMeta> imgDbResponse = getImageMetaResponse.readEntity(ImageDbResponseImageMeta.class);
        imageRetrieveResponse.setImageUrl(imgDbResponse.getData().getImageUrl());
        imageRetrieveResponse.setImageId(imgDbResponse.getData().getImageId());
        imageRetrieveResponse.setMetaDataProperties(imgDbResponse.getData().getProperties());

        logger.fine("Image retrieved imageRetrieveResponse:" + imageRetrieveResponse);
        return imageRetrieveResponse;
    }

    @Override
    public void deleteImage(String imageId, String userKey) throws ImageDbClientException {
        logger.fine("delete image meta imageId:" + imageId);

        try (Response deleteImageResponse = client.target(baseUrl + "/img/" + imageId).request(MediaType.APPLICATION_JSON).header("userKey", userKey).delete()) {

            if (!Response.Status.fromStatusCode(deleteImageResponse.getStatus()).getFamily().equals(Response.Status.Family.SUCCESSFUL)) {
                throw new ImageDbClientException("Image delete failed with status " + deleteImageResponse.getStatus());
            }
        }
        logger.fine("Image delete request successfully submitted");
    }
}
