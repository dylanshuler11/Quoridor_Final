package edu.up.cs301.quoridor;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lieu18 on 3/25/2018.
 */

public class QuoridorFinalizeTurn extends GameAction {

    private static final long serialVersionUID = 696969420L;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public QuoridorFinalizeTurn(GamePlayer player) {
        super(player);
    }

    /**
     * @return
     * 		whether this action is a finalize turn
     */
    public boolean isFinalize() {
        return true;
    }
}
