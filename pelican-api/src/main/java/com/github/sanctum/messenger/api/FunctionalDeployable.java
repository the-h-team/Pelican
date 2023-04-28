package com.github.sanctum.messenger.api;

import com.github.sanctum.labyrinth.task.TaskScheduler;
import com.github.sanctum.panther.util.Deployable;
import com.github.sanctum.panther.util.DeployableMapping;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public final class FunctionalDeployable<I, Q> implements Deployable<Q> {

	private final I i;
	private boolean applied;
	private final Function<I, Q> mappingFunction;
	private Q q;

	public FunctionalDeployable(I inadequate, Function<I, Q> mapper) {
		this.i = inadequate;
		this.mappingFunction = mapper;
	}

	@Override
	public FunctionalDeployable<I, Q> deploy() {
		get();
		return this;
	}

	@Override
	public FunctionalDeployable<I, Q> deploy(Consumer<? super Q> consumer) {
		consumer.accept(deploy().get());
		return this;
	}

	@Override
	public FunctionalDeployable<I, Q> queue() {
		TaskScheduler.of(this::deploy).schedule();
		return this;
	}

	@Override
	public FunctionalDeployable<I, Q> queue(long wait) {
		TaskScheduler.of(this::deploy).scheduleLater(wait);
		return this;
	}

	@Override
	public FunctionalDeployable<I, Q> queue(Consumer<? super Q> consumer, long wait) {
		TaskScheduler.of(() -> consumer.accept(this.deploy().get())).scheduleLater(wait);
		return this;
	}

	@Override
	public <O> DeployableMapping<O> map(Function<? super Q, ? extends O> mapper) {
		return null;
	}

	public <O> FunctionalDeployable<Q, O> mapAlt(Function<Q, O> mapper) {
		return new FunctionalDeployable<>(deploy().get(), mapper);
	}

	@Override
	public CompletableFuture<Q> submit() {
		return CompletableFuture.supplyAsync(this::get);
	}

	@Override
	public Q complete() {
		return submit().join();
	}

	@Override
	public Q get() {
		if (applied) {
			return q;
		} else {
			Q r = mappingFunction.apply(i);
			this.q = r;
			applied = true;
			return r;
		}
	}
}
