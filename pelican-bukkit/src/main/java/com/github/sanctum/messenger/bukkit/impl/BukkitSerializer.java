package com.github.sanctum.messenger.bukkit.impl;

import com.github.sanctum.messenger.api.Message;
import com.github.sanctum.panther.file.Configurable;
import com.github.sanctum.panther.file.JsonAdapter;
import com.github.sanctum.panther.file.Node;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Date;
import java.util.Map;
import org.bukkit.inventory.ItemStack;

@Node.Pointer(value = "Message")
public class BukkitSerializer implements JsonAdapter<Message> {

	@Override
	public JsonElement write(Message message) {
		JsonObject o = new JsonObject();
		o.addProperty("sender", message.getSender().getId().toString());
		if (message.getSubject() != null) {
			o.addProperty("subject", message.getSubject());
		}
		if (message.isItem()) {
			JsonAdapter<ItemStack> adapter = Configurable.getAdapter(ItemStack.class);
			o.add("item", adapter.write((ItemStack) message.getRaw()));
		}
		if (message.isText()) {
			o.addProperty("text", message.getText());
		}
		o.addProperty("date", message.getDateSent().getTime());
		return o;
	}

	@Override
	public Message read(Map<String, Object> map) {
		Object msg = null;
		if (map.containsKey("text")) {
			msg = map.get("text").toString();
		}
		if (map.containsKey("item")) {
			JsonAdapter<ItemStack> adapter = Configurable.getAdapter(ItemStack.class);
			msg = adapter.read((Map<String, Object>) map.get("item"));
		}
		String sender = map.get("sender").toString();
		Object subject = map.get("subject");
		Date date = new Date(Long.valueOf(map.get("date").toString()));
		return new BukkitMessage(msg, sender, subject != null ? subject.toString() : null, date);
	}

	@Override
	public Class<? extends Message> getSerializationSignature() {
		return Message.class;
	}

}
