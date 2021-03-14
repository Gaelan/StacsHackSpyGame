package spygame;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class SpyMission {
    protected final Game game;

    SpyMission(Game game) {
        this.game = game;
    }

    /**
     * Describe the mission to the spy. Shown at the beginning of the game.
     */
    abstract String describe();

    /**
     * Handle a message from the spy. Use this to implement any commands the spy can use ("plant mic", for example).
     */
    abstract void handleSpyMessage(MessageReceivedEvent event);

    /**
     * Called at the end of the game. Return true if the spy accomplished their task.
     */
    abstract boolean spyWins();

    /**
     * Called every minute. Returns a string to show to the spy to let them know how they're doing with their mission.
     */
    abstract String statusUpdate();

    Player getSpy() {
        return game.getSpy();
    }
}
