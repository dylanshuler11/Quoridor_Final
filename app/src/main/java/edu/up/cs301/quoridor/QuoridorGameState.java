package edu.up.cs301.quoridor;

import edu.up.cs301.game.infoMsg.GameState;

import android.util.Log;

import java.util.Stack;

/**
 * Created by manalili18 on 2/21/2018.
 *
 * @author Noah Davis
 * @author Anthony Lieu
 * @author Phillip Manalili
 * @author Dylan Shuler
 */


public class QuoridorGameState extends GameState {

    //nux told me to
    private static final long serialVersionUID = 6969420L;

    private int turn; // 0 -> player 1, 1 -> player 2
    private int[] p1Pos, p2Pos, tempPos;

    private boolean[][] horzWalls, tempHWalls;
    private boolean[][] vertWalls, tempVWalls;

    protected int p1RemainingWalls, p2RemainingWalls, tempRemWalls = 10;

    protected boolean wallDown = false;
    private boolean hasMoved = false;

    private boolean visitedSpot[][] = new boolean[9][9];
    private boolean initCheck = false; //used for initializing pathCheck array

    private QuoridorGameState tempQuo;

    public QuoridorGameState() {
        init();
    }

    //copy ctor
    public QuoridorGameState(QuoridorGameState g) {
        this.turn = g.turn;

        this.p1Pos = new int[]{g.p1Pos[0], g.p1Pos[1]};
        this.p2Pos = new int[]{g.p2Pos[0], g.p2Pos[1]};
        this.horzWalls = new boolean[8][8];
        this.vertWalls = new boolean[8][8];
        this.p1RemainingWalls = g.p1RemainingWalls;
        this.p2RemainingWalls = g.p2RemainingWalls;

        this.tempPos = new int[]{g.tempPos[0], g.tempPos[1]};
        this.tempHWalls = new boolean[8][8];
        this.tempVWalls = new boolean[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.tempHWalls[i][j] = g.tempHWalls[i][j];
                this.tempVWalls[i][j] = g.tempVWalls[i][j];
                this.horzWalls[i][j] = g.horzWalls[i][j];
                this.vertWalls[i][j] = g.vertWalls[i][j];
            }
        }
        hasMoved = false;
        wallDown = false;
    }

    private void init() {
        this.turn = 0;
        this.p1Pos = new int[]{4, 0};
        this.p2Pos = new int[]{4, 8};


        this.horzWalls = new boolean[8][8];
        this.vertWalls = new boolean[8][8];
        this.tempHWalls = new boolean[8][8];
        this.tempVWalls = new boolean[8][8];
        this.tempPos = new int[]{0, 0};

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                this.horzWalls[i][j] = this.vertWalls[i][j] = false;
                this.tempHWalls[i][j] = this.tempVWalls[i][j] = false;
            }
        }

        this.tempPos = new int[]{this.p1Pos[0], this.p1Pos[1]};
        this.tempRemWalls = this.p1RemainingWalls = this.p2RemainingWalls = 10;

        hasMoved = false;
        wallDown = false;
    }

    public int getTurn() {
        return turn;
    }

    public boolean[][] getVertWalls() {
        return vertWalls;
    }

    public boolean[][] getHorzWalls() {
        return horzWalls;
    }

    public boolean[][] getTempVWalls() {
        return tempVWalls;
    }

    public boolean[][] getTempHWalls() {
        return tempHWalls;
    }

    /**
     * Get the location of a player on the board
     *
     * @param player which player's position you want
     * @return x y coordinates of player
     */
    public int[] getPlayerPos(int player) {
        return player == 0 ? p1Pos : p2Pos;
    }

    public int[] getTempPlayerPos() {
        return tempPos;
    }

    public void setPlayerPos(int x, int y, int player) {
        if (player == 0) {
            p1Pos[0] = x;
            p1Pos[1] = y;
        } else if (player == 1) {
            p2Pos[0] = x;
            p2Pos[1] = y;
        }
    }

    /**
     * Check if there is a wall placed at a given x y coordinate
     *
     * @param x   x position to check
     * @param y   y position to check
     * @param dir 0 for horizontal, 1 for vertical
     * @return true if there is a wall
     */
    public boolean checkForWall(int x, int y, int dir) {
        return dir == 0 ? horzWalls[x][y] : vertWalls[x][y];
    }

    // prints all instance variables
    // format: instance variable followed by its value(s) delimited by %%
    // instance variables delimited by newlines
    // format for 2d boolean array, rows separated by newlines, columns delimited by %%
    @Override
    public String toString() {
        String result = "";

        result += "turn%%" + turn + "\n";

        result += "p1Pos%%" + p1Pos[0] + "%%" + p1Pos[1] + "\n";

        result += "p2Pos%%" + p2Pos[0] + "%%" + p2Pos[1] + "\n";

        result += "horzWalls\n";

        result += wallMatrixToString(horzWalls);
        result += "Vert Walls\n";
        result += wallMatrixToString(vertWalls);

        result += "p1RemainingWalls%%" + p1RemainingWalls + "\n";
        result += "p2RemainingWalls%%" + p2RemainingWalls + "\n";

        return result;
    }

    /**
     * toString helper method for 2d boolean matrices.
     * print each boolean in horzWalls
     * elements delimited by %%
     * rows delimited by newlines
     *
     * @param wallMatrix
     * @return String representation of input matrix
     */

    /**
     External Citation
     Date: 21 March 2018
     Problem: Needed to access last iteration of foreach loop
     Resource: https://stackoverflow.com/questions/41591107/detect-last-foreach-loop-iteration
     Solution: Used example code from this post.
     */

    private String wallMatrixToString(boolean[][] wallMatrix) {

        String result = "";

        for (boolean[] row : wallMatrix) {
            int i = 0;
            for (boolean b : row) {
                result += b;
                if (i++ != row.length - 1) result += "%%";
            }
            result += "\n";
        }

        return result;
    }

    /**
     * Move pawn according to direction. If necessary, referrences jump for various jump
     * cases.
     *
     * @param player who's turn it is
     * @param dir    direction of movement
     * @param jump   boolean that determines extra diagonal jumps
     * @return true if success, else false
     */
    public boolean movePawn(int player, Direction dir, boolean jump) {
        if (wallDown) {
            return false;
        }

        //moving player is in first slot of bothPlayers[]
        int[][] bothPlayers = new int[][]{p1Pos, p2Pos};
        boolean result;
        //check bounds
        if (player < 0 || player > 1) {
            return false;
        }
        //make sure the player can move
        if (player != turn) {
            return false;
        }
        //check if valid move
        //ie, check for walls, other players
        switch (dir) {
            case UP:
                result = moveUp(bothPlayers[player], bothPlayers[1 - player], jump);
                Log.i("movePawn", "moved player up");
                break;
            case DOWN:
                result = moveDown(bothPlayers[player], bothPlayers[1 - player], jump);
                Log.i("movePawn", "moved down");
                break;
            case RIGHT:
                result = moveRight(bothPlayers[player], bothPlayers[1 - player], jump);
                break;
            case LEFT:
                result = moveLeft(bothPlayers[player], bothPlayers[1 - player], jump);
                break;
            default:
                result = false;
                Log.i("movePawn", "Something went wrong");
        }
        hasMoved = result ? true : false;
        return result;
    }

    /**
     * This returns a 12 length array of relevant walls.
     * The first accessor determines horz (0) or vert (1)
     * <p>
     * The second accesor is the location of the wall with respect to the x,y coordinates in the
     * following order:
     * (-1,-2),(0,-2),(-2,-1),(-1,-1),(0,-1),(1,-1),(-2,0),(-1,0),(0,0),(1,0),(-1,1),(0,1)
     * <p>
     * Computations at the edges of board will return true, as edges are treated the same as walls.
     *
     * @param x
     * @param y
     * @return list of walls relevant to current position
     */
    private boolean[][] getRelevantWalls(int x, int y) {

        boolean[][] result = new boolean[2][12];

        //init result
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 12; j++) {
                result[i][j] = false;
            }
        }

        //if invalid inputs return null
        if (x < 0 || y < 0 || x > 8 || y > 8) return result;

        //walls 2 above x,y
        if (y - 2 >= 0) {
            if (x - 1 >= 0 && x - 1 < 8) {
                result[0][0] = horzWalls[x - 1][y - 2];
                result[1][0] = vertWalls[x - 1][y - 2];
            }

            if (x < 8) {
                result[0][1] = horzWalls[x][y - 2];
                result[1][1] = vertWalls[x][y - 2];
            }
        }

        //walls directly above x,y
        if (y - 1 >= 0) {
            if (x - 2 >= 0) {
                result[0][2] = horzWalls[x - 2][y - 1];
                result[1][2] = vertWalls[x - 2][y - 1];
            }

            if (x - 1 >= 0 && x - 1 < 8) {
                result[0][3] = horzWalls[x - 1][y - 1];
                result[1][3] = vertWalls[x - 1][y - 1];
            }

            if (x < 8) {
                result[0][4] = horzWalls[x][y - 1];
                result[1][4] = vertWalls[x][y - 1];
            }

            if (x + 1 < 8) {
                result[0][5] = horzWalls[x + 1][y - 1];
                result[1][5] = vertWalls[x + 1][y - 1];
            }
        }

        //walls on same line as x,y
        if (y < 8) {
            if (x - 2 >= 0) {
                result[0][6] = horzWalls[x - 2][y];
                result[1][6] = vertWalls[x - 2][y];
            }

            if (x - 1 >= 0 && x - 1 < 8) {
                result[0][7] = horzWalls[x - 1][y];
                result[1][7] = vertWalls[x - 1][y];
            }

            if (x < 8) {
                result[0][8] = horzWalls[x][y];
                result[1][8] = vertWalls[x][y];
            }

            if (x + 1 < 8) {
                result[0][9] = horzWalls[x + 1][y];
                result[1][9] = vertWalls[x + 1][y];
            }
        }

        //walls 1 below
        if (y + 1 < 7) {
            if (x - 1 >= 0 && x + 1 < 8) {
                result[0][10] = horzWalls[x - 1][y + 1];
                result[1][10] = vertWalls[x - 1][y + 1];
            }

            if (x < 8) {
                result[0][11] = horzWalls[x][y + 1];
                result[1][11] = vertWalls[x][y + 1];
            }
        }

        return result;
    }


    /**
     * Method to move pawn up
     *
     * @param currentPlayer
     * @param otherPlayer
     * @param jump          true move left, false move right
     * @return
     */
    public boolean moveUp(int[] currentPlayer, int[] otherPlayer, boolean jump) {
        int curX = currentPlayer[0];
        int curY = currentPlayer[1];
        int otherX = otherPlayer[0];
        int otherY = otherPlayer[1];
        int jumpMod = 0;

        boolean[][] relevantWalls = getRelevantWalls(curX, curY);

        try {
            if (curY == 0) //player is trying to move past top
            {
                return false;
            }
            //check if there are walls in front
            if (relevantWalls[0][3]) {
                return false;
            }
            if (relevantWalls[0][4]) {
                return false;
            }

            //check if players are adjacent
            if (otherX == curX && otherY + 1 == curY) {
                if (relevantWalls[0][0] || relevantWalls[0][1]) {
                    if (jump) //jump diagonally to the left
                    {
                        //check if there are no blocking walls on left side
                        if (relevantWalls[1][0] || relevantWalls[1][3]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] - 1;
                            tempPos[1] = currentPlayer[1] - 1;
                            return true;
                        }
                    } else {
                        //check if there are no blocking walls on right side
                        if (relevantWalls[1][4] || relevantWalls[1][1]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] + 1;
                            tempPos[1] = currentPlayer[1] - 1;
                            return true;
                        }
                    } //elif for jump case

                } //if for far walls
                else if (otherY == 0) {
                    if (jump) {
                        if(relevantWalls[1][3] || relevantWalls[0][2])
                        {
                            return false;
                        }
                        else
                        {
                            tempPos[0] = currentPlayer[0] - 1;
                            tempPos[1] = currentPlayer[1] - 1;
                            return true;
                        }
                    } else {
                        if(relevantWalls[1][4] || relevantWalls[0][5])
                        {
                            return false;
                        }
                        else
                        {
                            tempPos[0] = currentPlayer[0] + 1;
                            tempPos[1] = currentPlayer[1] - 1;
                            return true;
                        }
                    }
                } //if for edge case
                else {
                    tempPos[0] = currentPlayer[0];
                    //tempPos[1] = currentPlayer[1] - 1; //jump over the adjacent player
                    jumpMod = -1;
                }
            }//if for player adjacency

            tempPos[0] = curX;
            tempPos[1] = currentPlayer[1] - 1 + jumpMod; //move player up one space
            return true;

        } catch (ArrayIndexOutOfBoundsException ai) {
            return false;
        }

    }

    /**
     * move pawn down one space
     *
     * @param currentPlayer
     * @param otherPlayer
     * @param jump          true, move left, false move right
     * @return
     */
    public boolean moveDown(int[] currentPlayer, int[] otherPlayer, boolean jump) {
        int curX = currentPlayer[0];
        int curY = currentPlayer[1];
        int otherX = otherPlayer[0];
        int otherY = otherPlayer[1];
        int jumpMod = 0;

        boolean[][] relevantWalls = getRelevantWalls(curX, curY);

        try {
            if (curY == 8) //player is trying to move past bot
            {
                return false;
            }
            //check if there are walls in front
            if (relevantWalls[0][7]) {
                return false;
            }
            if (relevantWalls[0][8]) {
                return false;
            }

            //check if players are adjacent
            if (otherX == curX && otherY - 1 == curY) {
                //check if far walls exist
                if (relevantWalls[0][10] || relevantWalls[0][11]) {
                    if (jump) //jump diagonally to the left
                    {
                        //check if there are no blocking walls on left side
                        if (relevantWalls[1][7] || relevantWalls[1][10]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] - 1;
                            tempPos[1] = currentPlayer[1] + 1;
                            return true;
                        }
                    } else {
                        //check if there are no blocking walls on right side
                        if (relevantWalls[1][8] || relevantWalls[1][11]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] + 1;
                            tempPos[1] = currentPlayer[1] + 1;
                            return true;
                        }
                    } //elif for jump case

                } //if for far walls
                else if (otherY == 8) {
                    if (jump) {
                        if(relevantWalls[1][7] || relevantWalls[1][10]) {
                            return false;
                        }
                        else
                        {
                            tempPos[0] = currentPlayer[0] - 1;
                            tempPos[1] = currentPlayer[1] + 1;
                            return true;
                        }
                    } else {
                        if(relevantWalls[1][8] || relevantWalls[1][11])
                        {
                            return false;
                        }
                        else
                        {
                            tempPos[0] = currentPlayer[0] + 1;
                            tempPos[1] = currentPlayer[1] + 1;
                            return true;
                        }
                    }
                } else {
                    //currentPlayer[1] += 1; //jump over the adjacent player
                    jumpMod = 1;
                }
            }//if for player adjacency


            tempPos[0] = currentPlayer[0];
            tempPos[1] = currentPlayer[1] + 1 + jumpMod; //move player up one space
            return true;

        } catch (ArrayIndexOutOfBoundsException ai) {
            return false;
        }

    }

    /**
     * method to move the players
     *
     * @param currentPlayer
     * @param otherPlayer
     * @param jump
     * @return
     */
    public boolean moveLeft(int[] currentPlayer, int[] otherPlayer, boolean jump) {
        int curX = currentPlayer[0];
        int curY = currentPlayer[1];
        int otherX = otherPlayer[0];
        int otherY = otherPlayer[1];
        int jumpMod = 0;

        boolean[][] relevantWalls = getRelevantWalls(curX, curY);

        try {
            if (curX == 0) //player is trying to move left side off board
            {
                return false;
            }
            //check if there are walls on left side
            //check wall bot left
            if (curX != 0) {
                if (relevantWalls[1][7]) {
                    return false;
                }
                if (curY != 0) {
                    if (relevantWalls[1][3]) {
                        return false;
                    }
                }
            }
            //check if players are adjacent
            if (otherX + 1 == curX && otherY == curY) {
                //check if far walls exist
                if (relevantWalls[1][2] || relevantWalls[1][6]) {
                    if (jump) //jump diagonally to the left
                    {
                        //check if there are no blocking walls above
                        if (relevantWalls[0][2] || relevantWalls[0][3]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] - 1;
                            tempPos[1] = currentPlayer[1] - 1;
                            return true;
                        }
                    } else {
                        //check if there are no blocking walls below
                        if (relevantWalls[0][6] || relevantWalls[0][7]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] - 1;
                            tempPos[1] = currentPlayer[1] + 1;
                            return true;
                        }
                    } //elif for jump case

                } //if for far walls
                else if (otherX == 0) {
                    if (jump) {
                        if(relevantWalls[0][2] || relevantWalls[0][3])
                        {
                            return false;
                        }
                        tempPos[0] = currentPlayer[0] - 1;
                        tempPos[1] = currentPlayer[1] - 1;
                        return true;
                    } else {
                        if(relevantWalls[0][6] || relevantWalls[0][7])
                        {
                            return false;
                        }
                        tempPos[0] = currentPlayer[0] - 1;
                        tempPos[1] = currentPlayer[1] + 1;
                        return true;
                    }
                } //if for right side edge jump
                else {
                    jumpMod = -1;
                    //tempPos[0] -= 1; //jump left over the adjacent player
                    tempPos[1] = currentPlayer[1];
                }
            }//if for player adjacency
            tempPos[0] = currentPlayer[0] - 1 + jumpMod; //move player left one space
            tempPos[1] = currentPlayer[1];
            return true;

        } catch (ArrayIndexOutOfBoundsException ai) {
            return false;
        }
    }

    /**
     * Method to move the current player one space to the right
     *
     * @param currentPlayer
     * @param otherPlayer
     * @param jump          move up if true, move down if false
     * @return
     */
    public boolean moveRight(int[] currentPlayer, int[] otherPlayer, boolean jump) {
        int curX = currentPlayer[0];
        int curY = currentPlayer[1];
        int otherX = otherPlayer[0];
        int otherY = otherPlayer[1];
        int jumpMod = 0;

        boolean[][] relevantWalls = getRelevantWalls(curX, curY);

        try {
            if (curX == 8) //player is trying to move right side off board
            {
                return false;
            }
            //check if there are walls on right side
            if (curY != 0) {
                if (relevantWalls[1][4]) {
                    return false;
                }
                if (relevantWalls[1][8]) {
                    return false;
                }
            } else if (curY == 0) {
                if (relevantWalls[1][8]) {
                    return false;
                }
            }
            //check if players are adjacent
            if (otherX - 1 == curX && otherY == curY) {
                //check if far walls exist
                if (relevantWalls[1][5] || relevantWalls[1][9]) {
                    if (jump) //jump diagonally to the left
                    {
                        //check if there are no blocking walls above
                        if (relevantWalls[0][4] || relevantWalls[0][5]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] + 1;
                            tempPos[1] = currentPlayer[1] - 1;
                            return true;
                        }
                    } else {
                        //check if there are no blocking walls below
                        if (relevantWalls[0][8] || relevantWalls[0][9]) {
                            return false;
                        } else {
                            tempPos[0] = currentPlayer[0] + 1;
                            tempPos[1] = currentPlayer[1] + 1;
                            return true;
                        }
                    } //elif for jump case

                } //if for far walls
                else if (otherX == 8) {
                    if (jump) {
                        if(relevantWalls[0][4] || relevantWalls[0][5])
                        {
                            return false;
                        }
                        tempPos[0] = currentPlayer[0] + 1;
                        tempPos[1] = currentPlayer[1] - 1;
                        return true;
                    } else {
                        if(relevantWalls[0][7] || relevantWalls[0][9])
                        {
                            return false;
                        }
                        tempPos[0] = currentPlayer[0] + 1;
                        tempPos[1] = currentPlayer[1] + 1;
                        return true;
                    }
                } else {
                    //tempPos[0] += 1; //jump left over the adjacent player
                    jumpMod = 1;
                    tempPos[1] = currentPlayer[1];
                }
            }//if for player adjacency


            tempPos[0] = currentPlayer[0] + 1 + jumpMod; //move player left one space
            tempPos[1] = currentPlayer[1];
            return true;

        } catch (ArrayIndexOutOfBoundsException ai) {
            return false;
        }

    }

    /**
     * Starts new game
     *
     * @return true
     */
    public boolean newGame() {
        init();
        return true;
    }

    /**
     * Finalize temp values to reflect actual board changes.
     *
     * @return true. Always.
     */
    public boolean finalizeTurn() {
        //return if haven't moved or placed wall
        if (!hasMoved)
            return false;
        //check for path if wall was placed
        if (wallDown && !pathForAll()) {
            undo();
            for (int a = 0; a < 8; a++) {
                for (int b = 0; b < 8; b++) {
                    tempQuo.horzWalls[a][b] = false;
                    tempQuo.vertWalls[a][b] = false;
                }
            }
            return false;
        }

        //check who's turn it is and update their values
        if (turn == 0) {
            p1Pos[0] = tempPos[0];
            p1Pos[1] = tempPos[1];
            p1RemainingWalls = tempRemWalls;
        } else {
            p2Pos[0] = tempPos[0];
            p2Pos[1] = tempPos[1];
            p2RemainingWalls = tempRemWalls;
        }

        //update walls on board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (tempHWalls[i][j]) {
                    horzWalls[i][j] = tempHWalls[i][j];
                    tempHWalls[i][j] = false;
                }
                if (tempVWalls[i][j]) {
                    vertWalls[i][j] = tempVWalls[i][j];
                    tempVWalls[i][j] = false;
                }
            }
        }

        turn = 1 - turn;
        //set tempPos to prevent accidentally setting position on wall placement
        if (turn == 0) {
            tempPos[0] = p1Pos[0];
            tempPos[1] = p1Pos[1];
            tempRemWalls = p1RemainingWalls;
        } else {
            tempPos[0] = p2Pos[0];
            tempPos[1] = p2Pos[1];
            tempRemWalls = p2RemainingWalls;
        }
        wallDown = false;
        hasMoved = false;
        return true;
    }

    /**
     * Resets the board to the beginning of the turn.
     *
     * @return true?
     */
    public boolean undo() {
        //check who's turn it is and reset their values
        if (turn == 0) {
            tempPos[0] = p1Pos[0];
            tempPos[1] = p1Pos[1];
            if (wallDown)
                p1RemainingWalls++;
            tempRemWalls = p1RemainingWalls;
        } else {
            tempPos[0] = p2Pos[0];
            tempPos[1] = p2Pos[1];
            if (wallDown)
                p2RemainingWalls++;
            tempRemWalls = p2RemainingWalls;
        }

        //update walls on board
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tempHWalls[i][j] = false;
                tempVWalls[i][j] = false;
            }
        }
        hasMoved = false;
        wallDown = false;
        return true;
    }

    /**
     * placeWall
     * Checks for player turn
     * Calls borderPlaceCheck method to check border and placeable spots
     * Returns false if method call returns false
     */
    public boolean placeWall(int player, int x, int y) {
        if (hasMoved) {
            return false;
        }
        //checks for player turn, returns false if not turn
        if (player != turn)
            return false;
        //check for out of bounds, happens only with AI
        if (x < 0 || y < 0 || x >= 8 || y >= 8)
            return false;
        if (!wallDown) {
            //check bounds by calling method
            if (borderPlaceCheck(x, y)) {
                wallDown = true;
                //set temp wall variable to respective player's remaining walls
                if (player == 0)
                    p1RemainingWalls = tempRemWalls;
                else
                    p2RemainingWalls = tempRemWalls;
                hasMoved = true;
                return true;
            } else
                return false;
        } else
            return false;
    }

    /**
     * borderPlaceCheck
     * Method does border error checking
     * Checks to make sure given wall can be placed in x, y spot clicked by player
     *
     * @param x coordinate of wall
     * @param y coordinate of wall
     */
    private boolean borderPlaceCheck(int x, int y) {
        if (turn == 0) // player 1
            tempRemWalls = p1RemainingWalls;
        else
            tempRemWalls = p2RemainingWalls;
        if (tempRemWalls == 0) //if player has no walls, returns
            return false;
        if (!horzWalls[x][y] && !vertWalls[x][y]) // default to horzWall place first
        {
            //checks if x,y coordinate clicked is 0,0
            //checks respective wall locations based on placement
            if (x == 0 && y == 0) {
                if (horzWalls[x + 1][y] && !vertWalls[x][y + 1]) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if (vertWalls[x][y + 1] && !horzWalls[x + 1][y]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case - no walls surrounding so defaults to horizontal wall
                else if (!horzWalls[x + 1][y] && !vertWalls[x][y + 1]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if x,y coordinate clicked is 0,8
            //checks respective wall locations based on placement
            else if (x == 0 && y == 7) {
                if ((horzWalls[x + 1][y]) && (!vertWalls[x][y - 1])) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if (vertWalls[x][y - 1] && !horzWalls[x + 1][y]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case - no walls surrounding so defaults to horizontal wall
                else if (!horzWalls[x + 1][y] && !vertWalls[x][y - 1]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if x,y coordinate clicked is 8,0
            else if (x == 7 && y == 0) {
                if (horzWalls[x - 1][y] && !vertWalls[x][y + 1]) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if (vertWalls[x][y + 1] && !horzWalls[x - 1][y]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case
                else if (!horzWalls[x - 1][y] && !vertWalls[x][y + 1]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if x,y coordinate clicked is 8,8
            else if (x == 7 && y == 7) {
                if (horzWalls[x - 1][y] && !vertWalls[x][y - 1]) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if (vertWalls[x][y - 1] && !horzWalls[x - 1][y]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case
                else if (!horzWalls[x - 1][y] && !vertWalls[x][y - 1]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if x coordinate clicked is 0, can't check for x-1 spot or OOB error would be thrown
            else if (x == 0) {
                if (horzWalls[x + 1][y] && (!vertWalls[x][y - 1] || !vertWalls[x][y + 1])) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if ((vertWalls[x][y - 1] || vertWalls[x][y + 1]) && !horzWalls[x + 1][y]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case
                else if (!horzWalls[x + 1][y] && (!vertWalls[x][y - 1] && !vertWalls[x][y + 1])) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if x coordinate clicked is 8, can't check for x+1 spot or OOB error would be thrown
            else if (x == 7) {
                if (horzWalls[x - 1][y] && (!vertWalls[x][y - 1] || !vertWalls[x][y + 1])) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if ((vertWalls[x][y - 1] || vertWalls[x][y + 1]) && !horzWalls[x - 1][y]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case
                else if (!horzWalls[x - 1][y] && (!vertWalls[x][y - 1] && !vertWalls[x][y + 1])) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if y coordinate clicked is 0, can't check for y-1 spot or OOB error would be thrown
            else if (y == 0) {
                if ((horzWalls[x - 1][y] || horzWalls[x + 1][y]) && !vertWalls[x][y + 1]) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if (vertWalls[x][y + 1] && (!horzWalls[x - 1][y] || !horzWalls[x + 1][y])) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case
                else if ((!horzWalls[x - 1][y] && !horzWalls[x + 1][y]) && !vertWalls[x][y + 1]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //checks if y coordinate clicked is 0, can't check for y+1 spot or OOB error would be thrown
            else if (y == 7) {
                if (((horzWalls[x - 1][y]) || (horzWalls[x + 1][y])) && (!vertWalls[x][y - 1])) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if (tempVWalls[x][y - 1] && ((!horzWalls[x - 1][y]) || (!horzWalls[x + 1][y]))) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case
                else if ((!horzWalls[x - 1][y] && !horzWalls[x + 1][y]) && !vertWalls[x][y - 1]) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
            //Default Case - x or y are not on the borders so have to check all 4 spots to make sure wall can be placed
            else {
                //check if spot is available (horz and vert)
                if ((horzWalls[x - 1][y] || horzWalls[x + 1][y]) && (!vertWalls[x][y - 1] || !vertWalls[x][y + 1])) {
                    tempVWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else if ((vertWalls[x][y - 1] || vertWalls[x][y + 1]) && (!horzWalls[x - 1][y] || !horzWalls[x + 1][y])) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                }
                // Default Case - Horizontal wall is placed
                else if ((!horzWalls[x - 1][y] && !horzWalls[x + 1][y]) && (!vertWalls[x][y - 1] && !vertWalls[x][y + 1])) {
                    tempHWalls[x][y] = true;
                    tempRemWalls--;
                    return true;
                } else
                    return false;
            }
        } else
            return false;
    }

    /**
     * rotateWall
     * Checks for player turn
     * Calls borderRotateCheck method to see if rotate is valid
     *
     * @param player current player
     * @param x coordinate of wall
     * @param y coordinate of wall
     *
     */
    public boolean rotateWall(int player, int x, int y) {
        //checks for player turn, returns false if not turn
        if (player != turn)
            return false;

        return borderRotateCheck(x, y);
    }

    /**
     * borderRotateCheck
     * Method does border error checking
     * Makes sure wall trying to be rotated is a valid move
     *
     * @param x coordinate of wall
     * @param y coordinate of wall
     */
    private boolean borderRotateCheck(int x, int y) {
        //checks if x,y coordinate clicked is 0,0
        if (x == 0 && y == 0) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y + 1]) {
                    return false;
                }
                //Default Case - move is valid, switch hor wall to vert wall
                else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x + 1][y]) {
                    return false;
                }
                //Default Case - move is valid, switch vert wall to hor wall
                else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if x,y coordinate clicked is 0,7
        else if (x == 0 && y == 7) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y - 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x + 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if x,y coordinate clicked is 7,0
        else if (x == 7 && y == 0) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y + 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x - 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if x,y coordinate clicked is 7,7
        else if (x == 7 && y == 7) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y - 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x - 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if x coordinate clicked is 0
        else if (x == 0) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y - 1] || vertWalls[x][y + 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x + 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if x coordinate clicked is 7
        else if (x == 7) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y - 1] || vertWalls[x][y + 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x - 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if y coordinate clicked is 0
        else if (y == 0) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y + 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x - 1][y] || horzWalls[x + 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //checks if y coordinate clicked is 7
        else if (y == 7) {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y - 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x - 1][y] || horzWalls[x + 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
        //Default Case - x or y are not on the borders
        //Have to check for both spots depending on orientation of original wall
        //to make sure wall rotation is valid
        else {
            if (tempHWalls[x][y]) {
                if (vertWalls[x][y - 1] || vertWalls[x][y + 1]) {
                    return false;
                } else {
                    tempVWalls[x][y] = true;
                    tempHWalls[x][y] = false;
                    return true;
                }
            } else if (tempVWalls[x][y]) {
                if (horzWalls[x - 1][y] || horzWalls[x + 1][y]) {
                    return false;
                } else {
                    tempHWalls[x][y] = true;
                    tempVWalls[x][y] = false;
                    return true;
                }
            } else
                return false;
        }
    }

    /**
     * initChecker
     * Initializes visitedSpot array which is used when
     * pawn visits square when checking for winnable path
     *
     * @return 9x9 array of all false values
     */
    private boolean[][] initChecker() {
        tempQuo = new QuoridorGameState(this);
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                visitedSpot[i][j] = false;
            }
        }
        for (int k = 0; k < 8; k++) {
            for (int l = 0; l < 8; l++) {
                if (this.tempHWalls[k][l]) {
                    tempQuo.horzWalls[k][l] = true;
                }
                if (this.tempVWalls[k][l]) {
                    tempQuo.vertWalls[k][l] = true;
                }
            }
        }
        return visitedSpot;
    }

    /**
     * pathForAll
     * Calls pathCheck method for both players.
     *
     * @return true if all players can win
     */
    private boolean pathForAll() {
        int p1Pos[] = getPlayerPos(0);
        int p2Pos[] = getPlayerPos(1);

        pathCheck(0, p1Pos[0], p1Pos[1]); //call pathCheck on Player 1
        for (int i = 0; i < 9; i++)  {
            // if any end spot was reached, any column in row 8 for P1
            if (visitedSpot[i][8] == true) {
                initCheck = false; //boolean to reset visited spots array
                pathCheck(1, p2Pos[0], p2Pos[1]); // call pathCheck on Player 2
                for (int j = 0; j < 9; j++) {
                    // if any end spot was reached, any column in row 0 for P2
                    if (visitedSpot[j][0]) {
                        initCheck = false;
                        return true; //game is still winnable, wall is able to be placed
                    }
                }
            }
        }
        initCheck = false;
        return false;
    }

    /**
     * pathCheck
     *
     * Checks to make sure the game is still winnable if
     * wall placement happens
     *
     * @param player whose path is being checked
     * @param x      coordinate of square
     * @param y      coordinate of square
     * @return true if path is still winnable
     */
    private void pathCheck(int player, int x, int y) {
        if (!initCheck) {
            initChecker(); //initializes visitedSpot array to false values
            initCheck = true;
        }
        //uses copy of gamestate values so original state isn't changed
        int[] currentPlayer = tempQuo.getPlayerPos(player);
        int[] oppPlayer = tempQuo.getPlayerPos((player + 1) % 2);
        boolean canLeft = tempQuo.moveLeft(currentPlayer, oppPlayer, false) ||
                tempQuo.moveLeft(currentPlayer, oppPlayer, true); //is player able to move LEFT?
        boolean canRight = tempQuo.moveRight(currentPlayer, oppPlayer, false) ||
                tempQuo.moveRight(currentPlayer, oppPlayer, true); //is player able to move RIGHT?
        boolean canUp = tempQuo.moveUp(currentPlayer, oppPlayer, false) ||
                tempQuo.moveUp(currentPlayer, oppPlayer, true); //is player able to move UP?
        boolean canDown = tempQuo.moveDown(currentPlayer, oppPlayer, false) ||
                tempQuo.moveDown(currentPlayer, oppPlayer, true); //is player able to move DOWN?

        //check for out of bounds values, edge cases
        if ((x < 0 || x >= 9) || (y < 0 || y >= 9)) {
            return;
        }
        //was square visited
        if (visitedSpot[x][y]) {
            return;
        }
        //sqaure got visited so set to true
        visitedSpot[x][y] = true;

        //check which player, DOWN is checked before UP case if Player 1
        if (player == 0) {
            if (y == 8) { //goal reached
                return;
            }
            //check LEFT and if spot is visited, recurse if accessible
            if (canLeft && x != 0 && !visitedSpot[x - 1][y]) {
                tempQuo.setPlayerPos(x - 1, y, player);
                pathCheck(player, x - 1, y);
            }
            //check RIGHT and if spot is visited, recurse if accessible
            if (canRight && x != 8 && !visitedSpot[x + 1][y]) {
                tempQuo.setPlayerPos(x + 1, y, player);
                pathCheck(player, x + 1, y);
            }
            //check DOWN and if spot is visited, recurse if accessible
            if (canDown && y != 8 && !visitedSpot[x][y + 1]) {
                tempQuo.setPlayerPos(x, y + 1, player);
                pathCheck(player, x, y + 1);
            }
            //check UP and if spot is visited, recurse if accessible
            if (canUp && y != 0 && !visitedSpot[x][y - 1]) {
                tempQuo.setPlayerPos(x, y - 1, player);
                pathCheck(player, x, y - 1);
            }
        }
        //check which player, UP is checked before DOWN case if Player 2
        if (player == 1) {
            if (y == 0) { //goal reached
                return;
            }
            //check LEFT and if spot is visited, recurse if accessible
            if (canLeft && x != 0 && !visitedSpot[x - 1][y]) {
                tempQuo.setPlayerPos(x - 1, y, player);
                pathCheck(player, x - 1, y);
            }
            //check RIGHT and if spot is visited, recurse if accessible
            if (canRight && x != 8 && !visitedSpot[x + 1][y]) {
                tempQuo.setPlayerPos(x + 1, y, player);
                pathCheck(player, x + 1, y);
            }
            //check UP and if spot is visited, recurse if accessible
            if (canUp && y != 0 && !visitedSpot[x][y - 1]) {
                tempQuo.setPlayerPos(x, y - 1, player);
                pathCheck(player, x, y - 1);
            }
            //check DOWN and if spot is visited, recurse if accessible
            if (canDown && y != 8 && !visitedSpot[x][y + 1]) {
                tempQuo.setPlayerPos(x, y + 1, player);
                pathCheck(player, x, y + 1);
            }
        }
        return;
    }

}