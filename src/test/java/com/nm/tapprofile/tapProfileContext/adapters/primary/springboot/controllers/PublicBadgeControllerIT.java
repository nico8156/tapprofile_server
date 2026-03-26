package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicBadgeControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BadgeRepository badgeRepository;

	@Test
	void should_return_public_badge() throws Exception {
		var createRequest = new CreateProfileHttpRequest(
				"alex-badge-it",
				"Alex Badge IT",
				"alex-badge-it@example.com",
				"EXHIBITOR",
				"Backend developer",
				"I share my badge.");

		var createResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode json = objectMapper.readTree(createResponse);
		UUID profileId = UUID.fromString(json.get("profileId").asText());

		mockMvc.perform(post("/api/profiles/" + profileId + "/publish"))
				.andExpect(status().isNoContent());

		var badgeToken = badgeRepository.findByProfileId(new ProfileId(profileId)).orElseThrow().badgeToken();

		mockMvc.perform(get("/api/public/badges/" + badgeToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.slug").value("alex-badge-it"))
				.andExpect(jsonPath("$.displayName").value("Alex Badge IT"))
				.andExpect(jsonPath("$.role").value("EXHIBITOR"))
				.andExpect(jsonPath("$.badge.badgeToken").value(badgeToken))
				.andExpect(jsonPath("$.badge.status").value("ACTIVE"));
	}
}
