package edu.up.cs301.quoridor;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lieu18 on 3/25/2018.
 */

public class QuoridorUndoTurn extends GameAction {
    private static final long serialVersionUID = 420696942069L;
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public QuoridorUndoTurn(GamePlayer player) {
        super(player);
    }

    /**
     * @return
     * 		whether this action is an Undo turn
     */
    public boolean isUndo() {
        return true;
    }
}
