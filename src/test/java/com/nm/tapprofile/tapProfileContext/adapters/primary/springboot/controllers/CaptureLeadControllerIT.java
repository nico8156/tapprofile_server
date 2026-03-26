package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CaptureLeadHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpRequest;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CaptureLeadControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void should_capture_lead_for_published_profile() throws Exception {
		var createRequest = new CreateProfileHttpRequest(
				"alex-lead",
				"Alex Lead",
				"alex-lead@example.com",
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

		var request = new CaptureLeadHttpRequest(
				"Nina",
				"nina@example.com",
				"Hello Alex");

		mockMvc.perform(post("/api/public/profiles/alex-lead/leads")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.leadId").exists());
	}
}
