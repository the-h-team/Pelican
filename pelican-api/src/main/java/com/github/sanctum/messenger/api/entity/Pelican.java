package com.github.sanctum.messenger.api.entity;

import com.github.sanctum.labyrinth.api.TaskService;
import com.github.sanctum.labyrinth.event.LabyrinthVentCall;
import com.github.sanctum.messenger.api.DeliverySequence;
import com.github.sanctum.messenger.api.FunctionalDeployable;
import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.messenger.api.event.IncomingMessageEvent;
import com.github.sanctum.messenger.util.SimpleMessage;
import com.github.sanctum.panther.annotation.Experimental;
import com.github.sanctum.panther.container.ImmutablePantherCollection;
import com.github.sanctum.panther.container.PantherCollection;
import com.github.sanctum.panther.container.PantherSet;
import com.github.sanctum.panther.util.Deployable;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Pelican implements Recipient, Sender {

	final PantherCollection<Message> inbox = new PantherSet<>();

	Pelican recent;
	boolean manualRead = true;

	@Override
	public abstract @NotNull String getName();

	@Override
	public abstract @NotNull UUID getId();

	public abstract void open(@NotNull Object target);

	public abstract Type getType();

	public PantherCollection<Message> inbox() {
		return ImmutablePantherCollection.of(inbox);
	}

	public boolean isManualRead() {
		return manualRead;
	}

	public void setManualRead(boolean manualRead) {
		this.manualRead = manualRead;
	}

	@Experimental
	public void input(@NotNull Message message) {
		inbox.add(message);
	}

	public @Nullable Pelican getRecent() {
		return recent;
	}

	public Deployable<Message> destroy(@NotNull Message message) {
		return Deployable.of(message, m -> {
			inbox.remove(m);
			m.setValid(false);
		}, TaskService.ASYNCHRONOUS);
	}

	@Override
	public @NotNull Deployable<DeliverySequence> write(@NotNull Object o, Recipient... recipients) {
		DeliverySequence.Builder builder = DeliverySequence.builder();
		return new FunctionalDeployable<>(builder, b -> {
			for (Recipient r : recipients) {
				b.add(r);
			}
			b.set(new SimpleMessage(o, getId().toString(), null));
			return b.build();
		});
	}

	@Override
	public @NotNull Deployable<DeliverySequence> write(@NotNull Object o, @NotNull String subject, Recipient... recipients) {
		DeliverySequence.Builder builder = DeliverySequence.builder();
		return new FunctionalDeployable<>(builder, b -> {
			for (Recipient r : recipients) {
				b.add(r);
			}
			b.set(new SimpleMessage(o, getId().toString(), subject));
			return b.build();
		});
	}

	@Override
	public Deployable<Void> write(@NotNull DeliverySequence sequence) {
		return Deployable.of(null, unused -> {
			for (Recipient r : sequence.getResponders()) {
				r.read(sequence).queue();
			}
		}, TaskService.ASYNCHRONOUS);
	}

	@Override
	// Mail received event
	public @NotNull Deployable<Message> read(@NotNull DeliverySequence sequence) {
		return Deployable.of(sequence.getContent(), message -> {
			if (message.isValid()) {
				recent = (Pelican) message.getSender();
				new LabyrinthVentCall<>(new IncomingMessageEvent(message, this)).schedule().join();
			}
		}, TaskService.ASYNCHRONOUS);
	}

	@Override
	public @NotNull Deployable<Message> read(@NotNull Message m) {
		return Deployable.of(m, message -> {
			if (message.isValid()) {
				recent = (Pelican) message.getSender();
				new LabyrinthVentCall<>(new IncomingMessageEvent(message, this)).schedule().join();
			}
		}, TaskService.ASYNCHRONOUS);
	}

	public enum Type {

		BUKKIT, UNKNOWN;

	}

}
