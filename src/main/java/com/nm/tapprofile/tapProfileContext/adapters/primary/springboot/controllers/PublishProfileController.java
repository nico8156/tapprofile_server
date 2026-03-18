package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class PublishProfileController {

	private final PublishProfileCommandHandler handler;

	public PublishProfileController(PublishProfileCommandHandler handler) {
		this.handler = handler;
	}

	@PostMapping("/{profileId}/publish")
	public ResponseEntity<?> publish(@PathVariable UUID profileId) {
		var result = handler.handle(new PublishProfileCommand(profileId));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
}
