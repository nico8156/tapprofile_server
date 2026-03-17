package com.nm.tapprofile.tapProfileContext.shared.validation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public sealed interface Validation<E, A> permits Validation.Valid, Validation.Invalid {

	boolean isValid();

	boolean isInvalid();

	A get();

	List<E> getErrors();

	<B> Validation<E, B> map(Function<A, B> mapper);

	static <E, A> Validation<E, A> valid(A value) {
		return new Valid<>(value);
	}

	static <E, A> Validation<E, A> invalid(List<E> errors) {
		return new Invalid<>(errors);
	}

	static <E, A, B, C, D, R> Validation<E, R> combine(
			Validation<E, A> va,
			Validation<E, B> vb,
			Validation<E, C> vc,
			Validation<E, D> vd,
			Function4<A, B, C, D, R> combiner) {
		List<E> errors = new ArrayList<>();

		if (va.isInvalid())
			errors.addAll(va.getErrors());
		if (vb.isInvalid())
			errors.addAll(vb.getErrors());
		if (vc.isInvalid())
			errors.addAll(vc.getErrors());
		if (vd.isInvalid())
			errors.addAll(vd.getErrors());

		if (!errors.isEmpty()) {
			return Validation.invalid(errors);
		}

		return Validation.valid(
				combiner.apply(va.get(), vb.get(), vc.get(), vd.get()));
	}

	@FunctionalInterface
	interface Function4<A, B, C, D, R> {
		R apply(A a, B b, C c, D d);
	}

	record Valid<E, A>(A value) implements Validation<E, A> {
		public Valid {
			Objects.requireNonNull(value);
		}

		@Override
		public boolean isValid() {
			return true;
		}

		@Override
		public boolean isInvalid() {
			return false;
		}

		@Override
		public A get() {
			return value;
		}

		@Override
		public List<E> getErrors() {
			throw new IllegalStateException("No errors present");
		}

		@Override
		public <B> Validation<E, B> map(Function<A, B> mapper) {
			return Validation.valid(mapper.apply(value));
		}
	}

	record Invalid<E, A>(List<E> errors) implements Validation<E, A> {
		public Invalid {
			Objects.requireNonNull(errors);
			if (errors.isEmpty()) {
				throw new IllegalArgumentException("Errors cannot be empty");
			}
			errors = List.copyOf(errors);
		}

		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public boolean isInvalid() {
			return true;
		}

		@Override
		public A get() {
			throw new IllegalStateException("No valid value present");
		}

		@Override
		public List<E> getErrors() {
			return errors;
		}

		@Override
		public <B> Validation<E, B> map(Function<A, B> mapper) {
			return Validation.invalid(errors);
		}
	}
}
