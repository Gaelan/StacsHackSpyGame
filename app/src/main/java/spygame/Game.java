package spygame;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

public class Game {
    ArrayList<Player> players = new ArrayList<>();
    Guild guild;
    Room entryRoom;
    boolean started;
    private Player spy;

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


        Item knife = new Item("Knife", "Kitchen knife");
        Item glass = new Item("Glass", "Small wine glass");
        Item coathanger = new Item("Coat hanger", "Coat hanger for hanging up coats");

        ballroom.addAdjacentRoom(diningRoom);
        bar.addAdjacentRoom(diningRoom);
        bar.addAdjacentRoom(kitchen);
        kitchen.addAdjacentRoom(diningRoom);
        cloakroom.addAdjacentRoom(ballroom);
        kitchen.addItem(knife);
        bar.addItem(glass);
        cloakroom.addItem(coathanger);

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
        Random rand = new Random();

        started = true;

        this.spy = players.get(rand.nextInt(players.size()));

        players.forEach(Player::startGame);

        List<Role> roles = guild.getRoles();
        for(Player player : players) {
            Role randomElement;
            do {
                randomElement = roles.get(rand.nextInt(roles.size()));
            } while (randomElement.getName().equalsIgnoreCase("admin"));
            System.out.print(randomElement);
            guild.addRoleToMember(player.getMember(), randomElement).queue();
        }
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

    public boolean isSpy(Player player) {
        return spy == player;
    }
}
