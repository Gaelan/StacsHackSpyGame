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
    Room entryRoom;

    Game(Guild guild) {
        this.guild = guild;
        for (TextChannel channel : getPrivateChannelsCategory().getTextChannels()) {
            channel.delete().queue();
        }

        Room ballroom = new Room(this, "Ballroom", "", new ArrayList<>());
        Room diningRoom = new Room(this, "Dining Room", "", new ArrayList<>());
        Room bar = new Room(this, "Bar", "", new ArrayList<>());
        Room kitchen = new Room(this, "Kitchen", "", new ArrayList<>());
        Room cloakroom = new Room(this, "Cloakroom", "", new ArrayList<>());

        ballroom.addAdjacentRoom(diningRoom);
        bar.addAdjacentRoom(diningRoom);
        kitchen.addAdjacentRoom(diningRoom);
        cloakroom.addAdjacentRoom(ballroom);

        this.entryRoom = ballroom;
    }

    void addPlayer(User user) {
        players.add(new Player(this, user));
    }

    Category getPrivateChannelsCategory() {
        return guild.getCategoriesByName("Private Channels", true).get(0);
    }

    void start() {
        players.forEach(Player::startGame);
    }
}
