package com.mrppa.imgdb.img.service.impl;

import com.mrppa.imgdb.img.service.ImageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.nio.file.Files;

class LocalFileImageStoreTest extends ImageStoreTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalFileImageStoreTest.class);

    @Override
    ImageStore initImageStore() throws Exception {
        String tmpdir = Files.createTempDirectory("_tmpImageDir").toFile().getAbsolutePath();
        LOGGER.info("Running Local File Image test on {}", tmpdir);
        return new LocalFileImageStore(tmpdir);
    }
}
