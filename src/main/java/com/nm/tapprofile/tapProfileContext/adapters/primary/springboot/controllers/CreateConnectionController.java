package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateConnectionHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CreateConnectionHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateConnectionCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateConnectionCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/connections")
public class CreateConnectionController {

	private final CreateConnectionCommandHandler handler;

	public CreateConnectionController(CreateConnectionCommandHandler handler) {
		this.handler = handler;
	}

	@PostMapping
	public ResponseEntity<?> create(@RequestBody CreateConnectionHttpRequest request) {
		var result = handler.handle(new CreateConnectionCommand(
				request.scannerProfileId(),
				request.badgeToken()));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new CreateConnectionHttpResponse(result.getSuccess().value()));
	}
}
