package com.mrppa.imgdb.stresstest;

import com.mrppa.imgdb.client.service.ImgDBClientService;
import com.mrppa.imgdb.client.service.impl.ImgDBClientServiceImpl;
import com.mrppa.imgdb.stresstest.tests.ImageUploadTestRunner;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class StressTestApplication {
    private static final Logger logger = Logger.getLogger(StressTestApplication.class.getName());

    private static final ImgDBClientService imgDBClientService = new ImgDBClientServiceImpl("http://localhost:8080/api/v1");

    private static final int PARALLEL_THREADS = 10;
    private static final int COUNT = 1000;

    public static void main(String[] arg) throws InterruptedException {
        logger.info("running stress test");

        executeUpload(COUNT, PARALLEL_THREADS);
        logger.info("stress test completed");
    }

    public static void executeUpload(int count, int parallelThreads) throws InterruptedException {
        logger.info("running file Upload test for " + count + " with " + parallelThreads + " parallel threads");
        TestHandler testHandler = new TestHandler();
        testHandler.startTest();

        ExecutorService executorService = Executors.newFixedThreadPool(parallelThreads);
        for (int j = 0; j < count; j++) {
            Runnable task = new ImageUploadTestRunner(imgDBClientService, testHandler);
            executorService.execute(task);
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.HOURS);
        testHandler.endTest();

        logger.info("file upload test completed");
    }
}
