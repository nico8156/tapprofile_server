package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commands.CreateProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.commands.PublishProfileCommand;
import com.nm.tapprofile.tapProfileContext.application.queries.GetPublicProfileQuery;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeBadgeRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.repositories.FakeProfileRepository;
import com.nm.tapprofile.tapProfileContext.testdoubles.time.FixedDateTimeProvider;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class GetPublicProfileQueryHandlerTest {

	@Test
	void should_return_published_profile_by_slug() {
		var repository = new FakeProfileRepository();

		var createHandler = new CreateProfileCommandHandler(
				repository,
				new FakeBadgeRepository(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		var createResult = createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"Backend developer",
				"I build useful products."));

		var profileId = createResult.getSuccess();

		var publishHandler = new PublishProfileCommandHandler(
				repository,
				new FixedDateTimeProvider(Instant.parse("2026-03-17T11:00:00Z")));

		publishHandler.handle(new PublishProfileCommand(profileId.value()));

		var handler = new GetPublicProfileQueryHandler(repository);

		var result = handler.handle(new GetPublicProfileQuery("alex-martin"));

		assertTrue(result.isSuccess());

		var publicProfile = result.getSuccess();
		assertEquals("alex-martin", publicProfile.slug());
		assertEquals("Alex Martin", publicProfile.displayName());
		assertEquals("VISITOR", publicProfile.role());
		assertEquals("Backend developer", publicProfile.headline());
		assertEquals("I build useful products.", publicProfile.bio());
		assertEquals(Instant.parse("2026-03-17T11:00:00Z"), publicProfile.publishedAt());
	}

	@Test
	void should_fail_when_profile_does_not_exist() {
		var repository = new FakeProfileRepository();
		var handler = new GetPublicProfileQueryHandler(repository);

		var result = handler.handle(new GetPublicProfileQuery("unknown-profile"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotFoundError);
	}

	@Test
	void should_fail_when_profile_is_not_published() {
		var repository = new FakeProfileRepository();

		var createHandler = new CreateProfileCommandHandler(
				repository,
				new FakeBadgeRepository(),
				new ProfileFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))),
				new BadgeFactory(new FixedDateTimeProvider(Instant.parse("2026-03-17T10:00:00Z"))));

		createHandler.handle(new CreateProfileCommand(
				"alex-martin",
				"Alex Martin",
				"Backend developer",
				"I build useful products."));

		var handler = new GetPublicProfileQueryHandler(repository);

		var result = handler.handle(new GetPublicProfileQuery("alex-martin"));

		assertTrue(result.isFailure());
		assertTrue(result.getFailure() instanceof ProfileNotPublishedError);
	}
}
