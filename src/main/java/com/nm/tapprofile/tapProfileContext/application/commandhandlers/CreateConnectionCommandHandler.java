package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CreateConnectionCommand;
import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.BadgeRevokedError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ConnectionAlreadySelfError;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.model.BadgeStatus;
import com.nm.tapprofile.tapProfileContext.domain.model.Connection;
import com.nm.tapprofile.tapProfileContext.domain.model.ConnectionId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus;
import com.nm.tapprofile.tapProfileContext.domain.services.ConnectionFactory;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

public final class CreateConnectionCommandHandler {

	private final ProfileRepository profileRepository;
	private final BadgeRepository badgeRepository;
	private final ConnectionRepository connectionRepository;
	private final ConnectionFactory connectionFactory;

	public CreateConnectionCommandHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository,
			ConnectionRepository connectionRepository,
			ConnectionFactory connectionFactory) {
		this.profileRepository = profileRepository;
		this.badgeRepository = badgeRepository;
		this.connectionRepository = connectionRepository;
		this.connectionFactory = connectionFactory;
	}

	public Result<DomainError, ConnectionId> handle(CreateConnectionCommand command) {
		var scannerProfileId = new ProfileId(command.scannerProfileId());

		var maybeScannerProfile = profileRepository.findById(scannerProfileId);

		if (maybeScannerProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(command.scannerProfileId().toString()));
		}

		var maybeBadge = badgeRepository.findByBadgeToken(command.badgeToken());

		if (maybeBadge.isEmpty()) {
			return Result.failure(new BadgeNotFoundError(command.badgeToken()));
		}

		var badge = maybeBadge.get();

		if (badge.status() == BadgeStatus.REVOKED) {
			return Result.failure(new BadgeRevokedError(command.badgeToken()));
		}

		if (badge.profileId().equals(scannerProfileId)) {
			return Result.failure(new ConnectionAlreadySelfError(command.scannerProfileId()));
		}

		var maybeScannedProfile = profileRepository.findById(badge.profileId());

		if (maybeScannedProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(badge.profileId().value().toString()));
		}

		var scannedProfile = maybeScannedProfile.get();

		if (scannedProfile.status() != ProfileStatus.PUBLISHED) {
			return Result.failure(new ProfileNotPublishedError(scannedProfile.slug().value()));
		}

		var existingConnection = connectionRepository.findByProfileIds(scannerProfileId, badge.profileId());

		if (existingConnection.isPresent()) {
			return Result.success(existingConnection.get().id());
		}

		Connection connection = connectionFactory.create(scannerProfileId, badge.profileId());
		connectionRepository.save(connection);

		return Result.success(connection.id());
	}
}
