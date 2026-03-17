package com.nm.tapprofile.tapProfileContext.shared.result;

import java.util.Objects;
import java.util.function.Function;

public sealed interface Result<E, A> permits Result.Success, Result.Failure {

    boolean isSuccess();

    boolean isFailure();

    A getSuccess();

    E getFailure();

    <B> Result<E, B> map(Function<A, B> mapper);

    <B> Result<E, B> flatMap(Function<A, Result<E, B>> mapper);

    static <E, A> Result<E, A> success(A value) {
        return new Success<>(value);
    }

    static <E, A> Result<E, A> failure(E error) {
        return new Failure<>(error);
    }

    record Success<E, A>(A value) implements Result<E, A> {
        public Success {
            Objects.requireNonNull(value);
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public A getSuccess() {
            return value;
        }

        @Override
        public E getFailure() {
            throw new IllegalStateException("No failure present");
        }

        @Override
        public <B> Result<E, B> map(Function<A, B> mapper) {
            return Result.success(mapper.apply(value));
        }

        @Override
        public <B> Result<E, B> flatMap(Function<A, Result<E, B>> mapper) {
            return mapper.apply(value);
        }
    }

    record Failure<E, A>(E error) implements Result<E, A> {
        public Failure {
            Objects.requireNonNull(error);
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public A getSuccess() {
            throw new IllegalStateException("No success present");
        }

        @Override
        public E getFailure() {
            return error;
        }

        @Override
        public <B> Result<E, B> map(Function<A, B> mapper) {
            return Result.failure(error);
        }

        @Override
        public <B> Result<E, B> flatMap(Function<A, Result<E, B>> mapper) {
            return Result.failure(error);
        }
    }
}
