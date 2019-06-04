package nl.andrewlalis;

import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.GuildMessageChannel;
import discord4j.core.object.entity.MessageChannel;
import discord4j.core.object.util.Snowflake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

/**
 * The starting point for the application.
 */
public class Handiebot2 {

    private static final Logger logger = LoggerFactory.getLogger(Handiebot2.class);

    public static void main(String[] args) {
        logger.info("Starting Handiebot2.");

        final DiscordClient client = new DiscordClientBuilder(args[0]).build();

        logger.info("Adding event handlers...");
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .subscribe(messageCreateEvent -> {
                    logger.info("A message was created somewhere.");

                    Set<Snowflake> userMentionIds = messageCreateEvent.getMessage().getUserMentionIds();
                    boolean amIMentioned = false;
                    for (Snowflake snowflake : userMentionIds) {
                        Optional<Snowflake> myId = client.getSelfId();
                        if (myId.isPresent() && snowflake.equals(myId.get())) {
                            amIMentioned = true;
                        }
                    }

                    if (amIMentioned) {
                        logger.info("I was mentioned!");
                        MessageChannel channel = messageCreateEvent.getMessage().getChannel().block();
                        try {
                            channel.createMessage("Hello there!").block();
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    } else {
                        logger.info("I was not mentioned...");
                    }
                });

        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(readyEvent -> {
                    logger.info("Ready!");
                    GuildMessageChannel startingChannel = (GuildMessageChannel) readyEvent.getSelf().getClient().getGuildById(Snowflake.of(283654164653801472L)).block()
                            .getChannelById(Snowflake.of(326092300302155791L)).block();
                    startingChannel.createMessage("Now online.").block();
                });

        logger.info("Logging in");

        client.login().block();

        logger.info("Logged in as " + client.getSelf().block().getUsername());


    }
}
