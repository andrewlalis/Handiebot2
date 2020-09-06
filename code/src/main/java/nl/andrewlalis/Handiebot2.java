package nl.andrewlalis;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import lombok.extern.slf4j.Slf4j;
import nl.andrewlalis.consumers.MessageCreateConsumer;

/**
 * The starting point for the application.
 */
@Slf4j
public class Handiebot2 {

    public static void main(String[] args) {
        final DiscordClient client = DiscordClient.create(getTokenFromArgs(args));
        GatewayDiscordClient gateway = client.login().blockOptional()
                .orElseThrow(() -> new RuntimeException("Could not obtain gateway discord client."));

        log.info("Waiting for ready event.");
        gateway.on(ReadyEvent.class).subscribe(event -> {
            log.info("Logged in as {}({}).", event.getSelf().getUsername(), event.getSelf().getDiscriminator());
            event.getSelf().getClient().getGuilds().collectList().subscribe(guilds -> {
                guilds.forEach(guild -> {
                    log.info("Active in guild {}({}).", guild.getName(), guild.getId().asString());
                });
            });
        });

        log.info("Initializing message consumer.");
        gateway.on(MessageCreateEvent.class).subscribe(new MessageCreateConsumer());

        gateway.onDisconnect().block();
    }

    /**
     * Parses a Discord client token from the command line arguments. Will force
     * exit the program if a token cannot be obtained.
     * @param args The command line args.
     * @return The token to use.
     */
    private static String getTokenFromArgs(String[] args) {
        if (args.length < 1) {
            log.error("Missing token command line argument.");
            System.exit(-1);
        }
        final String token = args[0];
        if (token.isBlank()) {
            log.error("Invalid token argument given.");
            System.exit(-1);
        }
        return token;
    }
}
