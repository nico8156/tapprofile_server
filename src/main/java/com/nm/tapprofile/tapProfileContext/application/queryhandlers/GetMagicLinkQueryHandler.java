package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.MagicLinkRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.queries.GetMagicLinkQuery;
import com.nm.tapprofile.tapProfileContext.application.queries.GetMagicLinkResult;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.MagicLinkExpiredError;
import com.nm.tapprofile.tapProfileContext.domain.errors.MagicLinkNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

public final class GetMagicLinkQueryHandler {

	private static final Logger log = LoggerFactory.getLogger(GetMagicLinkQueryHandler.class);

	private final MagicLinkRepository magicLinkRepository;
	private final ProfileRepository profileRepository;
	private final ConnectionRepository connectionRepository;
	private final DateTimeProvider dateTimeProvider;

	public GetMagicLinkQueryHandler(
			MagicLinkRepository magicLinkRepository,
			ProfileRepository profileRepository,
			ConnectionRepository connectionRepository,
			DateTimeProvider dateTimeProvider) {
		this.magicLinkRepository = magicLinkRepository;
		this.profileRepository = profileRepository;
		this.connectionRepository = connectionRepository;
		this.dateTimeProvider = dateTimeProvider;
	}

	public Result<DomainError, GetMagicLinkResult> handle(GetMagicLinkQuery query) {
		log.info("Magic link accessed: token={}", query.token());

		var maybeMagicLink = magicLinkRepository.findByToken(query.token());

		if (maybeMagicLink.isEmpty()) {
			log.warn("Magic link invalid or expired: token={}", query.token());
			return Result.failure(new MagicLinkNotFoundError(query.token().toString()));
		}

		var magicLink = maybeMagicLink.get();
		var now = dateTimeProvider.now();

		if (magicLink.isExpiredAt(now)) {
			log.warn("Magic link invalid or expired: token={}", query.token());
			return Result.failure(new MagicLinkExpiredError(query.token().toString()));
		}

		var maybeProfile = profileRepository.findById(magicLink.profileId());

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(magicLink.profileId().value().toString()));
		}

		var profile = maybeProfile.get();
		log.info("Magic link valid: profileId={}", profile.id().value());
		var contacts = connectionRepository.findByProfileId(profile.id()).stream()
				.sorted(Comparator.comparing(connection -> connection.createdAt(), Comparator.reverseOrder()))
				.map(connection -> {
					ProfileId connectedProfileId = connection.scannerProfileId().equals(profile.id())
							? connection.scannedProfileId()
							: connection.scannerProfileId();

					return profileRepository.findById(connectedProfileId)
							.map(connectedProfile -> new GetMagicLinkResult.ContactItem(
									connectedProfile.id().value(),
									connectedProfile.displayName().value(),
									connectedProfile.headline().value(),
									connectedProfile.role().name(),
									connection.createdAt()))
							.orElse(null);
				})
				.filter(contact -> contact != null)
				.toList();

		magicLinkRepository.save(magicLink.markUsedAt(now));

		return Result.success(new GetMagicLinkResult(
				new GetMagicLinkResult.ProfileSummary(
						profile.id().value(),
						profile.slug().value(),
						profile.displayName().value(),
						profile.emailAddress().value(),
						profile.role().name(),
						profile.status().name()),
				contacts));
	}
}
