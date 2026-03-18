package com.nm.tapprofile.tapProfileContext.application.queries;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record GetDashboardResult(
		ProfileSummary profile,
		Metrics metrics,
		List<LeadItem> recentLeads) {
	public record ProfileSummary(
			UUID profileId,
			String slug,
			String displayName,
			String status) {
	}

	public record Metrics(
			int viewCount,
			int leadCount,
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
