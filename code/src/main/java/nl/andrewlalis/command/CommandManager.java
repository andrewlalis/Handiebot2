package nl.andrewlalis.command;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.command.commands.LatexMathRenderCommand;
import nl.andrewlalis.command.commands.PingCommand;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton manager class for keeping a list of all available commands, and
 * doing the necessary preparations to delegate processing of a message to a
 * command's logic.
 */
@Slf4j
public class CommandManager {
	private static CommandManager instance;
	private static final String PREFIX = "!";

	private final Map<String, Command> commands;

	private CommandManager() {
		this.commands = new HashMap<>();
		this.initializeCommands();
	}

	private void initializeCommands() {
		this.commands.put("ping", new PingCommand());
		this.commands.put("math", new LatexMathRenderCommand());
	}

	public Mono<Void> handleMessage(MessageCreateEvent event) {
		final String content = event.getMessage().getContent();
		if (content.startsWith(PREFIX)) {
			final String[] words = content.split(" ");
			final String commandWord = words[0].substring(1).trim().toLowerCase();
			final String[] args = new String[words.length - 1];
			System.arraycopy(words, 1, args, 0, words.length - 1);
			Command command = this.commands.get(commandWord);
			if (command != null) {
				String author = "nobody";
				if (event.getMessage().getAuthor().isPresent()) {
					author = event.getMessage().getAuthor().get().getUsername();
				}
				log.debug("Command {} called by `{}`.", commandWord, author);
				return command.call(event, args)
						.then(
								Mono.just(event.getMessage())
										.flatMap(Message::delete)
										.doOnError(throwable -> log.error("Could not delete user command message: {}", throwable.getMessage()))
						);
			}
		}
		return Mono.empty();
	}

	public static CommandManager getInstance() {
		if (instance == null) {
			instance = new CommandManager();
		}
		return instance;
	}
}
