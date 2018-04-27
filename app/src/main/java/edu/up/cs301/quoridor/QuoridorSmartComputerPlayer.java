package edu.up.cs301.quoridor;

import android.util.Log;

import java.util.Random;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;

/**
 * Created by AnthonyLieu on 4/18/18.
 */

public class QuoridorSmartComputerPlayer extends QuoridorComputerPlayer {

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public QuoridorSmartComputerPlayer(String name) {
        super(name);

    }

    @Override
    protected void init(){
        randy = new Random();
        wallChance = 0.35f;
    }

    /**
     * move:
     * left: 0 - 2
     * right: 3-5
     * up: 6 & 7
     * down: 8 & 9
     * up/down: 10 - 22
     *
     * @param l left
     * @param r right
     * @param u up
     * @param d down
     */
    @Override
    protected void move(boolean l, boolean r, boolean u, boolean d){
        boolean validMove = false;
        int dir = randy.nextInt(40);
        //make up/down movement a lot more likely
        if(this.getPlayerNum() == 0){ //go down more often
            dir = (dir > 9) ? 8 : dir;
        }
        else if (this.getPlayerNum() == 1) { // go up more often
            dir = (dir > 9) ? 6 : dir;
        }


        while(!validMove) {
            switch(dir){
                case 0:
                case 1:
                case 2:
                    if(l){
                        game.sendAction(new QuoridorMovePawn(this,Direction.LEFT,randy.nextBoolean()));
                        validMove = true;
                        Log.i("left",""+l);
                    }
                    break;
                case 3:
                case 4:
                case 5:
                    if(r){
                        game.sendAction(new QuoridorMovePawn(this,Direction.RIGHT,randy.nextBoolean()));
                        validMove = true;
                        Log.i("right",""+r);
                    }
                    break;
                case 6:
                case 7:
                    if(u){
                        game.sendAction(new QuoridorMovePawn(this,Direction.UP,randy.nextBoolean()));
                        validMove = true;
                        Log.i("up",""+u);
                    }
                    break;
                case 8:
                case 9:
                    if(d){
                        game.sendAction(new QuoridorMovePawn(this,Direction.DOWN,randy.nextBoolean()));
                        validMove = true;
                        Log.i("down",""+d);
                    }
                    break;
                default:
                    Log.i("computer player","oh shoot waddup");
                    validMove = false;
                    break;
            }//switch
            dir = randy.nextInt(10); // if selected weighted move isn't available just choose rand
        }
    }

    /**
     * placeWall
     * Decides how the computer places the wall
     */
    @Override
    protected void placeWall(){
        //find location of opponent player
        int turn = tempQgs.getTurn();
        int[] otherPos = new int[]{tempQgs.getPlayerPos(turn-1)[0],tempQgs.getPlayerPos(turn-1)[1]};

        //attempt to place horzWall below him 0,0
        if(this.getPlayerNum() == 1) {
            if (tempQgs.placeWall(turn, otherPos[0], otherPos[1])) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0], otherPos[1]));
                return;
            }
            tempQgs.undo();
            //attempt to place horzWall below left -1,0
            if (tempQgs.placeWall(turn, otherPos[0] - 1, otherPos[1])) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0] - 1, otherPos[1]));
                return;
            }
            tempQgs.undo();
            //attempt to place vert wall 0,-1
            if (tempQgs.placeWall(turn, otherPos[0], otherPos[1] - 1)) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0], otherPos[1] - 1));
                game.sendAction(new QuoridorRotateWall(this, otherPos[0], otherPos[1] - 1));
                return;
            }
            tempQgs.undo();
            //attempt to place vert wall -1,-1
            if (tempQgs.placeWall(turn, otherPos[0] - 1, otherPos[1] - 1)) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0] - 1, otherPos[1] - 1));
                game.sendAction(new QuoridorRotateWall(this, otherPos[0] - 1, otherPos[1] - 1));
                return;
            }
            tempQgs.undo();
        }
        else if (this.getPlayerNum() == 0) {
            if (tempQgs.placeWall(turn, otherPos[0], otherPos[1]-1)) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0], otherPos[1]-1));
                return;
            }
            tempQgs.undo();
            //attempt to place horzWall below left -1,0
            if (tempQgs.placeWall(turn, otherPos[0] - 1, otherPos[1]-1)) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0] - 1, otherPos[1]-1));
                return;
            }
            tempQgs.undo();
            //attempt to place vert wall 0,-1
            if (tempQgs.placeWall(turn, otherPos[0], otherPos[1])) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0], otherPos[1]));
                game.sendAction(new QuoridorRotateWall(this, otherPos[0], otherPos[1]));
                return;
            }
            tempQgs.undo();
            //attempt to place vert wall -1,-1
            if (tempQgs.placeWall(turn, otherPos[0] - 1, otherPos[1])) {
                game.sendAction(new QuoridorPlaceWall(this, otherPos[0] - 1, otherPos[1]));
                game.sendAction(new QuoridorRotateWall(this, otherPos[0] - 1, otherPos[1]));
                return;
            }
            tempQgs.undo();
        }

        //otherwise place random wall
        super.placeWall();

    }
}

