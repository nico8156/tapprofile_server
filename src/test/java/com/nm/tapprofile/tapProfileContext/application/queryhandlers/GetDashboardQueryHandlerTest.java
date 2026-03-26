package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CaptureLeadCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.CaptureLeadCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.queries.GetDashboardQuery;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.LeadFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.MagicLinkFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeConnectionRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeLeadRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkEmailSender;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeMagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;

class GetDashboardQueryHandlerTest {

	@Test
	void should_return_dashboard_with_profile_and_recent_leads() {
		var profileRepository = new FakeProfileRepository();
		var leadRepository = new FakeLeadRepository();
		var profileViewRepository = new FakeProfileViewRepository();
		var connectionRepository = new FakeConnectionRepository();
		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				new FakeBadgeRepository(),
				new FakeMagicLinkRepository(),
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var createResult = createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"alex@example.com",
				"Backend developer",
				"I build useful products."));

		var profileId = createResult.getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				profileRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));
		publishHandler.handle(new PublishProfileCommand(profileId.value()));

		var captureLeadHandler1 = new CaptureLeadCommandHandler(
				profileRepository,
				leadRepository,
				new LeadFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));
		captureLeadHandler1.handle(new CaptureLeadCommand(
				"alex-martin",
				"Nina",
				"nina@example.com",
				"Hello Alex"));

		var captureLeadHandler2 = new CaptureLeadCommandHandler(
				profileRepository,
				leadRepository,
				new LeadFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T16:00:00Z"))));
		captureLeadHandler2.handle(new CaptureLeadCommand(
				"alex-martin",
				"Marc",
				"marc@example.com",
				"Let's talk soon"));

		var handler = new GetDashboardQueryHandler(
				profileRepository,
				connectionRepository,
				leadRepository,
				profileViewRepository);

		var result = handler.handle(new GetDashboardQuery(profileId.value()));

		assertTrue(result.isSuccess());

		var dashboard = result.getSuccess();
		assertEquals(profileId.value(), dashboard.profile().profileId());
		assertEquals("alex-martin", dashboard.profile().slug());
		assertEquals("Alex Martin", dashboard.profile().displayName());
		assertEquals("VISITOR", dashboard.profile().role());
		assertEquals("PUBLISHED", dashboard.profile().status());

		assertEquals(0, dashboard.metrics().connectionCount());
		assertEquals(2, dashboard.metrics().leadCount());
		assertEquals(0, dashboard.metrics().scanCount());
		assertEquals(2, dashboard.recentLeads().size());

		assertEquals("Marc", dashboard.recentLeads().get(0).firstName());
		assertEquals("marc@example.com", dashboard.recentLeads().get(0).email());
		assertEquals(Instant.parse("2026-03-17T16:00:00Z"), dashboard.recentLeads().get(0).createdAt());

		assertEquals("Nina", dashboard.recentLeads().get(1).firstName());
	}

	@Test
	void should_return_empty_leads_when_profile_has_no_leads() {
		var profileRepository = new FakeProfileRepository();
		var leadRepository = new FakeLeadRepository();
		var profileViewRepository = new FakeProfileViewRepository();
		var connectionRepository = new FakeConnectionRepository();
		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				new FakeBadgeRepository(),
				new FakeMagicLinkRepository(),
				new MagicLinkFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")), java.time.Duration.ofDays(30)),
				new FakeMagicLinkEmailSender(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var createResult = createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"alex@example.com",
				"Backend developer",
				"I build useful products."));

		var handler = new GetDashboardQueryHandler(
				profileRepository,
				connectionRepository,
				leadRepository,
				profileViewRepository);

		var result = handler.handle(new GetDashboardQuery(createResult.getSuccess().value()));

		assertTrue(result.isSuccess());

		var dashboard = result.getSuccess();
		assertEquals(0, dashboard.metrics().leadCount());
		assertTrue(dashboard.recentLeads().isEmpty());
		assertEquals("VISITOR", dashboard.profile().role());
		assertEquals("DRAFT", dashboard.profile().status());
	}

	@Test
	void should_fail_when_profile_does_not_exist() {
		var handler = new GetDashboardQueryHandler(
				new FakeProfileRepository(),
				new FakeConnectionRepository(),
				new FakeLeadRepository(),
				new FakeProfileViewRepository());

		var result = handler.handle(new GetDashboardQuery(
				UUID.fromString("11111111-1111-1111-1111-111111111111")));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotFoundError);
	}
}
