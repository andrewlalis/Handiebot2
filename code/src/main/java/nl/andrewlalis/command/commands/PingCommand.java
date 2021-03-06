package nl.andrewlalis.command.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import nl.andrewlalis.command.Command;
import reactor.core.publisher.Mono;

public class PingCommand implements Command {
	@Override
	public Mono<Void> call(MessageCreateEvent event, String[] args) {
		return Mono.just(event.getMessage())
				.flatMap(Message::getChannel)
				.flatMap(channel -> channel.createMessage("Pong!"))
				.then();
	}
}
