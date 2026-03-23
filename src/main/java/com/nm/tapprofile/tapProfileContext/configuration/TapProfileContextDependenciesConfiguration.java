package com.nm.tapprofile.tapProfileContext.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryBadgeRepository;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryConnectionRepository;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryLeadRepository;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryProfileRepository;
import com.nm.tapprofile.tapProfileContext.adapters.secondary.gateways.repositories.inmem.InMemoryProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateConnectionCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CaptureLeadCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.CreateProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.PublishProfileCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.commandhandlers.RegisterProfileViewCommandHandler;
import com.nm.tapprofile.tapProfileContext.application.ports.BadgeRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ConnectionRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.LeadRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileRepository;
import com.nm.tapprofile.tapProfileContext.application.ports.ProfileViewRepository;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetDashboardQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetConnectionsQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetProfileBadgeQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetPublicBadgeQueryHandler;
import com.nm.tapprofile.tapProfileContext.application.queryhandlers.GetPublicProfileQueryHandler;
import com.nm.tapprofile.tapProfileContext.domain.services.BadgeFactory;
import com.nm.tapprofile.tapProfileContext.domain.services.ConnectionFactory;
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
	BadgeRepository badgeRepository() {
		return new InMemoryBadgeRepository();
	}

	@Bean
	ConnectionRepository connectionRepository() {
		return new InMemoryConnectionRepository();
	}

	@Bean
	ProfileFactory profileFactory(DateTimeProvider dateTimeProvider) {
		return new ProfileFactory(dateTimeProvider);
	}

	@Bean
	BadgeFactory badgeFactory(DateTimeProvider dateTimeProvider) {
		return new BadgeFactory(dateTimeProvider);
	}

	@Bean
	ConnectionFactory connectionFactory(DateTimeProvider dateTimeProvider) {
		return new ConnectionFactory(dateTimeProvider);
	}

	@Bean
	CreateProfileCommandHandler createProfileCommandHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository,
			ProfileFactory profileFactory,
			BadgeFactory badgeFactory) {
		return new CreateProfileCommandHandler(profileRepository, badgeRepository, profileFactory, badgeFactory);
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
			ConnectionRepository connectionRepository,
			LeadRepository leadRepository,
			ProfileViewRepository profileViewRepository) {
		return new GetDashboardQueryHandler(
				profileRepository,
				connectionRepository,
				leadRepository,
				profileViewRepository);
	}

	@Bean
	GetPublicProfileQueryHandler getPublicProfileQueryHandler(
			ProfileRepository profileRepository) {
		return new GetPublicProfileQueryHandler(profileRepository);
	}

	@Bean
	CreateConnectionCommandHandler createConnectionCommandHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository,
			ConnectionRepository connectionRepository,
			ConnectionFactory connectionFactory) {
		return new CreateConnectionCommandHandler(
				profileRepository,
				badgeRepository,
				connectionRepository,
				connectionFactory);
	}

	@Bean
	GetPublicBadgeQueryHandler getPublicBadgeQueryHandler(
			BadgeRepository badgeRepository,
			ProfileRepository profileRepository) {
		return new GetPublicBadgeQueryHandler(badgeRepository, profileRepository);
	}

	@Bean
	GetConnectionsQueryHandler getConnectionsQueryHandler(
			ProfileRepository profileRepository,
			ConnectionRepository connectionRepository) {
		return new GetConnectionsQueryHandler(profileRepository, connectionRepository);
	}

	@Bean
	GetProfileBadgeQueryHandler getProfileBadgeQueryHandler(
			ProfileRepository profileRepository,
			BadgeRepository badgeRepository) {
		return new GetProfileBadgeQueryHandler(profileRepository, badgeRepository);
	}

}
