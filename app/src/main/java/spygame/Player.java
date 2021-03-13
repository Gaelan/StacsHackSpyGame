package spygame;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;

public class Player {
    Game game;
    long discordId;
    long privateChannelId;
    Room currentRoom;

    Player(Game game, User user) {
        this.game = game;
        this.discordId = user.getIdLong();

        String name = user.getAsTag().toLowerCase().replaceAll("[^a-z]", "-");
        game.getPrivateChannelsCategory().createTextChannel(name).queue(x -> {
            this.privateChannelId = x.getIdLong();
//            getPrivateChannel().getManager().setSlowmode(30).queue();
            getPrivateChannel().sendMessage(
                    user.getAsMention() + ", this is your private channel! While you're waiting for the game to start, please join the Pregame channel."
            ).queue();
        });

        this.moveToPregame();
    }

    void startGame() {
        moveToRoom(game.entryRoom);
        sendPrivateMessage("You arrive at the party, entering into the " + currentRoom.getName() + ". " + currentRoom.getDescription());
        sendPrivateMessage("To move, use commands like `go dining room`.");
    }

    void moveToRoom(Room room) {
        this.currentRoom = room;
        VoiceChannel vc = game.guild.getVoiceChannelsByName(room.name, true).get(0);
        game.guild.moveVoiceMember(this.getMember(), vc).queue();
    }

    TextChannel getPrivateChannel() {
        return game.guild.getTextChannelById(privateChannelId);
    }

    Member getMember() {
        return game.guild.getMemberById(discordId);
    }

    public void handleCommand(MessageReceivedEvent event) {
        if (event.getChannel() != getPrivateChannel()) {
            return;
        }

        String msg = event.getMessage().getContentRaw();
        if (msg.startsWith("go")) {
            String roomName = msg.replace("go ", "");
            currentRoom
                    .adjacentRooms.stream().filter(r -> r.getName().equalsIgnoreCase(roomName)).findAny()
                    .ifPresentOrElse(dest -> {
                        moveToRoom(dest);
                        sendPrivateMessage("You enter the " + dest.getName() + ". " + dest.getDescription());
                    }, () -> {
                        sendPrivateMessage("That's not a room you can visit from here.");
                        look();
                    });
        } else if (msg.equalsIgnoreCase("look")) {
            look();
        } else {
            sendPrivateMessage("I'm afraid I don't understand.");
        }
    }

    private void look() {
        sendPrivateMessage("You're currently in the " + currentRoom.getName() + ". " + currentRoom.getDescription());
    }

    public void sendPrivateMessage(String message) {
        getPrivateChannel().sendMessage(message).queue();
    }

    public void moveToPregame() {
        VoiceChannel vc = game.guild.getVoiceChannelsByName("Pregame", true).get(0);
        try {
            game.guild.moveVoiceMember(this.getMember(), vc).queue();
        } catch (IllegalStateException e) {
            // they might not be in voice at all; that's fine.
        }
    }

    public boolean readyToStart() {
        return Objects.requireNonNull(getMember().getVoiceState()).getChannel() != null;
    }
}
