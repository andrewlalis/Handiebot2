package nl.andrewlalis.command.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.command.Command;
import nl.andrewlalis.command.CommandManager;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Slf4j
public class FormulaCommand implements Command {

	private final Map<String, String> formulaLatexStrings;

	public FormulaCommand() throws IOException {
		this.formulaLatexStrings = new HashMap<>();
		this.initFormulas();
	}

	@Override
	public Mono<Void> call(MessageCreateEvent event, String[] args) {
		if (args.length != 1) {
			return Mono.just(event.getMessage())
					.flatMap(Message::getChannel)
					.flatMap(channel -> channel.createMessage("No formula given! Provide the name of a formula as a single argument to the command."))
					.then();
		}
		if (args[0].equalsIgnoreCase("list")) {
			return Mono.just(event.getMessage())
					.flatMap(Message::getChannel)
					.flatMap(channel -> channel.createMessage("Available formulas: " + this.getFormulasListString()))
					.then();
		}
		String formulaName = args[0];
		String formulaLatex = this.formulaLatexStrings.get(formulaName);
		if (formulaLatex == null) {
			return Mono.just(event.getMessage())
					.flatMap(Message::getChannel)
					.flatMap(channel -> channel.createMessage("Unknown formula name."))
					.then();
		}
		return CommandManager.getInstance().executeCommand(event, "math", formulaLatex);
	}

	private void initFormulas() throws IOException {
		Properties properties = new Properties();
		properties.load(this.getClass().getClassLoader().getResourceAsStream("formulas.properties"));
		for (String formulaName : properties.stringPropertyNames()) {
			this.formulaLatexStrings.put(formulaName, properties.getProperty(formulaName));
		}
	}

	private String getFormulasListString() {
		return this.formulaLatexStrings.keySet().stream()
				.sorted()
				.map(key -> "**" + key + "**")
				.collect(Collectors.joining(", "));
	}
}
