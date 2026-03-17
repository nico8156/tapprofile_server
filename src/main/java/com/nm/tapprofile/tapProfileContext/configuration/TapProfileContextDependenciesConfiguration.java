package com.nm.tapprofile.tapProfileContext.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.domain.services.ProfileFactory;
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
}
