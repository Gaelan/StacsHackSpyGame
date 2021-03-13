package spygame;

import java.util.ArrayList;


public class Room {
    Game game;
    ArrayList<Player> playersInRoom;
    String name;
    String description;
    ArrayList<Item> items;

    Room(Game game, String name, String description, ArrayList<Item> items) {
        this.game = game;
        this.name = name;
        this.description = description;
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
    }

    void removePlayer(Player player) {
        playersInRoom.remove(player);
    }

    void addItem(Item item) {
        items.add(item);
    }

    void removeItem(Item item) {
        items.remove(item);
    }

    
}