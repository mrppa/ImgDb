package com.mrppa.imgdb.meta;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ContinuousFileUploadExecutor extends TimedExecutor {

	private String imagePath = "";

	RestTemplate restTemplate = new RestTemplate();

	private Resource testFile;

	public ContinuousFileUploadExecutor() {
		try {
			testFile = getTestFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void tick() {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", this.testFile);

		ResponseEntity<String> response = restTemplate.postForEntity("http://localhost:8080/api/v1/img/",
				new HttpEntity<>(body, headers), String.class);
	}

	private Resource getTestFile() throws IOException {
		Path testFile = Files.createTempFile("test-file", ".txt");
		System.out.println("Creating and Uploading Test File: " + testFile);
		Files.write(testFile, "Hello World !!, This is a test file.".getBytes());
		return new FileSystemResource(testFile.toFile());
	}

}
