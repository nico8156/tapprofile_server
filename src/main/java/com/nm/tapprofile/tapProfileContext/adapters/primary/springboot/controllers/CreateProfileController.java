package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateProfileHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;

@RestController
@RequestMapping("/api/profiles")
public class CreateProfileController {

	private final CreateProfileCommandHandler handler;

	public CreateProfileController(CreateProfileCommandHandler handler) {
		this.handler = handler;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody CreateProfileHttpRequest request) {
		var result = handler.handle(new CreateProfileCommand(
				request.slug(),
				request.displayName(),
				request.headline(),
				request.bio()));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure(), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new CreateProfileHttpResponse(result.getSuccess().value()));
	}
}
