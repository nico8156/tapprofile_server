package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateConnectionHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryMagicLinkEmailSender;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GetMagicLinkControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private BadgeRepository badgeRepository;

	@Autowired
	private InMemoryMagicLinkEmailSender magicLinkEmailSender;

	@Test
	void should_return_profile_and_contacts_from_magic_link() throws Exception {
		int initialEmailCount = magicLinkEmailSender.sentEmails().size();

		var ownerRequest = new CreateProfileHttpRequest(
				"magic-link-owner-it",
				"Magic Link Owner IT",
				"magic-link-owner-it@example.com",
				"Backend developer",
				"I retrieve my dashboard.");

		var ownerResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ownerRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode ownerJson = objectMapper.readTree(ownerResponse);
		UUID ownerProfileId = UUID.fromString(ownerJson.get("profileId").asText());

		var contactRequest = new CreateProfileHttpRequest(
				"magic-link-contact-it",
				"Magic Link Contact IT",
				"magic-link-contact-it@example.com",
				"Product engineer",
				"I appear in contacts.");

		var contactResponse = mockMvc.perform(post("/api/profiles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(contactRequest)))
				.andReturn()
				.getResponse()
				.getContentAsString();

		JsonNode contactJson = objectMapper.readTree(contactResponse);
		UUID contactProfileId = UUID.fromString(contactJson.get("profileId").asText());

		mockMvc.perform(post("/api/profiles/" + contactProfileId + "/publish"))
				.andExpect(status().isNoContent());

		var badgeToken = badgeRepository.findByProfileId(new ProfileId(contactProfileId)).orElseThrow().badgeToken();

		mockMvc.perform(post("/api/connections")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(new CreateConnectionHttpRequest(ownerProfileId, badgeToken))))
				.andExpect(status().isCreated());

		assertEquals(initialEmailCount + 2, magicLinkEmailSender.sentEmails().size());
		var token = magicLinkEmailSender.sentEmails().get(initialEmailCount).token();

		mockMvc.perform(get("/api/magic-link/" + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.profile.profileId").value(ownerProfileId.toString()))
				.andExpect(jsonPath("$.profile.email").value("magic-link-owner-it@example.com"))
				.andExpect(jsonPath("$.contacts[0].profileId").value(contactProfileId.toString()))
				.andExpect(jsonPath("$.contacts[0].displayName").value("Magic Link Contact IT"));
	}

	@Test
	void should_return_not_found_for_unknown_magic_link() throws Exception {
		mockMvc.perform(get("/api/magic-link/" + UUID.randomUUID()))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.errors[0].code").value("magic_link.not_found"));
	}
}
