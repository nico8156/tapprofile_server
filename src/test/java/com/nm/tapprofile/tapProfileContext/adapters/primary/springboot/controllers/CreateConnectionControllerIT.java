package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateConnectionHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpRequest;
import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CreateConnectionControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BadgeRepository badgeRepository;

	@Test
	void should_create_connection() throws Exception {
		var scannerRequest = new CreateProfileHttpRequest(
				"scanner-it",
				"Scanner IT",
				"Backend developer",
				"I scan badges.");

		var scannerResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(scannerRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode scannerJson = objectMapper.readTree(scannerResponse);
		UUID scannerProfileId = UUID.fromString(scannerJson.get("profileId").asText());

		var scannedRequest = new CreateProfileHttpRequest(
				"scanned-it",
				"Scanned IT",
				"Product engineer",
				"I get scanned.");

		var scannedResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(scannedRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode scannedJson = objectMapper.readTree(scannedResponse);
		UUID scannedProfileId = UUID.fromString(scannedJson.get("profileId").asText());

		mockMvc.perform(post("/api/profiles/" + scannedProfileId + "/publish"))
				.andExpect(status().isNoContent());

		var badgeToken = badgeRepository.findByProfileId(new ProfileId(scannedProfileId)).orElseThrow().badgeToken();

		var connectionRequest = new CreateConnectionHttpRequest(scannerProfileId, badgeToken);

		mockMvc.perform(post("/api/connections")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(connectionRequest)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.connectionId").exists());
	}
}
