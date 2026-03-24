package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateConnectionHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpRequest;
import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	@Autowired
	private ConnectionRepository connectionRepository;

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

	@Test
	void should_be_idempotent_for_same_pair_when_scanned_in_reverse() throws Exception {
		var firstRequest = new CreateProfileHttpRequest(
				"first-connection-it",
				"First Connection IT",
				"Backend developer",
				"I scan badges.");

		var firstResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(firstRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode firstJson = objectMapper.readTree(firstResponse);
		UUID firstProfileId = UUID.fromString(firstJson.get("profileId").asText());

		var secondRequest = new CreateProfileHttpRequest(
				"second-connection-it",
				"Second Connection IT",
				"Product engineer",
				"I also scan badges.");

		var secondResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(secondRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode secondJson = objectMapper.readTree(secondResponse);
		UUID secondProfileId = UUID.fromString(secondJson.get("profileId").asText());

		mockMvc.perform(post("/api/profiles/" + firstProfileId + "/publish"))
				.andExpect(status().isNoContent());
		mockMvc.perform(post("/api/profiles/" + secondProfileId + "/publish"))
				.andExpect(status().isNoContent());

		var firstBadgeToken = badgeRepository.findByProfileId(new ProfileId(firstProfileId)).orElseThrow().badgeToken();
		var secondBadgeToken = badgeRepository.findByProfileId(new ProfileId(secondProfileId)).orElseThrow().badgeToken();

		var firstConnectionResponse = mockMvc.perform(post("/api/connections")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new CreateConnectionHttpRequest(firstProfileId, secondBadgeToken))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.connectionId").exists())
				.andReturn()
				.getResponse()
				.getContentAsString();

		var secondConnectionResponse = mockMvc.perform(post("/api/connections")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new CreateConnectionHttpRequest(secondProfileId, firstBadgeToken))))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.connectionId").exists())
				.andReturn()
				.getResponse()
				.getContentAsString();

		var firstConnectionJson = objectMapper.readTree(firstConnectionResponse);
		var secondConnectionJson = objectMapper.readTree(secondConnectionResponse);

		var firstConnectionId = UUID.fromString(firstConnectionJson.get("connectionId").asText());
		var secondConnectionId = UUID.fromString(secondConnectionJson.get("connectionId").asText());

		assertEquals(firstConnectionId, secondConnectionId);
		assertEquals(1, connectionRepository.findByProfileId(new ProfileId(firstProfileId)).size());
		assertEquals(1, connectionRepository.findByProfileId(new ProfileId(secondProfileId)).size());
	}
}
