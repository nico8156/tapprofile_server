package com.nm.tapprofile.tapProfileContext.application.commandhandlers;

import com.nm.tapprofile.tapProfileContext.application.commands.CaptureLeadCommand;
import com.nm.tapprofile.tapProfileContext.application.ports.LeadRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotFoundError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileNotPublishedError;
import com.nm.tapprofile.tapProfileContext.domain.model.Lead;
import com.nm.tapprofile.tapProfileContext.domain.model.LeadId;
import com.nm.tapprofile.tapProfileContext.domain.model.ProfileStatus;
import com.nm.tapprofile.tapProfileContext.domain.model.Slug;
import com.nm.tapprofile.tapProfileContext.domain.services.LeadFactory;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

import java.util.List;

public final class CaptureLeadCommandHandler {

	private final ProfileRepository profileRepository;
	private final LeadRepository leadRepository;
	private final LeadFactory leadFactory;

	public CaptureLeadCommandHandler(
			ProfileRepository profileRepository,
			LeadRepository leadRepository,
			LeadFactory leadFactory) {
		this.profileRepository = profileRepository;
		this.leadRepository = leadRepository;
		this.leadFactory = leadFactory;
	}

	public Result<List<DomainError>, LeadId> handle(CaptureLeadCommand command) {
		var slugValidation = Slug.create(command.slug());

		if (slugValidation.isInvalid()) {
			return Result.failure(List.of(new ProfileNotFoundError(command.slug())));
		}

		var slug = slugValidation.get();
		var maybeProfile = profileRepository.findBySlug(slug);

		if (maybeProfile.isEmpty()) {
			return Result.failure(List.of(new ProfileNotFoundError(command.slug())));
		}

		var profile = maybeProfile.get();

		if (profile.status() != ProfileStatus.PUBLISHED) {
			return Result.failure(List.of(new ProfileNotPublishedError(profile.slug().value())));
		}

		var leadValidation = leadFactory.create(
				profile.id(),
				command.firstName(),
				command.email(),
				command.message());

		if (leadValidation.isInvalid()) {
			return Result.failure(List.copyOf(leadValidation.getErrors()));
		}

		Lead lead = leadValidation.get();
		leadRepository.save(lead);
		return Result.success(lead.id());
	}
}
