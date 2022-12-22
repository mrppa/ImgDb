package com.mrppa.imgdb.stresstest.tests;

import com.mrppa.imgdb.client.exception.ImageDbClientException;
import com.mrppa.imgdb.client.service.ImgDBClientService;
import com.mrppa.imgdb.model.AccessMode;
import com.mrppa.imgdb.stresstest.TestHandler;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map;

public class ImageUploadTestRunner implements Runnable{
    private final ImgDBClientService imgDBClientService ;

    private final TestHandler testHandler;

    public ImageUploadTestRunner(ImgDBClientService imgDBClientService,TestHandler testHandler){
        this.imgDBClientService= imgDBClientService;
        this.testHandler=testHandler;
    }
    @Override
    public void run() {
        try {
            uploadImage();
            testHandler.markSuccess();
        } catch (FileNotFoundException | ImageDbClientException e) {
            testHandler.markFail();
        }
    }

    private void uploadImage() throws FileNotFoundException, ImageDbClientException {
        InputStream inpStream = getClass().getClassLoader().getResourceAsStream("testImages/small.jpg");
        imgDBClientService.storeImage(inpStream, Map.of(),"stressTestKey", AccessMode.PUBLIC,AccessMode.RESTRICTED);
    }
}
