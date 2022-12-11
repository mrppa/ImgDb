package com.mrppa.imgdb;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.img.service.impl.LocalFileImageStore;

@Configuration
public class Config {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new Pbkdf2PasswordEncoder();
	}

	@Bean
	public ImageStore imageStore() throws IOException {
		File tempDir = Files.createTempDirectory("foobar").toFile();
		String userDirectory = tempDir.getAbsolutePath().toString();
		return new LocalFileImageStore(userDirectory);
	}
}
