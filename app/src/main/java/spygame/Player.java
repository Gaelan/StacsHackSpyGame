package spygame;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import javax.annotation.RegEx;
import java.util.ArrayList;

public class Player {
    Game game;
    long discordId;
    long privateChannelId;
    Room currentRoom;

    Player(Game game, User user) {
        this.game = game;
        this.discordId = user.getIdLong();


        String name = user.getAsTag().toLowerCase().replaceAll("[^a-z]", "-");
        game.getPrivateChannelsCategory().createTextChannel(name).queue(x -> {
            this.privateChannelId = x.getIdLong();
            getPrivateChannel().sendMessage(
                    user.getAsMention() + ", this is your private channel! While you're waiting for the game to start, please join the Pregame channel."
            ).queue();
        });
    }

    void startGame() {
        moveToRoom(game.entryRoom);
    }

    void moveToRoom(Room room) {
        VoiceChannel vc = game.guild.getVoiceChannelsByName(room.name, true).get(0);
        game.guild.moveVoiceMember(this.getMember(), vc).queue();
    }

    TextChannel getPrivateChannel() {
        return game.guild.getTextChannelById(privateChannelId);
    }

    Member getMember() {
        return game.guild.getMemberById(discordId);
    }
}
