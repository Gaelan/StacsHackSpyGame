package spygame;

import net.dv8tion.jda.api.entities.User;

import java.util.ArrayList;

public class Player {
    Game game;
    long discordId;
    int playerNumber;

    Player(Game game, User user, int playerNumber) {
        this.game = game;
        this.discordId = user.getIdLong();
        this.playerNumber = playerNumber;
    }
}
