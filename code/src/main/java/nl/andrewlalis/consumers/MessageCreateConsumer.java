package nl.andrewlalis.consumers;

import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.command.CommandManager;

import java.util.function.Consumer;

/**
 * Main entry point for handling input messages.
 */
@Slf4j
public class MessageCreateConsumer implements Consumer<MessageCreateEvent> {
	@Override
	public void accept(MessageCreateEvent event) {
		CommandManager.getInstance().handleMessage(event)
				.subscribe();
	}
}
