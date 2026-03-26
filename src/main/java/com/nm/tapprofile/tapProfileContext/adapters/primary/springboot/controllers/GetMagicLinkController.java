package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.GetMagicLinkHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetMagicLinkQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetMagicLinkQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/magic-link")
public class GetMagicLinkController {

	private final GetMagicLinkQueryHandler handler;

	public GetMagicLinkController(GetMagicLinkQueryHandler handler) {
		this.handler = handler;
	}

	@GetMapping("/{token}")
	public ResponseEntity<?> getByToken(@PathVariable UUID token) {
		var result = handler.handle(new GetMagicLinkQuery(token));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		var success = result.getSuccess();
		return ResponseEntity.ok(new GetMagicLinkHttpResponse(
				new GetMagicLinkHttpResponse.ProfileItem(
						success.profile().profileId(),
						success.profile().slug(),
						success.profile().displayName(),
						success.profile().email(),
						success.profile().role(),
						success.profile().status()),
				success.contacts().stream()
						.map(contact -> new GetMagicLinkHttpResponse.ContactItem(
								contact.profileId(),
								contact.displayName(),
								contact.headline(),
								contact.role(),
								contact.createdAt()))
						.toList()));
	}
}
