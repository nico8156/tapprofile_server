package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpRequest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PublicProfileControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void should_return_published_public_profile() throws Exception {
		var createRequest = new CreateProfileHttpRequest(
				"alex-public",
				"Alex Public",
				"Backend developer",
				"I build useful products.");

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

		mockMvc.perform(get("/api/public/profiles/alex-public"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.slug").value("alex-public"))
				.andExpect(jsonPath("$.displayName").value("Alex Public"))
				.andExpect(jsonPath("$.role").value("VISITOR"));
	}

	@Test
	void should_return_not_found_for_draft_profile() throws Exception {
		var createRequest = new CreateProfileHttpRequest(
				"alex-draft-public",
				"Alex Draft",
				"Backend developer",
				"I build useful products.");

		mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/public/profiles/alex-draft-public"))
				.andExpect(status().isNotFound());
	}
}
