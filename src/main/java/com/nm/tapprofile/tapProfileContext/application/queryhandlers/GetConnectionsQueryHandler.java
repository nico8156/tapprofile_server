package com.nm.tapprofile.tapProfileContext.application.queryhandlers;

import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.queries.GetConnectionsQuery;
import com.nm.tapprofile.tapProfileContext.application.queries.GetConnectionsResult;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileId;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

import java.util.Comparator;

public final class GetConnectionsQueryHandler {

	private final ProfileRepository profileRepository;
	private final ConnectionRepository connectionRepository;

	public GetConnectionsQueryHandler(
			ProfileRepository profileRepository,
			ConnectionRepository connectionRepository) {
		this.profileRepository = profileRepository;
		this.connectionRepository = connectionRepository;
	}

	public Result<DomainError, GetConnectionsResult> handle(GetConnectionsQuery query) {
		var profileId = new ProfileId(query.profileId());

		var maybeProfile = profileRepository.findById(profileId);

		if (maybeProfile.isEmpty()) {
			return Result.failure(new ProfileNotFoundError(query.profileId().toString()));
		}

		var items = connectionRepository.findByProfileId(profileId).stream()
				.sorted(Comparator.comparing(connection -> connection.createdAt(), Comparator.reverseOrder()))
				.map(connection -> {
					var connectedProfileId = connection.scannerProfileId().equals(profileId)
							? connection.scannedProfileId()
							: connection.scannerProfileId();

					return profileRepository.findById(connectedProfileId)
							.map(connectedProfile -> new GetConnectionsResult.Item(
									connectedProfile.id().value(),
									connectedProfile.displayName().value(),
									connectedProfile.headline().value(),
									connectedProfile.role().name(),
									connection.createdAt()))
							.orElse(null);
				})
				.filter(item -> item != null)
				.toList();

		return Result.success(new GetConnectionsResult(items));
	}
}
