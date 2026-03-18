package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.application.commandhandlers.RegisterProfileViewCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.RegisterProfileViewCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/profiles")
public class RegisterProfileViewController {

	private final RegisterProfileViewCommandHandler handler;

	public RegisterProfileViewController(RegisterProfileViewCommandHandler handler) {
		this.handler = handler;
	}

	@PostMapping("/{slug}/views")
	public ResponseEntity<?> registerView(@PathVariable String slug) {
		var result = handler.handle(new RegisterProfileViewCommand(slug));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
