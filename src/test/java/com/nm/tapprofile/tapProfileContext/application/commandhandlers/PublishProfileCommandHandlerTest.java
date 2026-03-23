package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileAlreadyPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.shared.result.Unit;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PublishProfileCommandHandlerTest {

	@Test
	void should_publish_existing_draft_profile() {
		var repository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var createFactory = new ProfileFactory(
				new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")));
		var badgeFactory = new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")));
		var createHandler = new CreateProfileCommandHandler(repository, badgeRepository, createFactory, badgeFactory);

		var createResult = createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"Backend developer",
				"I build useful products."));

		var profileId = createResult.getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				repository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));

		var result = publishHandler.handle(new PublishProfileCommand(profileId.value()));

		assertTrue(result.isSuccess());
		assertEquals(Unit.INSTANCE, result.getSuccess());

		var savedProfile = repository.findById(profileId);
		assertTrue(savedProfile.isPresent());
		assertEquals(com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus.PUBLISHED,
				savedProfile.get().status());
		assertEquals(Instant.parse("2026-03-17T11:00:00Z"), savedProfile.get().publishedAt());
	}

	@Test
	void should_fail_when_profile_does_not_exist() {
		var repository = new FakeProfileRepository();
		var handler = new PublishProfileCommandHandler(
				repository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));

		var result = handler.handle(new PublishProfileCommand(UUID.randomUUID()));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotFoundError);
	}

	@Test
	void should_fail_when_profile_is_already_published() {
		var repository = new FakeProfileRepository();
		var badgeRepository = new FakeBadgeRepository();
		var createFactory = new ProfileFactory(
				new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")));
		var badgeFactory = new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z")));
		var createHandler = new CreateProfileCommandHandler(repository, badgeRepository, createFactory, badgeFactory);

		var createResult = createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"Backend developer",
				"I build useful products."));

		var profileId = createResult.getSuccess();

		var firstPublishHandler = new PublishProfileCommandHandler(
				repository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));
		firstPublishHandler.handle(new PublishProfileCommand(profileId.value()));

		var secondPublishHandler = new PublishProfileCommandHandler(
				repository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T12:00:00Z")));

		var result = secondPublishHandler.handle(new PublishProfileCommand(profileId.value()));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileAlreadyPublishedError);
	}
}
