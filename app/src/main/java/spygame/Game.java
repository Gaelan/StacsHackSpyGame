package spygame;

import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class Game {
    // low for testing, probably should be something closer to 10 eventually
    private static final int GAME_LENGTH_MINUTES = 3;

    ArrayList<Player> players = new ArrayList<>();
    ArrayList<Room> rooms = new ArrayList<>();
    Guild guild;
    Room entryRoom;
    boolean started;
    private Player spy;
    private SpyMission spyMission;
    private int minutesLeft = GAME_LENGTH_MINUTES;

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
        this.spyMission = new PlantMicrophoneMission(this);

        players.forEach(Player::startGame);

        startGameTimer();

        // Not sure if we actually want discord roles for game roles? uncomment this if we do
//        List<Role> roles = guild.getRoles();
//        for(Player player : players) {
//            Role randomElement;
//            do {
//                randomElement = roles.get(rand.nextInt(roles.size()));
//            } while (randomElement.getName().equalsIgnoreCase("admin"));
//            System.out.print(randomElement);
//            guild.addRoleToMember(player.getMember(), randomElement).queue();
//        }
    }

    private void startGameTimer() {
        new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(60 * 1000); // 1 minute
                } catch (InterruptedException e) {
                    // don't think this should ever happen?
                    e.printStackTrace();
                    return;
                }
                this.minutesLeft -= 1;
                if (minutesLeft > 0) {
                    players.forEach(Player::announceTimeRemaining);
                } else {
                    gameOver();
                }
            }
        }).start();
    }

    private void gameOver() {
        String message = "Game over!\n";
        message += getSpy().getName() + " was the spy.\n";
        if (getSpyMission().spyWins()) {
            message += "The spy accomplished their mission, and won!\n";
        } else {
            message += "The spy did not accomplish their mission on time. Everybody else wins!";
        }
        String finalMessage = message;
        players.forEach(p -> p.sendPrivateMessage(finalMessage));
        movePlayersToPregame();
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

    public SpyMission getSpyMission() {
        return spyMission;
    }

    public Player getSpy() {
        return this.spy;
    }

    public int getRoomCount() {
        return this.rooms.size();
    }

    public void addRoom(Room room) {
        this.rooms.add(room);
    }

    public int getMinutesLeft() {
        return minutesLeft;
    }
}
