package edu.up.cs301.quoridor;

import android.util.Log;

import java.util.Random;

import edu.up.cs301.game.GameComputerPlayer;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;

/**
 * Created by lieu18 on 3/25/2018.
 */

public class QuoridorComputerPlayer extends GameComputerPlayer {

    protected Random randy;
    protected QuoridorGameState tempQgs;
    protected float wallChance;

    /**
     * constructor
     *
     * @param name the player's name (e.g., "John")
     */
    public QuoridorComputerPlayer(String name) {
        super(name);
        init();
    }

    protected void init(){
        randy = new Random();
        wallChance = 0.5f;
    }

    /*
     * dumb computer player - just only tries to move forward
     * does no other moves at the moment
     */
    protected void receiveInfo(GameInfo info) {
        if (info instanceof NotYourTurnInfo) return;

        QuoridorLocalGame qlg = (QuoridorLocalGame) this.game;
        QuoridorGameState qgs = qlg.state;
        int turn = qgs.getTurn();
        if(turn != this.getPlayerNum()) return;

        qgs.placeWall(turn,0,0);
        qgs.undo();

        tempQgs = new QuoridorGameState(qgs);

        boolean canL, canR, canU, canD;
        canL = canR = canU = canD = false;

        //postition of player 1
        int[] curPos = qgs.getPlayerPos(turn);
        int curWall = this.getPlayerNum() == 0 ? qgs.p1RemainingWalls : qgs.p2RemainingWalls;
        //postition of player 2
        int[] otherPos = qgs.getPlayerPos(1-turn);



        canL = tempQgs.moveLeft(curPos,otherPos,false) || tempQgs.moveLeft(curPos,otherPos,true) ;
        canR = tempQgs.moveRight(curPos,otherPos,false) || tempQgs.moveRight(curPos,otherPos,true);
        canU = tempQgs.moveUp(curPos,otherPos,false) || tempQgs.moveUp(curPos,otherPos,true);
        canD = tempQgs.moveDown(curPos,otherPos,false) || tempQgs.moveDown(curPos,otherPos,true);

        tempQgs.undo();

        int willPlaceWall = randy.nextFloat() > wallChance ? 0 : 1;

        if(curWall > 0) {
            switch (willPlaceWall) {
                case 0:
                    move(canL, canR, canU, canD);
                    break;
                case 1:
                    placeWall();
                    break;
                default:
                    Log.i("computer:move/wall", "This shouldn't execute.");
                    break;
            }
        }
        else {
            move(canL, canR, canU, canD);
        }

        //game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
        game.sendAction(new QuoridorFinalizeTurn(this));
    }

    protected void move(boolean l, boolean r, boolean u, boolean d){
        boolean validMove = false;
        while(!validMove) {
            switch(randy.nextInt(4)){
                case 0:
                    if(l){
                        game.sendAction(new QuoridorMovePawn(this,Direction.LEFT,randy.nextBoolean()));
                        validMove = true;
                        Log.i("left",""+l);
                    }
                    break;
                case 1:
                    if(r){
                        game.sendAction(new QuoridorMovePawn(this,Direction.RIGHT,randy.nextBoolean()));
                        validMove = true;
                        Log.i("right",""+r);
                    }
                    break;
                case 2:
                    if(u){
                        game.sendAction(new QuoridorMovePawn(this,Direction.UP,randy.nextBoolean()));
                        validMove = true;
                        Log.i("up",""+u);
                    }
                    break;
                case 3:
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
            }
        }
    }

    protected void placeWall(){
        boolean validMove = false;
        boolean isHorz = randy.nextBoolean();
        int x = randy.nextInt(8);
        int y = randy.nextInt(8);
        while(!validMove){
            Log.i("computer:wall","just (re)started) while loop");
            boolean wallValid = tempQgs.placeWall(this.getPlayerNum(),x,y);
            if(!isHorz) wallValid = tempQgs.rotateWall(this.getPlayerNum(),x,y) && wallValid;

            if(wallValid){
                validMove = true;
                game.sendAction(new QuoridorPlaceWall(this,x,y));
                if(!isHorz) game.sendAction(new QuoridorRotateWall(this,x,y));
            }
            else {
                tempQgs.undo();
                isHorz = randy.nextBoolean();
                x = randy.nextInt(8);
                y = randy.nextInt(8);
            }

        }

    }

    public int getPlayerNum() {return this.playerNum;}
}
