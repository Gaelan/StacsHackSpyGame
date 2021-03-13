package spygame;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

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
            getPrivateChannel().sendMessage(
                    user.getAsMention() + ", this is your private channel! While you're waiting for the game to start, please join the Pregame channel."
            ).queue();
        });
    }

    void startGame() {
        moveToRoom(game.entryRoom);
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
        String msg = event.getMessage().getContentRaw();
        if (msg.startsWith("!go")) {
            String roomName = msg.replace("!go ", "");
            currentRoom
                    .adjacentRooms.stream().filter(r -> r.getName().equalsIgnoreCase(roomName)).findAny()
                    .ifPresent(dest -> {
                        moveToRoom(dest);
                        getPrivateChannel()
                                .sendMessage("You enter the " + dest.getName() + ". " + dest.getDescription())
                                .queue();
                    });
        }
    }
}
