package edu.up.cs301.quoridor;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by shule on 4/26/2018.
 */
public class QuoridorGameStateTest {


    @Test
    public void movePawn() throws Exception {
        QuoridorGameState qgs = new QuoridorGameState();
        assertEquals(true, qgs.movePawn(0,Direction.DOWN,false));
        assertEquals(false, qgs.movePawn(0,Direction.UP,false));
        assertEquals(true, qgs.movePawn(0,Direction.RIGHT,false));
        assertEquals(true, qgs.movePawn(0,Direction.LEFT,false));
    }

    @Test
    public void placeWall() throws Exception {
        QuoridorGameState qgs = new QuoridorGameState();
        assertEquals(true, qgs.placeWall(0,4,4));
        assertEquals(false, qgs.placeWall(0,-4,4));
    }

    @Test
    public void rotateWall() throws Exception {
        QuoridorGameState qgs = new QuoridorGameState();
        qgs.placeWall(0,4,4);
        assertEquals(true, qgs.rotateWall(0,4,4));
        assertEquals(false, qgs.rotateWall(0,3,4));
    }

}