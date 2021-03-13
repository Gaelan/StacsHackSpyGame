package spygame;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;
import java.util.Objects;

public class Game {
    ArrayList<Player> players = new ArrayList<>();
    Guild guild;

//    static final long[] PRIVATE_CHANNEL_IDS = {
//            820305712113909770L,
//            820305725519560724L,
//            820305737489580053L,
//            820305746368659487L,
//            820305758456512512L,
//            820305777247649823L,
//            820305791009161267L,
//            820305802207559700L,
//            820305812223295518L,
//            820305821291380776L,
//    };

    Game(Guild guild) {
        this.guild = guild;
        for (TextChannel channel : getPrivateChannelsCategory().getTextChannels()) {
            channel.delete().queue();
        }
    }

    void addPlayer(User user) {
//        long channelId = PRIVATE_CHANNEL_IDS[players.size()];
        players.add(new Player(this, user));
    }

    Category getPrivateChannelsCategory() {
        return guild.getCategoriesByName("Private Channels", true).get(0);
    }
}
