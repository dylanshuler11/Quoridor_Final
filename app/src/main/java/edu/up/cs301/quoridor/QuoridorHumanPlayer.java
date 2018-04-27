package edu.up.cs301.quoridor;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import edu.up.cs301.game.GameHumanPlayer;
import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.R;
import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.infoMsg.IllegalMoveInfo;
import edu.up.cs301.game.infoMsg.NotYourTurnInfo;
/**
 * Created by lieu18 on 3/25/2018.
 */

public class QuoridorHumanPlayer extends GameHumanPlayer implements View.OnTouchListener, View.OnClickListener {
    /**
     * constructor
     *
     * @param name the name of the player
     */

    // the current activity
    private Activity myActivity;

    // the surface view
    private QuoridorSurfaceView surfaceView;

    // the ID for the layout to use
    private int layoutId;

    private QuoridorGameState qgs;

    //private QuoridorGameState quoridorGameState;

    public QuoridorHumanPlayer(String name, int layoutId) {
        super(name);
        this.layoutId = layoutId;
    }


    public View getTopView() {
        return myActivity.findViewById(R.id.top_gui_layout);
    }

    /*
     * human player receiving info from gamestate to update board
     */
    public void receiveInfo(GameInfo info) {
        if (surfaceView == null) return;

        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            //surfaceView.flash(Color.RED, 50);
        }
        else if (!(info instanceof QuoridorGameState))
            // if we do not have a TTTState, ignore
            return;
        else {

            surfaceView.setState((QuoridorGameState)info);
            qgs = (QuoridorGameState)info;
            surfaceView.invalidate();
            Log.i("human player", "receiving");
        }

    }

    public int getPlayerNum() {return this.playerNum;}

    /**
     * sets the current player as the activity's GUI
     */
    public void setAsGui(GameMainActivity activity) {

        // remember our activitiy
        myActivity = activity;

        // Load the layout resource for the new configuration
        activity.setContentView(layoutId);

        // set the surfaceView instance variable
        surfaceView = (QuoridorSurfaceView) myActivity.findViewById(R.id.quoridorBoard);
        Log.i("set listener","OnTouch");
        surfaceView.setOnTouchListener(this);

        Button newGame = (Button) activity.findViewById(R.id.newGameButton);
        Button finalize = (Button) activity.findViewById(R.id.finalizeTurnButton);
        Button undo = (Button) activity.findViewById(R.id.undoButton);

        newGame.setOnClickListener(this);
        finalize.setOnClickListener(this);
        undo.setOnClickListener(this);

    }

    /**
     * perform any initialization that needs to be done after the player
     * knows what their game-position and opponents' names are.
     */
    protected void initAfterReady() {
        myActivity.setTitle("Quoridor: "+allPlayerNames[0]+" vs. "+ allPlayerNames[1]);
    }


    /*
     * onClick method, checks for buttons being clicks and sends appropriate action
     */
    public void onClick(View v) {
        // if we are not yet connected to a game, ignore
        if (game == null) return;

        if (!(v instanceof Button)) return;

        // Construct the action and send it to the game
        switch(v.getId()){
            case R.id.undoButton:
                game.sendAction(new QuoridorUndoTurn(this));
                break;
            case R.id.newGameButton:
                game.sendAction(new QuoridorNewGame(this));
                break;
            case R.id.finalizeTurnButton:
                game.sendAction(new QuoridorFinalizeTurn(this));
                break;
            default:
                return;
        }

        surfaceView.invalidate(); //redraw the surface view


    }

    /*
     * onTouch method - handles all the touches that take place on the surface view
     */
    public boolean onTouch(View v, MotionEvent event) {
        /**
         External Citation
         Date: 21 March 2018
         Problem: Touching screen would rotate wall 30+ times (on one touch)
         Resource: https://stackoverflow.com/questions/5765904/
                    touch-release-method-in-android
         Solution: Used example code from this post (on release of touch event happens).
         */
        // ignore if not an "up" event
        if (event.getAction() != MotionEvent.ACTION_UP) return true;
        // register that we have handled the event

        if (!(v instanceof QuoridorSurfaceView))
            return true;

        int x = (int) event.getX();
        int y = (int) event.getY();

        //get values from the surface view to use later on
        int curX = surfaceView.startingX; //starting x position
        int curY = surfaceView.startingY; // starting y position
        int margin = surfaceView.margin;
        int squareSize = surfaceView.squareSize; //size of squares on surface view

        //QuoridorLocalGame qlg = (QuoridorLocalGame) this.game;


        //position of player 1
        int[] p1Pos = qgs.getPlayerPos(0);
        //position of player 2
        int[] p2Pos = qgs.getPlayerPos(1);
        //walls
        boolean[][] horzWalls = qgs.getHorzWalls();
        boolean[][] vertWalls = qgs.getVertWalls();

        int[][] playerPos = new int[][]{p1Pos,p2Pos};

        int turn = qgs.getTurn(); //get the turn from the local game

        //handles movement of pawn, whether left, right, up, or down
        if (x > curX + playerPos[turn][0] * margin - margin &&
                x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                y > curY + playerPos[turn][1] * margin &&
                y < curY + playerPos[turn][1] * margin + squareSize) {
            game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, false));
        }
        // RIGHT
        else if (x > curX + playerPos[turn][0] * margin + margin &&
                x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                y > curY + playerPos[turn][1] * margin &&
                y < curY + playerPos[turn][1] * margin + squareSize) {
            game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, false));
        }
        // UP
        else if (x > curX + playerPos[turn][0] * margin &&
                x < curX + playerPos[turn][0] * margin + squareSize &&
                y > curY + playerPos[turn][1] * margin - margin &&
                y < curY + playerPos[turn][1] * margin + squareSize - margin) {
            game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
        }
        // DOWN
        else if (x > curX + playerPos[turn][0] * margin &&
                x < curX + playerPos[turn][0] * margin + squareSize &&
                y > curY + playerPos[turn][1] * margin + margin &&
                y < curY + playerPos[turn][1] * margin + squareSize + margin) {
            game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, false));
        }

        //jump actions
        //check if adjacent in each direction
        //adjacent in left
        if(playerPos[0][1] == playerPos[1][1] &&
                playerPos[turn][0] - 1 == playerPos[1-turn][0]){
            if (x > curX + playerPos[turn][0] * margin - 2 * margin &&
                    x < curX + playerPos[turn][0] * margin + squareSize - 2 * margin &&
                    y > curY + playerPos[turn][1] * margin &&
                    y < curY + playerPos[turn][1] * margin + squareSize) {
                game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, false));
            }
        }
        //adjacent in right
        if(playerPos[0][1] == playerPos[1][1] &&
                playerPos[turn][0] + 1 == playerPos[1-turn][0]){
            if (x > curX + playerPos[turn][0] * margin + 2 * margin &&
                    x < curX + playerPos[turn][0] * margin + squareSize + 2 * margin &&
                    y > curY + playerPos[turn][1] * margin &&
                    y < curY + playerPos[turn][1] * margin + squareSize) {
                game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, false));
            }
        }
        //adjacent in up
        if(playerPos[0][0] == playerPos[1][0] &&
                playerPos[turn][1] - 1 == playerPos[1-turn][1]){
            if (x > curX + playerPos[turn][0] * margin &&
                    x < curX + playerPos[turn][0] * margin + squareSize &&
                    y > curY + playerPos[turn][1] * margin - 2*margin&&
                    y < curY + playerPos[turn][1] * margin + squareSize - 2*margin) {
                game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
            }
        }
        //adjacent in down
        if(playerPos[0][0] == playerPos[1][0] &&
                playerPos[turn][1] + 1 == playerPos[1-turn][1]){
            if (x > curX + playerPos[turn][0] * margin &&
                    x < curX + playerPos[turn][0] * margin + squareSize &&
                    y > curY + playerPos[turn][1] * margin + 2*margin&&
                    y < curY + playerPos[turn][1] * margin + squareSize + 2*margin) {
                game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, false));
            }
        }

        final int[] curPlayer = playerPos[turn];

        if (playerPos[0][0] == playerPos[1][0] &&
                playerPos[turn][1] - 1 == playerPos[1 - turn][1]) {

            //check for edge case
            if(playerPos[1-turn][1] == 0) {
                //up left
                if (x > curX + playerPos[turn][0] * margin - margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                        y > curY + playerPos[turn][1] * margin - margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.UP, true));
                }
                //up right
                if (x > curX + playerPos[turn][0] * margin + margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                        y > curY + playerPos[turn][1] * margin - margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
                }
            }

            //wall on top left
            //check for outOfBounds
            if(curPlayer[0]-1 >= 0 && curPlayer[1]-2 >= 0) {
                //check actual wall
                if (horzWalls[curPlayer[0] - 1][curPlayer[1] - 2]) {
                    //check location clicked
                    //up left
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.UP, true));
                    }
                    //up right
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
                    }
                }
            }

            //wall on top right
            //check for outOfBounds
            if(curPlayer[0] >= 0 && curPlayer[1]-2 >= 0 && curPlayer[0] < 8) {
                //check actual wall
                if (horzWalls[curPlayer[0]][curPlayer[1]-2]) {
                    //check location clicked
                    //up left
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.UP, true));
                    }
                    //up right
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
                    }
                }
            }
        } // move up


        //down
        if (playerPos[0][0] == playerPos[1][0] &&
                playerPos[turn][1] + 1 == playerPos[1 - turn][1]) {

            //check for edge case
            if(playerPos[1-turn][1] == 8) {
                //down left
                if (x > curX + playerPos[turn][0] * margin - margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                        y > curY + playerPos[turn][1] * margin + margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, true));
                }
                //down right
                if (x > curX + playerPos[turn][0] * margin + margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                        y > curY + playerPos[turn][1] * margin + margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, false));
                }
            }

            //wall on bot left
            //check for outOfBounds
            if(curPlayer[0]-1 >= 0 && curPlayer[1]+1 <= 7) {
                //check actual wall
                if (horzWalls[curPlayer[0]-1][curPlayer[1]+1]) {
                    //check location clicked
                    //down left
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, true));
                    }
                    //up right
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, false));
                    }
                }
            }

            //wall on BOT right
            //check for outOfBounds
            if(curPlayer[0] >= 0 && curPlayer[1]+1 >= 0 && curPlayer[1]+1 <= 7) {
                //check actual wall
                if (horzWalls[curPlayer[0]][curPlayer[1]+1]) {
                    //check location clicked
                    //up left
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.DOWN, true));
                    }
                    //up right
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.UP, false));
                    }
                }
            }
        } // move down

        //right
        if (playerPos[0][1] == playerPos[1][1] &&
                playerPos[turn][0] + 1 == playerPos[1 - turn][0]) {

            //check for edge case
            if(playerPos[1-turn][0] == 8) {
                //RIGHT UP
                if (x > curX + playerPos[turn][0] * margin + margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                        y > curY + playerPos[turn][1] * margin - margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, true));
                }
                //RIGHT DOWN
                if (x > curX + playerPos[turn][0] * margin + margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                        y > curY + playerPos[turn][1] * margin + margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, false));
                }
            }

            //wall on TOP RIGHT
            //check for outOfBounds
            if(curPlayer[0]+1 <= 7 && curPlayer[1]-1 >= 0) {
                //check actual wall
                if (vertWalls[curPlayer[0]+1][curPlayer[1]-1]) {
                    //check location clicked
                    //RIGHT UP
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, true));
                    }
                    //RIGHT DOWN
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, false));
                    }
                }
            }

            //wall on BOT right
            //check for outOfBounds
            if(curPlayer[0]+1 <= 7 && curPlayer[1] <= 7) {
                //check actual wall
                if (vertWalls[curPlayer[0]+1][curPlayer[1]]) {
                    //check location clicked
                    //RIGHT UP
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, true));
                    }
                    //RIGHT DOWN
                    if (x > curX + playerPos[turn][0] * margin + margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize + margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.RIGHT, false));
                    }
                }
            }
        } // move RIGHT

        if (playerPos[0][1] == playerPos[1][1] &&
                playerPos[turn][0] - 1 == playerPos[1 - turn][0]) {

            //check for edge case
            if(playerPos[1-turn][0] == 0) {
                //LEFT UP
                if (x > curX + playerPos[turn][0] * margin - margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                        y > curY + playerPos[turn][1] * margin - margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, true));
                }
                //LEFT DOWN
                if (x > curX + playerPos[turn][0] * margin - margin &&
                        x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                        y > curY + playerPos[turn][1] * margin + margin &&
                        y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                    game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, false));
                }
            }

            //wall on TOP left
            //check for outOfBounds
            if(curPlayer[0]-2 >= 0 && curPlayer[1]-1 >= 0) {
                //check actual wall
                if (vertWalls[curPlayer[0]-2][curPlayer[1]-1]) {
                    //check location clicked
                    //LEFT UP
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, true));
                    }
                    //LEFT DOWN
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, false));
                    }
                }
            }

            //wall on BOT left
            //check for outOfBounds
            if(curPlayer[0]-2 >= 0 && curPlayer[1] <= 7) {
                //check actual wall
                if (vertWalls[curPlayer[0]-2][curPlayer[1]]) {
                    //check location clicked
                    //left UP
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin - margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize - margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, true));
                    }
                    //left DOWN
                    if (x > curX + playerPos[turn][0] * margin - margin &&
                            x < curX + playerPos[turn][0] * margin + squareSize - margin &&
                            y > curY + playerPos[turn][1] * margin + margin &&
                            y < curY + playerPos[turn][1] * margin + squareSize + margin) {
                        game.sendAction(new QuoridorMovePawn(this, Direction.LEFT, false));
                    }
                }
            }
        } // move LEFT

        // PLACE WAllS PLAYER INTERACTIONS
        curX = surfaceView.startingX; //set a new starting X, so don't used change value for wall
        curY = surfaceView.startingY;
        for (int k = 0; k < 8; k ++) {
            for (int l = 0; l < 8; l++) {

                if(x > curX + squareSize && x < curX + margin && y > curY + squareSize
                        && y < curY + margin) {
                    //send wall rotate action if there is an already placed wall where the user clicks
                    if (surfaceView.state.getTempHWalls()[l][k] || surfaceView.state.getTempVWalls()[l][k]) {
                        game.sendAction(new QuoridorRotateWall(this, l, k));
                    }
                    // draw wall if no wall placed
                        game.sendAction(new QuoridorPlaceWall(this, l, k));
                }
                curX += surfaceView.margin; //increment the current X pos by the margin
            }
            curX = surfaceView.startingX;
            curY += margin;
        }

        surfaceView.invalidate(); //redraw the surface view

        return true;
    }
}
