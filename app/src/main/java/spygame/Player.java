package spygame;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Objects;
import java.util.Random;

public class Player {
    Game game;
    long discordId;
    long privateChannelId;
    Room currentRoom;
    Item currentItem;
    private Player votingAgainst;
    private String[] tasks = {"hang up your coat in the cloakroom", "dance in the ballroom", "get a drink from the bar", "get food from the kitchen", "go mingle with the other people"};

    Player(Game game, User user) {
        this.game = game;
        this.discordId = user.getIdLong();
        this.currentItem = null;

        String name = user.getAsTag().toLowerCase().replaceAll("[^a-z]", "-");
        game.getPrivateChannelsCategory().createTextChannel(name).queue(x -> {
            this.privateChannelId = x.getIdLong();
//            getPrivateChannel().getManager().setSlowmode(30).queue();
            getPrivateChannel().sendMessage(
                    user.getAsMention() + ", this is your private channel! While you're waiting for the game to start, please join the Pregame voice channel."
            ).queue();
        });

        this.moveToPregame();
    }

    void startGame() {
        moveToRoom(game.entryRoom);
        sendPrivateMessage("You arrive at the party, entering into the " + currentRoom.getName() + ". " + currentRoom.getDescription());
        sendPrivateMessage("To move, use commands like `go dining room`.");
        if (game.isSpy(this)) {
            sendPrivateMessage(":detective: You're the spy! Don't tell anyone else. Your secret mission: " + game.getSpyMission().describe());
        }
        Random rand = new Random();
        int int1 = rand.nextInt(tasks.length);
        int int2 = rand.nextInt(tasks.length);
        sendPrivateMessage("Hint: the game may be more fun if you: " + tasks[int1] + " and " + tasks[int2] + ".");
        
    }

    void moveToRoom(Room room) {
        if (currentRoom != null) {
            this.currentRoom.removePlayer(this);
        }
        this.currentRoom = room;
        this.currentRoom.addPlayer(this);
    }

    TextChannel getPrivateChannel() {
        return game.guild.getTextChannelById(privateChannelId);
    }

    Member getMember() {
        return game.guild.getMemberById(discordId);
    }

    String getName() {
        return getMember().getEffectiveName();
    }

    public void handleCommand(MessageReceivedEvent event) {
        if (event.getChannel() != getPrivateChannel()) {
            return;
        }

        String msg = event.getMessage().getContentRaw();
        if (msg.startsWith("go ")) {
            String roomName = msg.replace("go ", "");
            currentRoom
                    .adjacentRooms.stream().filter(r -> r.getName().equalsIgnoreCase(roomName)).findAny()
                    .ifPresentOrElse(dest -> {
                        for (Player player : this.currentRoom.playersInRoom) {
                            if (player != this) {
                                player.notifyPlayerLeft(this, dest);
                            }
                        }
                        for (Player player : dest.playersInRoom) {
                            if (player != this) {
                                player.notifyPlayerEntered(this, this.currentRoom);
                            }
                        }
                        moveToRoom(dest);
                        sendPrivateMessage("You enter the " + dest.getName() + ". " + dest.getDescription());
                    }, () -> {
                        sendPrivateMessage("That's not a room you can visit from here.");
                        look();
                    });
        } else if (msg.equalsIgnoreCase("look")) {
            look();
        } else if (msg.startsWith("pick up ")) {
            String itemName = msg.replace("pick up ", "");
            currentRoom
                .items.stream().filter(i -> i.getName().equalsIgnoreCase(itemName)).findAny()
                .ifPresentOrElse(item -> {
                    currentRoom.removeItem(item);
                    sendPrivateMessage("You picked up " + item.getName());
                    currentItem = item;
                }, () -> {
                    sendPrivateMessage("That item is not in this room.");
                    look();
                });
        } else if (msg.equalsIgnoreCase("inv")) {
            inv();
        } else if (msg.startsWith("vote ")) {
            String playerName = msg.replace("vote ", "");
            game.players.stream()
                .filter(p -> p.getName().equalsIgnoreCase(playerName))
                .findAny().ifPresentOrElse(p -> {
                    this.votingAgainst = p;
                    sendPrivateMessage("You are now voting against " + p.getName() + ".");
                }, () -> {
                    sendPrivateMessage("I don't know who that is.");
                });
        } else if (msg.startsWith("drop")) {
            if (msg.equals("drop")) {
                if (currentItem != null) {
                    currentRoom.addItem(currentItem);
                    sendPrivateMessage("Dropped " + currentItem.getDescription());
                    currentItem = null;
                }
                else {
                    sendPrivateMessage("You aren't carrying anything!");
                }
            }
            else {
                String itemName = msg.replace("drop ","");
                if (currentItem.getName().equalsIgnoreCase(itemName)) {
                    currentRoom.addItem(currentItem);
                    sendPrivateMessage("Dropped " + currentItem.getDescription());
                    currentItem = null;
                }
            }
        } else if (msg.equals("dance")) {
            sendPrivateMessage("Wow, you have some good moves!");
        } else if (msg.startsWith("get")) {
            System.out.println(msg);
            if (msg.equals("get food")) {
                if (currentRoom.getName().equals("Kitchen")) {
                    currentRoom
                    .items.stream().filter(i -> i.getName().equalsIgnoreCase("plate of food")).findAny()
                    .ifPresentOrElse(item -> {
                        sendPrivateMessage("You picked up some food");
                        currentItem = item;
                    }, () -> {
                    });
                }
                else {
                    sendPrivateMessage("There is no food here!");
                }
            }
            else if (msg.equals("get drink")) {
                if (currentRoom.getName().equals("Bar")) {
                    currentRoom
                    .items.stream().filter(i -> i.getName().equalsIgnoreCase("glass of whisky")).findAny()
                    .ifPresentOrElse(item -> {
                        sendPrivateMessage("You picked up a glass of whisky");
                        currentItem = item;
                    }, () -> {
                    });
                }
                else {
                    sendPrivateMessage("There are no drinks here!");
                }
            }
            else {
                sendPrivateMessage("You can't get that!");
            }
        } else if (msg.startsWith("eat")) {
            if (msg.equals("eat food")) {
                if (currentRoom.getName().equalsIgnoreCase("Dining Room")) {
                    if (currentItem.getName().equalsIgnoreCase("plate of food")) {
                        sendPrivateMessage("You ate the food!");
                    currentItem = null;
                    }
                    else {
                        sendPrivateMessage("You aren't carrying any food!");
                    }
                }
                else {
                    sendPrivateMessage("You can't eat here!");
                }
            }
            else {
                sendPrivateMessage("You can't eat that!");
            }
        } else if (msg.equals("drink") || msg.equals("drink whisky")) {
            if (currentItem.getName().equalsIgnoreCase("glass of whisky")) {
                    sendPrivateMessage("You drank the whisky!");
                currentItem = null;
                }
            else {
                sendPrivateMessage("You aren't carrying a drink!");
            }
        } 

        else if (msg.startsWith("hang up")) {
            if (msg.equals("hang up coat")) {
                if (currentRoom.getName().equals("Cloakroom")) {
                    sendPrivateMessage("You hung your coat up!");
                }
                else {
                    sendPrivateMessage("You can't hang that up in this room!");
                }
            }
            else {
                sendPrivateMessage("You can't hang that up!");
            }
        }
        
        else if (msg.equals("?")) {
            sendPrivateMessage("You can\n"+
                               "go (place), look, pick up (object), inv, vote (username), drop (object), drop, dance, get(food/drink), eat(food), drink, ?");
        } else if (this.isSpy()) {
            game.getSpyMission().handleSpyMessage(event);
        } else {
            sendPrivateMessage("I'm afraid I don't understand.");
        }
        
    }

    public boolean isSpy() {
        return game.isSpy(this);
    }

    private void inv() {
        if (currentItem == null) {
            sendPrivateMessage("You're not carrying anything!");
        }
        else {
            sendPrivateMessage("You are carrying " + currentItem.getDescription());
        }
    }

    private void notifyPlayerLeft(Player player, Room dest) {
        sendPrivateMessage(player.getName() + " leaves for the " + dest.getName() + ".");
    }

    private void notifyPlayerEntered(Player player, Room source) {
        sendPrivateMessage(player.getName() + " enters from the " + source.getName() + ".");
    }

    private void look() {
        sendPrivateMessage("You're currently in the " + currentRoom.getName() + ". " + currentRoom.getDescription());
        sendPrivateMessage("You can see: \n");
        for (Item item : currentRoom.getItems()) {
            sendPrivateMessage(item.getName() + "\n");
        }
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

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void announceTimeRemaining() {
        if (game.getMinutesLeft() == 1) {
            sendPrivateMessage("There is 1 minute remaining.");
        } else {
            sendPrivateMessage("There are " + game.getMinutesLeft() + " minutes remaining.");
        }
        if (this.isSpy()) {
            sendPrivateMessage(game.getSpyMission().statusUpdate());
        }
    }

    public Player getVotingAgainst() {
        return votingAgainst;
    }
}
