package com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.controllers;

import com.nm.tapprofile.tapProfileContext.adapters.primary.springboot.dto.GetDashboardHttpResponse;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetDashboardQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queries.GetDashboardQuery;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
public class GetDashboardController {

	private final GetDashboardQueryHandler handler;

	public GetDashboardController(GetDashboardQueryHandler handler) {
		this.handler = handler;
	}

	@GetMapping("/{profileId}/dashboard")
	public ResponseEntity<?> getDashboard(@PathVariable UUID profileId) {
		var result = handler.handle(new GetDashboardQuery(profileId));

		if (result.isFailure()) {
			return ApiExceptionHandler.toErrorResponse(result.getFailure());
		}

		var dashboard = result.getSuccess();

		return ResponseEntity.ok(new GetDashboardHttpResponse(
				new GetDashboardHttpResponse.ProfileSummary(
						dashboard.profile().profileId(),
						dashboard.profile().slug(),
						dashboard.profile().displayName(),
						dashboard.profile().status()),
				new GetDashboardHttpResponse.Metrics(
						dashboard.metrics().viewCount(),
						dashboard.metrics().leadCount(),
						dashboard.metrics().conversionRate()),
				dashboard.recentLeads().stream()
						.map(lead -> new GetDashboardHttpResponse.LeadItem(
								lead.leadId(),
								lead.firstName(),
								lead.email(),
								lead.message(),
								lead.createdAt()))
						.toList()));
	}
}
