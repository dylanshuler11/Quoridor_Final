package edu.up.cs301.quoridor;

import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.actionMsg.GameAction;

/**
 * Created by lieu18 on 3/25/2018.
 */

public class QuoridorPlaceWall extends GameAction {
    private static final long serialVersionUID = 696942069L;

    private int x;
    private int y;
    private int playerNum;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     * @param x the x position from where the action was clicked
     * @param y the y position from where the action was clicked
     */
    public QuoridorPlaceWall(GamePlayer player, int x, int y) {

        super(player);

        if (player instanceof QuoridorHumanPlayer) {
            this.playerNum = ((QuoridorHumanPlayer) player).getPlayerNum();
            this.x = x;
            this.y = y;
        }
        else if (player instanceof QuoridorComputerPlayer) {
            this.playerNum = ((QuoridorComputerPlayer) player).getPlayerNum();
            this.x = x;
            this.y = y;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPlayerNum() {
        return playerNum;
    }


    /**
     * @return
     * 		whether this action is a place wall
     */
    public boolean isPlaceWall() {
        return true;
    }
}
