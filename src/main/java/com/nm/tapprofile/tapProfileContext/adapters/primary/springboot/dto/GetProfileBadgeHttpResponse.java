package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

public record GetProfileBadgeHttpResponse(
		String badgeToken,
		String publicBadgeUrl) {
}
