package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.PublicProfileHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetPublicProfileQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicProfileQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/profiles")
public class PublicProfileController {

	private final GetPublicProfileQueryHandler handler;

	public PublicProfileController(GetPublicProfileQueryHandler handler) {
		this.handler = handler;
	}

	@GetMapping("/{slug}")
	public ResponseEntity<?> getBySlug(@PathVariable String slug) {
		var result = handler.handle(new GetPublicProfileQuery(slug));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		var profile = result.getSuccess();
		return ResponseEntity.ok(new PublicProfileHttpResponse(
				profile.profileId(),
				profile.slug(),
				profile.displayName(),
				profile.role(),
				profile.headline(),
				profile.bio(),
				profile.publishedAt()));
	}
}
