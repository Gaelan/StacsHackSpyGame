package spygame;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.HashSet;

public class PlantMicrophoneMission extends SpyMission {
    private final HashSet<Room> plantedRooms = new HashSet<>();

    PlantMicrophoneMission(Game game) {
        super(game);
    }

    @Override
    public String describe() {
        return "Plant a microphone in each room at the party using the `plant mic` command. You must be alone in the room when you do so.";
    }

    @Override
    public void handleSpyMessage(MessageReceivedEvent event) {
        String msg = event.getMessage().getContentRaw();

        if (msg.equalsIgnoreCase("plant mic")) {
            Room room = getSpy().getCurrentRoom();

            if (plantedRooms.contains(room)) {
                getSpy().sendPrivateMessage("You've already planted a mic in this room!");
            } else if (room.playersInRoom.size() > 1) {
                getSpy().sendPrivateMessage("There's someone else in this room! They'd see you.");
            } else {
                plantedRooms.add(room);
                getSpy().sendPrivateMessage("You plant a microphone in the " + room.getName() + ".");
            }
        }
        else {
            getSpy().sendPrivateMessage("I'm afraid I don't understand.");
        }
    }

    @Override
    boolean spyWins() {
        return plantedRooms.size() == game.getRoomCount();
    }

    @Override
    String statusUpdate() {
        return "You've planted microphones in " + plantedRooms.size() + " out of " + game.getRoomCount() + " rooms.";
    }
}
