package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.GetConnectionsHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetConnectionsQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetConnectionsQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class GetConnectionsController {

	private final GetConnectionsQueryHandler handler;

	public GetConnectionsController(GetConnectionsQueryHandler handler) {
		this.handler = handler;
	}

	@GetMapping("/{profileId}/connections")
	public ResponseEntity<?> getConnections(@PathVariable UUID profileId) {
		var result = handler.handle(new GetConnectionsQuery(profileId));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		return ResponseEntity.ok(result.getSuccess().connections().stream()
				.map(connection -> new GetConnectionsHttpResponse(
						connection.profileId(),
						connection.displayName(),
						connection.headline(),
						connection.role(),
						connection.createdAt()))
				.toList());
	}
}
