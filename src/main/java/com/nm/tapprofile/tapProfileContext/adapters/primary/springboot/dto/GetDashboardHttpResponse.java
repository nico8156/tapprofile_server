package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetDashboardHttpResponse(
		ProfileSummary profile,
		Metrics metrics,
		List<LeadItem> recentLeads) {
	public record ProfileSummary(
			UUID profileId,
			String slug,
			String displayName,
			String role,
			String status) {
	}

	public record Metrics(
			int viewCount,
			int scanCount,
			int leadCount,
			int connectionCount,
			double conversionRate) {
	}

	public record LeadItem(
			UUID leadId,
			String firstName,
			String email,
			String message,
			Instant createdAt) {
	}
}
