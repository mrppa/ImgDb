package com.mrppa.imgdb.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.Data;

@Data
public abstract class TimedExecutor {
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	private int tps = 1;

	@Scheduled(fixedRate = 1000)
	public void scheduled() {
		LOGGER.info("Running");
		for (int i = 0; i < tps; i++) {
			tick();
		}
	}

	public abstract void tick();

}
