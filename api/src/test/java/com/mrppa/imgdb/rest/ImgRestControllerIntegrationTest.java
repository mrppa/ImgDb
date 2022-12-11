package com.mrppa.imgdb.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.mrppa.imgdb.ImgdbApplicationTests;
import com.mrppa.imgdb.meta.entities.ImageMetaStatus;
import com.mrppa.imgdb.model.ImgDbResponse;
import com.mrppa.imgdb.model.UiImageMeta;

@SpringBootTest(classes = ImgdbApplicationTests.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ImgRestControllerIntegrationTest {
	private final Logger LOGGER = LoggerFactory.getLogger(ImgRestControllerIntegrationTest.class);

	@LocalServerPort
	private int port;

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Test
	public void testSucessPath() {

		// create meta
		UiImageMeta uiImageMeta = new UiImageMeta();
		uiImageMeta.setDescription("Test Desc");
		var response = exchange("/api/v1/img/meta", HttpMethod.POST, uiImageMeta, "testKey");
		LOGGER.info("Response {}", response);
		assertTrue(response.getStatusCode().is2xxSuccessful());
		assertTrue(response.getBody().isSuccess());
		assertNotNull(response.getBody().getData().getImagId());
		assertEquals(ImageMetaStatus.CREATED, response.getBody().getData().getStatus());
		String imageId = response.getBody().getData().getImagId();

		// Upload Image
		var uploadResponse = uploadFile("/api/v1/img/" + imageId, HttpMethod.PUT, "Sample Image Content".getBytes(),
				"testKey");
		LOGGER.info("Response {}", uploadResponse);
		assertTrue(response.getStatusCode().is2xxSuccessful());

		// Get Metadata
		var getResponse = exchange("/api/v1/img/meta/" + imageId, HttpMethod.GET, null, "testKey");
		LOGGER.info("Response {}", getResponse);
		assertTrue(getResponse.getStatusCode().is2xxSuccessful());
		assertEquals(imageId, getResponse.getBody().getData().getImagId());
		assertEquals("Test Desc", getResponse.getBody().getData().getDescription());
		assertEquals(ImageMetaStatus.ACTIVE, getResponse.getBody().getData().getStatus());
		UiImageMeta receivedUiImageMeta = getResponse.getBody().getData();

		// Update meta
		receivedUiImageMeta.setDescription("Test Desc1");
		var updateResponse = exchange("/api/v1/img/meta/" + imageId, HttpMethod.PUT, receivedUiImageMeta, "testKey");
		LOGGER.info("Response {}", updateResponse);
		assertTrue(response.getStatusCode().is2xxSuccessful());
		assertTrue(response.getBody().isSuccess());

		// Get Metadata
		var getResponse2 = exchange("/api/v1/img/meta/" + imageId, HttpMethod.GET, null, "testKey");
		LOGGER.info("Response {}", getResponse2);
		assertTrue(getResponse2.getStatusCode().is2xxSuccessful());
		assertEquals(imageId, getResponse2.getBody().getData().getImagId());
		assertEquals("Test Desc1", getResponse2.getBody().getData().getDescription());
		assertEquals(ImageMetaStatus.ACTIVE, getResponse2.getBody().getData().getStatus());

		// download file
		var respFileContent = downloadFile(getResponse2.getBody().getData().getImageUrl(), HttpMethod.GET, "testKey");
		LOGGER.info("Response {}", respFileContent);
		assertTrue(respFileContent.getStatusCode().is2xxSuccessful());
		assertEquals("Sample Image Content", new String(respFileContent.getBody()));

		// delete
		var deleteResponse = exchange("/api/v1/img/" + imageId, HttpMethod.DELETE, null, "testKey");
		LOGGER.info("Response {}", deleteResponse);
		assertTrue(deleteResponse.getStatusCode().is2xxSuccessful());

		// Get Metadata
		var getResponse3 = exchange("/api/v1/img/meta/" + imageId, HttpMethod.GET, null, "testKey");
		LOGGER.info("Response {}", getResponse3);
		assertTrue(getResponse3.getStatusCode().is2xxSuccessful());
		assertEquals(ImageMetaStatus.PENDING_DELETE, getResponse3.getBody().getData().getStatus());

	}

	private String createURLWithPort(String uri) {
		return "http://localhost:" + port + uri;
	}

	private ResponseEntity<ImgDbResponse<UiImageMeta>> exchange(String url, HttpMethod httpMethod, UiImageMeta reqBody,
			String userKey) {
		HttpHeaders headers = new HttpHeaders();
		if (userKey != null) {
			headers.add("userKey", userKey);
		}
		var entity = new HttpEntity<>(reqBody, headers);

		var response = restTemplate.exchange(createURLWithPort(url), httpMethod, entity,
				new ParameterizedTypeReference<ImgDbResponse<UiImageMeta>>() {
				});
		return response;
	}

	private ResponseEntity<Void> uploadFile(String url, HttpMethod httpMethod, byte[] fileContnt, String userKey) {
		HttpHeaders headers = new HttpHeaders();
		if (userKey != null) {
			headers.add("userKey", userKey);
		}
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, String> fileMap = new LinkedMultiValueMap<>();
		ContentDisposition contentDisposition = ContentDisposition.builder("form-data").name("file")
				.filename("sampleimg").build();
		fileMap.add(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString());
		HttpEntity<byte[]> fileEntity = new HttpEntity<>(fileContnt, fileMap);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", fileEntity);
		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		var response = restTemplate.exchange(createURLWithPort(url), httpMethod, requestEntity,
				new ParameterizedTypeReference<Void>() {
				});
		return response;
	}

	private ResponseEntity<byte[]> downloadFile(String url, HttpMethod httpMethod, String userKey) {
		HttpHeaders headers = new HttpHeaders();
		if (userKey != null) {
			headers.add("userKey", userKey);
		}
		var entity = new HttpEntity<>(null, headers);

		ResponseEntity<byte[]> fileContent = restTemplate.exchange(url, httpMethod, entity, byte[].class);
		return fileContent;
	}

}
