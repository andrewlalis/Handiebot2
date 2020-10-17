package nl.andrewlalis.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import reactor.core.publisher.Mono;

public interface Command {
	/**
	 * Execute the command in a certain context defined by the event and args.
	 * @param event The event which triggered this call.
	 * @param args The arguments to pass to this call.
	 */
	Mono<Void> call(MessageCreateEvent event, String[] args);
}
