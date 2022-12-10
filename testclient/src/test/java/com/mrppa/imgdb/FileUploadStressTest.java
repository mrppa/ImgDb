package com.mrppa.imgdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.mrppa.imgdb.meta.ContinuousFileUploadExecutor;
import com.mrppa.imgdb.meta.TimedExecutor;

@SpringBootTest
class FileUploadStressTest {

	@Autowired
	TimedExecutor timedExecutor;

	@TestConfiguration
	static class TestConfig {

		@Bean
		public TimedExecutor timedExecutor() {
			ContinuousFileUploadExecutor timedExecutor = new ContinuousFileUploadExecutor();
			timedExecutor.setTps(100);
			return timedExecutor;
		}
	}

	@Test
	void test() throws InterruptedException {
		Thread.sleep(50000);
	}

}
