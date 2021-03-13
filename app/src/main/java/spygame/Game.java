package spygame;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Game {
    ArrayList<Player> players = new ArrayList<>();
    Guild guild;
    Room entryRoom;
    boolean started;

    Game(Guild guild) {
        this.guild = guild;
        for (TextChannel channel : getPrivateChannelsCategory().getTextChannels()) {
            channel.delete().queue();
        }

        Room ballroom = new Room(this, "Ballroom", "From here, you can go into the cloakroom or out into the dining room.", new ArrayList<>());
        Room diningRoom = new Room(this, "Dining Room", "From here, you can visit the bar, kitchen, or ballroom.", new ArrayList<>());
        Room bar = new Room(this, "Bar", "From here, you can visit the dining room or kitchen.", new ArrayList<>());
        Room kitchen = new Room(this, "Kitchen", "From here, you can visit the dining room or bar.", new ArrayList<>());
        Room cloakroom = new Room(this, "Cloakroom", "You can return to the ballroom.", new ArrayList<>());

        ballroom.addAdjacentRoom(diningRoom);
        bar.addAdjacentRoom(diningRoom);
        bar.addAdjacentRoom(kitchen);
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

    public boolean readyToStart() {
        return players.stream().allMatch(Player::readyToStart);
    }

    void start() {
        started = true;
        players.forEach(Player::startGame);
    }

    public void handlePlayerCommand(MessageReceivedEvent event) {
        if (!started) return;
        long id = event.getAuthor().getIdLong();
        Optional<Player> player = players.stream().filter(p -> p.discordId == id).findAny();
        player.ifPresent(value -> value.handleCommand(event));
    }

    public void movePlayersToPregame() {
        players.forEach(Player::moveToPregame);
    }
}
