package com.mrppa.imgdb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.img.service.impl.LocalFileImageStore;

@Configuration
@Profile("test")
public class TestConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10, new SecureRandom());
	}

	@Bean
	public ImageStore imageStore() throws IOException {
		File tempDir = Files.createTempDirectory("foobar").toFile();
		String userDirectory = tempDir.getAbsolutePath().toString();
		return new LocalFileImageStore(userDirectory);
	}
}
