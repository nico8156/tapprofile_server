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
class GetProfileBadgeControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BadgeRepository badgeRepository;

	@Test
	void should_return_profile_badge() throws Exception {
		var createRequest = new CreateProfileHttpRequest(
				"alex-profile-badge-it",
				"Alex Profile Badge IT",
				"alex-profile-badge-it@example.com",
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

		var badgeToken = badgeRepository.findByProfileId(new ProfileId(profileId)).orElseThrow().badgeToken();

		mockMvc.perform(get("/api/profiles/" + profileId + "/badge"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.badgeToken").value(badgeToken))
				.andExpect(jsonPath("$.publicBadgeUrl").value("http://localhost:3000/b/" + badgeToken));
	}
}
