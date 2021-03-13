package spygame;

import java.util.ArrayList;
import java.util.HashSet;


public class Room {
    Game game;
    ArrayList<Player> playersInRoom;
    String name;
    String description;
    ArrayList<String> items;
    HashSet<Room> adjacentRooms = new HashSet<>();

    Room(Game game, String name, String description, ArrayList<String> items) {
        this.game = game;
        this.name = name;
        this.description = description;
        this.items = items;
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

    ArrayList<String> getItems() {
        return items;
    }

    void addPlayer(Player player) {
        playersInRoom.add(player);
    }

    void removePlayer(Player player) {
        playersInRoom.remove(player);
    }

    void addItem(String item) {
        items.add(item);
    }

    void removeItem(String item) {
        items.remove(item);
    }

    void addAdjacentRoom(Room room) {
        this.adjacentRooms.add(room);
        room.adjacentRooms.add(this);
    }
}
