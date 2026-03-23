package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.PublicBadgeHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetPublicBadgeQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicBadgeQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/badges")
public class PublicBadgeController {

	private final GetPublicBadgeQueryHandler handler;

	public PublicBadgeController(GetPublicBadgeQueryHandler handler) {
		this.handler = handler;
	}

	@GetMapping("/{badgeToken}")
	public ResponseEntity<?> getByBadgeToken(@PathVariable String badgeToken) {
		var result = handler.handle(new GetPublicBadgeQuery(badgeToken));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		var badge = result.getSuccess();

		return ResponseEntity.ok(new PublicBadgeHttpResponse(
				badge.slug(),
				badge.displayName(),
				badge.role(),
				badge.headline(),
				badge.bio(),
				badge.publishedAt(),
				new PublicBadgeHttpResponse.BadgeSummary(
						badge.badge().badgeToken(),
						badge.badge().status(),
						badge.badge().createdAt())));
	}
}
