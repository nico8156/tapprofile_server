package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.util.UUID;

public record CreateConnectionHttpRequest(
		UUID scannerProfileId,
		String badgeToken) {
}
