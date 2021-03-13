/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package spygame;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter
{
    Game currentGame;

    public static void main(String[] args) throws LoginException {
        // args[0] should be the token
        // We only need 2 intents in this bot. We only respond to messages in guilds and private channels.
        // All other events will be disabled.
        JDABuilder.createLight("ODIwMzAyOTE2OTM4MTA0ODMy.YEzMnw.NWgsENMO7Gy85tl_bC1k4Ss_PAo", GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES)
                .addEventListeners(new Bot())
                .setActivity(Activity.playing("Type !ping"))
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        if (msg.getContentRaw().equals("!join"))
        {
            if (this.currentGame == null) {
                this.currentGame = new Game(event.getGuild());
            }
            User user = msg.getAuthor();
            MessageChannel channel = event.getChannel();
            channel.sendMessage(user.getAsTag() + " has joined the game!")
                    .queue();
            currentGame.addPlayer(user);
        }
    }
}
