package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateConnectionCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ConnectionAlreadySelfError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ConnectionFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeConnectionRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CreateConnectionCommandHandlerTest {

	@Test
	void should_create_connection_from_badge_token() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var connectionRepository = new FakeConnectionRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var scannerProfileId = createHandler.handle(new CreateProfileCommand(
				"scanner-profile",
				"Scanner Profile",
				"Backend developer",
				"I scan badges.")).getSuccess();

		var scannedProfileId = createHandler.handle(new CreateProfileCommand(
				"scanned-profile",
				"Scanned Profile",
				"Product engineer",
				"I get scanned.")).getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				profileRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));
		publishHandler.handle(new PublishProfileCommand(scannedProfileId.value()));

		var badgeToken = badgeRepository.findByProfileId(scannedProfileId).orElseThrow().badgeToken();

		var handler = new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				connectionRepository,
				new ConnectionFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CreateConnectionCommand(scannerProfileId.value(), badgeToken));

		assertTrue(result.isSuccess());
		assertNotNull(result.getSuccess());

		var connections = connectionRepository.findByProfileId(scannerProfileId);
		assertEquals(1, connections.size());
		assertEquals(scannerProfileId, connections.getFirst().scannerProfileId());
		assertEquals(scannedProfileId, connections.getFirst().scannedProfileId());
		assertEquals(Instant.parse("2026-03-17T15:00:00Z"), connections.getFirst().createdAt());
	}

	@Test
	void should_fail_when_badge_token_does_not_exist() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));
		var scannerProfileId = createHandler.handle(new CreateProfileCommand(
				"scanner-missing-badge",
				"Scanner Missing Badge",
				"Developer",
				"I need a badge token.")).getSuccess();

		var handler = new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				new FakeConnectionRepository(),
				new ConnectionFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CreateConnectionCommand(
				scannerProfileId.value(),
				"unknown-token"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof BadgeNotFoundError);
	}

	@Test
	void should_fail_when_scanning_own_badge() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var profileId = createHandler.handle(new CreateProfileCommand(
				"self-profile",
				"Self Profile",
				"Developer",
				"I scan myself.")).getSuccess();

		var badgeToken = badgeRepository.findByProfileId(profileId).orElseThrow().badgeToken();

		var handler = new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				new FakeConnectionRepository(),
				new ConnectionFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CreateConnectionCommand(profileId.value(), badgeToken));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ConnectionAlreadySelfError);
	}

	@Test
	void should_fail_when_scanned_profile_is_not_published() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var scannerProfileId = createHandler.handle(new CreateProfileCommand(
				"scanner-draft",
				"Scanner Draft",
				"Developer",
				"I scan.")).getSuccess();

		var scannedProfileId = createHandler.handle(new CreateProfileCommand(
				"scanned-draft",
				"Scanned Draft",
				"Designer",
				"I stay draft.")).getSuccess();

		var badgeToken = badgeRepository.findByProfileId(scannedProfileId).orElseThrow().badgeToken();

		var handler = new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				new FakeConnectionRepository(),
				new ConnectionFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var result = handler.handle(new CreateConnectionCommand(scannerProfileId.value(), badgeToken));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotPublishedError);
	}

	@Test
	void should_not_create_duplicate_connection_when_same_pair_is_scanned_in_reverse() {
		var profileRepository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var connectionRepository = new FakeConnectionRepository();

		var createHandler = new CreateProfileCommandHandler(
				profileRepository,
				badgeRepository,
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var firstProfileId = createHandler.handle(new CreateProfileCommand(
				"first-profile",
				"First Profile",
				"Backend developer",
				"I scan first.")).getSuccess();

		var secondProfileId = createHandler.handle(new CreateProfileCommand(
				"second-profile",
				"Second Profile",
				"Product engineer",
				"I scan second.")).getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				profileRepository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));
		publishHandler.handle(new PublishProfileCommand(firstProfileId.value()));
		publishHandler.handle(new PublishProfileCommand(secondProfileId.value()));

		var firstBadgeToken = badgeRepository.findByProfileId(firstProfileId).orElseThrow().badgeToken();
		var secondBadgeToken = badgeRepository.findByProfileId(secondProfileId).orElseThrow().badgeToken();

		var handler = new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				connectionRepository,
				new ConnectionFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T15:00:00Z"))));

		var firstResult = handler.handle(new CreateConnectionCommand(firstProfileId.value(), secondBadgeToken));
		var secondResult = handler.handle(new CreateConnectionCommand(secondProfileId.value(), firstBadgeToken));

		assertTrue(firstResult.isSuccess());
		assertTrue(secondResult.isSuccess());
		assertEquals(firstResult.getSuccess(), secondResult.getSuccess());

		var firstProfileConnections = connectionRepository.findByProfileId(firstProfileId);
		var secondProfileConnections = connectionRepository.findByProfileId(secondProfileId);

		assertEquals(1, firstProfileConnections.size());
		assertEquals(1, secondProfileConnections.size());
		assertEquals(firstProfileConnections.getFirst().id(), secondProfileConnections.getFirst().id());
	}
}
