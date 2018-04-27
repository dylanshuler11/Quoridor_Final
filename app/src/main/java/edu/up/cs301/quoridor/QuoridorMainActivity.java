package edu.up.cs301.quoridor;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import edu.up.cs301.game.GameMainActivity;
import edu.up.cs301.game.GamePlayer;
import edu.up.cs301.game.LocalGame;
import edu.up.cs301.game.R;
import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import edu.up.cs301.quoridor.QuoridorComputerPlayer;
import edu.up.cs301.quoridor.QuoridorHumanPlayer;

/**
 * Created by lieu18 on 3/25/2018.
 *
 * @author Anthony Lieu
 * @author Phillip Manalili
 * @author Noah Davis
 * @author Dylan Shuler
 *
 *
 * Final Game Completion:
 *      Game is fully functional with pawn jumps and path checker.
 *      All game requirments were met.
 *      All bugs reported on Bugzilla were address and resolved.
 *      Currently no known bugs with the game as of this submission.
 *      All Actions were completed and met.
 *      Complex Algorithms are implemented into the game.
 *      Network play works between tablets.
 *
 * Quick How to Play:
 *      In general: two pawns start at opposite ends of a 9x9 board.
 *      The objective is to reach the other side of the board before your opponent.
 *      Each pawn is allocated 10 walls that they may place on the board.
 *      Pawn can either place a wall or move one space during thier turn.
 *      To move pawn press on available square to move pawn.
 *      Pawns move to only valid spots.
 *      To place wall, players must press at the intersections of the board.
 *      Walls can be rotated by pressing the same intersection.
 *      Only newly placed walls can be rotated.
 *      Walls cannot overlap.
 *      Human player is the red dot (top)
 *
 *      For network play:
 *      Same rules apply
 *      Active player is the red dot.
 *
 * Enhancements:
 * Buttons behave properly and add to the functionality of the game.
 *      New Game: Starts a new game when pressed
 *      Finalize: Ends turn. Turn can only be ended when a valid move occurs. Players cannot skip turns.
 *                Finalized moves cannot be undone.
 *      Undo: Resets the current action by player before finalize turn.
 *
 * Issues beyond our control:
 *      Noah's tablet could not support network play. (Prompted "Could not find game on server")
 *      Issue was not seen by other tablets of this team.
 *      Noah's Tablet ID: 20273.
 */

public class QuoridorMainActivity extends GameMainActivity {

    public static final int PORT_NUMBER = 10069;

    @Override
    public GameConfig createDefaultConfig() {

        // Define the allowed player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        // yellow-on-blue GUI
        playerTypes.add(new GamePlayerType("Human Player") {
            public GamePlayer createPlayer(String name) {
                return new QuoridorHumanPlayer(name, R.layout.quoridor_main);
            }
        });

        // dumb computer player
        playerTypes.add(new GamePlayerType("Computer Player (Random)") {
            public GamePlayer createPlayer(String name) {
                return new QuoridorComputerPlayer(name);
            }
        });

        playerTypes.add(new GamePlayerType("Computer Player (Smart)") {
            public GamePlayer createPlayer(String name) {
                return new QuoridorSmartComputerPlayer(name);
            }
        });


        // Create a game configuration class for Quoridor
        GameConfig defaultConfig = new GameConfig(playerTypes, 2,2, "Quoridor", PORT_NUMBER);

        // Add the default players
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Computer", 1); // dumb computer player

        // Set the initial information for the remote player
        defaultConfig.setRemoteData("Remote Player", "", 0);

        //done!
        return defaultConfig;

    }

    @Override
    public LocalGame createLocalGame() {
        return new QuoridorLocalGame();
    }
}
