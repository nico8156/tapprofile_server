package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CaptureLeadHttpRequest;
import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.CaptureLeadHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CaptureLeadCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.CaptureLeadCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/profiles")
public class CaptureLeadController {

	private final CaptureLeadCommandHandler handler;

	public CaptureLeadController(CaptureLeadCommandHandler handler) {
		this.handler = handler;
	}

	@PostMapping("/{slug}/leads")
	public ResponseEntity<?> captureLead(
			@PathVariable String slug,
			@RequestBody CaptureLeadHttpRequest request) {
		var result = handler.handle(new CaptureLeadCommand(
				slug,
				request.firstName(),
				request.email(),
				request.message()));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure(), HttpStatus.BAD_REQUEST);
		}

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new CaptureLeadHttpResponse(result.getSuccess().value()));
	}
}
