package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.GetProfileBadgeHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetProfileBadgeQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetProfileBadgeQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class GetProfileBadgeController {

	private final GetProfileBadgeQueryHandler handler;

	public GetProfileBadgeController(GetProfileBadgeQueryHandler handler) {
		this.handler = handler;
	}

	@GetMapping("/{profileId}/badge")
	public ResponseEntity<?> getProfileBadge(@PathVariable UUID profileId) {
		var result = handler.handle(new GetProfileBadgeQuery(profileId));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		var badge = result.getSuccess();

		return ResponseEntity.ok(new GetProfileBadgeHttpResponse(
				badge.badgeToken(),
				badge.publicBadgeUrl()));
	}
}
