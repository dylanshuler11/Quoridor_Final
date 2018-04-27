package edu.up.cs301.quoridor;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by manalili18 on 3/27/2018.
 */

public class QuoridorNewGame extends GameAction {

    private static final long serialVersionUID = 42069L;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public QuoridorNewGame(GamePlayer player) {

        super(player);

    }

    public boolean isNewGame() {
        return true;
    }
}
