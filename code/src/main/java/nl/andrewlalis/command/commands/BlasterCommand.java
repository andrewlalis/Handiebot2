package nl.andrewlalis.command.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.command.Command;

@Slf4j
public class BlasterCommand implements Command {
	@Override
	public void call(MessageCreateEvent event, String[] args) {
		MessageChannel channel = event.getMessage().getChannel().block();
		if (args.length > 0) {
			final String word = args[0];
			int count = 0;
			if (args.length > 1) {
				try {
					count = Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					// Do nothing.
				}
			}
			log.info("Blasting {} for {} times.", word, count);
			for (int i = 0; i < count; i++) {
				channel.createMessage(word).block();
			}
		}
	}
}
