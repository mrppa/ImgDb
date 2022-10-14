package com.mrppa.imgdb;

import java.nio.file.FileSystems;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;

import com.mrppa.imgdb.img.service.ImageStore;
import com.mrppa.imgdb.img.service.LocalFileImageStore;

@Configuration
public class Config {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new Pbkdf2PasswordEncoder();
	}

	@Bean
	public ImageStore imageStore() {
		String userDirectory = FileSystems.getDefault().getPath("images").toAbsolutePath().toString();
		return new LocalFileImageStore(userDirectory);
	}
}
