package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.DomainError;
import com.nm.tapprofile.tapProfileContext.domain.errors.ProfileAlreadyPublishedError;
import com.nm.tapprofile.tapProfileContext.shared.result.Result;

import java.time.Instant;
import java.util.Objects;

public final class Profile {

	private final ProfileId id;
	private final Slug slug;
	private final DisplayName displayName;
	private final Headline headline;
	private final Bio bio;
	private final ProfileStatus status;
	private final Instant createdAt;
	private final Instant publishedAt;

	public Profile(
			ProfileId id,
			Slug slug,
			DisplayName displayName,
			Headline headline,
			Bio bio,
			ProfileStatus status,
			Instant createdAt,
			Instant publishedAt) {
		this.id = Objects.requireNonNull(id);
		this.slug = Objects.requireNonNull(slug);
		this.displayName = Objects.requireNonNull(displayName);
		this.headline = Objects.requireNonNull(headline);
		this.bio = Objects.requireNonNull(bio);
		this.status = Objects.requireNonNull(status);
		this.createdAt = Objects.requireNonNull(createdAt);
		this.publishedAt = publishedAt;
	}

	public ProfileId id() {
		return id;
	}

	public Slug slug() {
		return slug;
	}

	public DisplayName displayName() {
		return displayName;
	}

	public Headline headline() {
		return headline;
	}

	public Bio bio() {
		return bio;
	}

	public ProfileStatus status() {
		return status;
	}

	public Instant createdAt() {
		return createdAt;
	}

	public Instant publishedAt() {
		return publishedAt;
	}

	public Result<DomainError, Profile> publish(Instant publicationDate) {
		if (status == ProfileStatus.PUBLISHED) {
			return Result.failure(new ProfileAlreadyPublishedError(id.value()));
		}

		return Result.success(new Profile(
				id,
				slug,
				displayName,
				headline,
				bio,
				ProfileStatus.PUBLISHED,
				createdAt,
				publicationDate));
	}
}
