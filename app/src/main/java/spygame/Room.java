package spygame;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;


public class Room {
    Game game;
    ArrayList<Player> playersInRoom = new ArrayList<>();
    String name;
    String description;
    ArrayList<Item> items;
    HashSet<Room> adjacentRooms = new HashSet<>();

    Room(Game game, String name, String description, ArrayList<Item> items) {
        this.game = game;
        this.name = name;
        this.description = description;
        this.items = items;
        game.addRoom(this);
    }

    String getName() {
        return name;
    }

    String getDescription() {
        return description;
    }

    ArrayList<Player> getPlayersInRoom() {
        return playersInRoom;
    }

    ArrayList<Item> getItems() {
        return items;
    }

    void addPlayer(Player player) {
        playersInRoom.add(player);
        getChannel().getManager().putPermissionOverride(
                player.getMember(),
                Collections.singletonList(Permission.VIEW_CHANNEL),
                Collections.emptyList()).queue(x ->
            game.guild.moveVoiceMember(player.getMember(), getChannel()).queue()
        );
    }

    void removePlayer(Player player) {
        playersInRoom.remove(player);
        getChannel().getManager().removePermissionOverride(player.getMember()).queue();
    }

    void addItem(Item item) {
        items.add(item);
    }

    void removeItem(Item item) {
        items.remove(item);
    }

    void addAdjacentRoom(Room room) {
        this.adjacentRooms.add(room);
        room.adjacentRooms.add(this);
    }

    VoiceChannel getChannel() {
        return game.guild.getVoiceChannelsByName(name, true).get(0);
    }
}
