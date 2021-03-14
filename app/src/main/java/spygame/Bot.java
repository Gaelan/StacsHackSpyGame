/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package spygame;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class Bot extends ListenerAdapter
{
    Game currentGame;

    public static void main(String[] args) throws LoginException {
        JDABuilder.createDefault("ODIwMzAyOTE2OTM4MTA0ODMy.YEzMnw.NWgsENMO7Gy85tl_bC1k4Ss_PAo")
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .enableIntents(GatewayIntent.GUILD_PRESENCES)
                .enableIntents(GatewayIntent.GUILD_VOICE_STATES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new Bot())
//                .setActivity(Activity.playing("Type !ping"))
                .build();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        Message msg = event.getMessage();
        MessageChannel channel = event.getChannel();
        if (msg.getContentRaw().equals("!join")) {
            if (this.currentGame == null || this.currentGame.finished) {
                this.currentGame = new Game(event.getGuild());
            }
            if (currentGame.started) {
                channel.sendMessage("Sorry, this game has already started. You'll be able to join the next one, or you can use !reset to start a new game now.").queue();
                return;
            }
            User user = msg.getAuthor();
            channel.sendMessage(user.getAsTag() + " has joined the game!")
                    .queue();
            currentGame.addPlayer(user);
        } else if (msg.getContentRaw().equals("!reset")) {
            this.currentGame.movePlayersToPregame();
            this.currentGame = new Game(event.getGuild());
        } else if (msg.getContentRaw().equals("!start")) {
            if (currentGame.started) {
                channel.sendMessage("The game has already started. If you'd like, you can use !reset to start a new game, or wait for this one to finish.").queue();
                return;
            } else if (currentGame.readyToStart()) {
                this.currentGame.start();
            } else {
                channel.sendMessage("Please make sure everyone is in the Pregame voice channel before starting.").queue();
            }
        } else if (this.currentGame != null) {
            this.currentGame.handlePlayerCommand(event);
        }
    }
}
