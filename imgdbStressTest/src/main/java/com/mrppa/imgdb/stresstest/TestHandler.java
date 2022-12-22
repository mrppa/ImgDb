package com.mrppa.imgdb.stresstest;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TestHandler {
    private static final Logger logger = Logger.getLogger(TestHandler.class.getName());

    private long startTime;

    private final AtomicInteger successCount=new AtomicInteger();
    private final AtomicInteger failCount=new AtomicInteger();

    public void startTest(){
        startTime = System.nanoTime();
    }

    public void markSuccess(){
        successCount.getAndIncrement();
    }
    public void markFail(){
        failCount.getAndIncrement();
    }

    public void endTest(){
        long elapsedTime = System.nanoTime() - startTime;
        logger.info("Test Completed in millis: " + elapsedTime / 1000000);
        logger.info("\t success count: " + successCount);
        logger.info("\t fail count: " + failCount);
    }
}
