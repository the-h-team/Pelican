package com.github.sanctum.messenger.api;

import com.github.sanctum.messenger.api.entity.Sender;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Message {

	@NotNull("Sender can not be null!") Sender getSender();

	@Nullable String getSubject();

	@NotNull String getText();

	@NotNull Object getRaw();

	@NotNull Date getDateSent();

	boolean isText();

	boolean isItem();

	boolean setValid(boolean valid);

	boolean isValid();

}
