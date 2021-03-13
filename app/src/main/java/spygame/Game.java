package spygame;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class Game {
    ArrayList<Player> players = new ArrayList<>();
    Guild guild;

    Game(Guild guild) {
        this.guild = guild;
    }

    void addPlayer(User user) {
        int num = players.size() + 1;
        players.add(new Player(this, user, num));

        TextChannel c = guild.getTextChannelsByName("player-" + num, false).get(0);
        c.sendMessage(user.getAsMention() + ", this is your private channel!").queue();
    }
}
