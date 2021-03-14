package spygame;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.Random;

public class FollowMovementsMission extends SpyMission {
    Player target;
    int secsWithTarget;
    int secsTotal;

    FollowMovementsMission(Game game) {
        super(game);

        if (game.players.size() == 1) {
            return;
        }

        Random r = new Random();
        do {
            target = game.players.get(r.nextInt(game.players.size()));
        } while (target.isSpy());

        new Thread(() -> {
            while (!game.finished) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                secsTotal++;
                if (getSpy().getCurrentRoom() == target.getCurrentRoom()) {
                    secsWithTarget++;
                }
            }
        }).start();
    }

    /**
     * Describe the mission to the spy. Shown at the beginning of the game.
     */
    @Override
    String describe() {
        if (target == null) {
            return "follow movements mission - requires at least 2 players";
        }
        return "Follow the movements of " + target.getName() + ". To win, you must spend at least 50% of the game in the same room as them.";
    }

    /**
     * Handle a message from the spy. Use this to implement any commands the spy can use ("plant mic", for example).
     */
    @Override
    void handleSpyMessage(MessageReceivedEvent event) {
        getSpy().sendPrivateMessage("I'm afraid I don't understand.");
    }

    /**
     * Called at the end of the game. Return true if the spy accomplished their task.
     */
    @Override
    boolean spyWins() {
        return secsWithTarget > (secsTotal / 2);
    }

    /**
     * Called every minute. Returns a string to show to the spy to let them know how they're doing with their mission.
     */
    @Override
    String statusUpdate() {
        int pct = 100 * secsWithTarget / secsTotal;
        return "You've been in the same room as " + target.getName() + " " + pct + "% of the time.";
    }

    @Override
    String leaderBoard() {
        return "";
    }
}
