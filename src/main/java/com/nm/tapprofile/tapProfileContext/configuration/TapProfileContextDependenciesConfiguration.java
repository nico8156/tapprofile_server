package com.nm.tapprofile.tapProfileContext.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryLeadRepository;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryProfileRepository;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CaptureLeadCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.RegisterProfileViewCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.ports.LeadRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetDashboardQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetPublicProfileQueryHandler;
import com.nm.tapprofile.tapProfileContext.domain.services.LeadFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileViewFactory;
import com.nm.tapprofile.tapProfileContext.shared.time.DateTimeProvider;
import com.nm.tapprofile.tapProfileContext.shared.time.SystemDateTimeProvider;

@Configuration
public class TapProfileContextDependenciesConfiguration {

	@Bean
	DateTimeProvider dateTimeProvider() {
		return new SystemDateTimeProvider();
	}

	@Bean
	ProfileRepository profileRepository() {
		return new InMemoryProfileRepository();
	}

	@Bean
	ProfileFactory profileFactory(DateTimeProvider dateTimeProvider) {
		return new ProfileFactory(dateTimeProvider);
	}

	@Bean
	CreateProfileCommandHandler createProfileCommandHandler(
			ProfileRepository profileRepository,
			ProfileFactory profileFactory) {
		return new CreateProfileCommandHandler(profileRepository, profileFactory);
	}

	@Bean
	PublishProfileCommandHandler publishProfileCommandHandler(
			ProfileRepository profileRepository,
			DateTimeProvider dateTimeProvider) {
		return new PublishProfileCommandHandler(profileRepository, dateTimeProvider);
	}

	@Bean
	LeadRepository leadRepository() {
		return new InMemoryLeadRepository();
	}

	@Bean
	LeadFactory leadFactory(DateTimeProvider dateTimeProvider) {
		return new LeadFactory(dateTimeProvider);
	}

	@Bean
	CaptureLeadCommandHandler captureLeadCommandHandler(
			ProfileRepository profileRepository,
			LeadRepository leadRepository,
			LeadFactory leadFactory) {
		return new CaptureLeadCommandHandler(profileRepository, leadRepository, leadFactory);
	}

	@Bean
	ProfileViewRepository profileViewRepository() {
		return new InMemoryProfileViewRepository();
	}

	@Bean
	ProfileViewFactory profileViewFactory(DateTimeProvider dateTimeProvider) {
		return new ProfileViewFactory(dateTimeProvider);
	}

	@Bean
	RegisterProfileViewCommandHandler registerProfileViewCommandHandler(
			ProfileRepository profileRepository,
			ProfileViewRepository profileViewRepository,
			ProfileViewFactory profileViewFactory) {
		return new RegisterProfileViewCommandHandler(
				profileRepository,
				profileViewRepository,
				profileViewFactory);
	}

	@Bean
	GetDashboardQueryHandler getDashboardQueryHandler(
			ProfileRepository profileRepository,
			LeadRepository leadRepository,
			ProfileViewRepository profileViewRepository) {
		return new GetDashboardQueryHandler(profileRepository, leadRepository, profileViewRepository);
	}

	@Bean
	GetPublicProfileQueryHandler getPublicProfileQueryHandler(
			ProfileRepository profileRepository) {
		return new GetPublicProfileQueryHandler(profileRepository);
	}

}
