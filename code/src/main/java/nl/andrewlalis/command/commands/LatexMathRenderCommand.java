package nl.andrewlalis.command.commands;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.command.Command;
import org.scilab.forge.jlatexmath.DefaultTeXFont;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;
import org.scilab.forge.jlatexmath.greek.GreekRegistration;
import reactor.core.publisher.Mono;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;

@Slf4j
public class LatexMathRenderCommand implements Command {
	@Override
	public Mono<Void> call(MessageCreateEvent event, String[] args) {
		if (args.length < 1) {
			return Mono.just(event.getMessage())
						.flatMap(Message::getChannel)
						.flatMap(channel -> channel.createMessage("Invalid command. Expected at least one argument."))
						.flatMap(message -> this.deleteMessageAfter(message, Duration.ofSeconds(3)));
		}
		final String mathString = "$$ " + String.join(" ", args) + " $$";
		return Mono.just(event.getMessage())
				.flatMap(Message::getChannel)
				.flatMap(channel -> channel.createMessage(messageCreateSpec -> {
					try {
						messageCreateSpec.addFile("math.png", this.renderLatex(mathString));
					} catch (IOException e) {
						log.warn("Error creating LaTeX math image: {}", e.getMessage());
						messageCreateSpec.setContent("Error creating image.");
					}
				}))
				.then();
	}

	private Mono<Void> deleteMessageAfter(Message message, Duration duration) {
		return Mono.just(message)
				.delayElement(duration)
				.flatMap(Message::delete)
				.doOnError(throwable -> log.warn("Error deleting message: {}", throwable.getMessage()));
	}

	private InputStream renderLatex(String latex) throws IOException {
		DefaultTeXFont.registerAlphabet(new GreekRegistration());
		TeXFormula formula = new TeXFormula(latex);
		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 20);
		icon.setInsets(new Insets(5, 5, 5, 5));
		JLabel label = new JLabel();
		label.setForeground(Color.BLACK);

		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		icon.paintIcon(label, g, 0, 0);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ImageIO.write(image, "png", bos);
		return new ByteArrayInputStream(bos.toByteArray());
	}
}
