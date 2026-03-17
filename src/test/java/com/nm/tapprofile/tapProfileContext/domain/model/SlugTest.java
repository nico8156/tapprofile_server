package com.nm.tapprofile.tapProfileContext.domain.model;

import com.nm.tapprofile.tapProfileContext.domain.errors.InvalidSlugError;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SlugTest {

	@Test
	void should_create_valid_slug() {
		var result = Slug.create("alex-martin");

		assertTrue(result.isValid());
		assertEquals("alex-martin", result.get().value());
	}

	@Test
	void should_fail_when_slug_is_blank() {
		var result = Slug.create("   ");

		assertTrue(result.isInvalid());
		assertEquals(1, result.getErrors().size());
	}

	@Test
	void should_fail_when_slug_contains_invalid_characters() {
		var result = Slug.create("Alex Martin");

		assertTrue(result.isInvalid());
		assertTrue(result.getErrors().stream().anyMatch(error -> error instanceof InvalidSlugError));
	}
}
