package spygame;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;

public class Player {
    Game game;
    long discordId;
    int playerNumber;

    Player(Game game, User user, int playerNumber) {
        this.game = game;
        this.discordId = user.getIdLong();
        this.playerNumber = playerNumber;
        VoiceChannel vc = game.guild.getVoiceChannelsByName("Ballroom", false).get(0);
        game.guild.moveVoiceMember(this.getMember(), vc).queue();
    }

    Member getMember() {
        return game.guild.getMemberById(discordId);
    }
}
