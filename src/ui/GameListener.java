/* GameListener is just used to update the CluedoBoard class of player movements
 * and setup the GameBoard upon the start of a game.
 */

package ui;

import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import game.PlayerListener;
import game.Place;
import game.Player;
import identities.Cards;

public interface GameListener
{
	
	/**
	 * @param shapes : list of highlights to be drawn to indicate 
	 * where a player can move to
	 */
	public void showPossibleMoves( List<Shape> shapes);
	/**
	 * @param player : id that the board can find to update location
	 * @param location : new position of player
	 */
	public void playerMoved( Cards player, Place location );
	/* Called when start is selected from the menu.
	 * @param players: to be added at start position.
	 * @param game: a means of communicating mouse clicks from the player
	 * @param splits: dealt cards from the deck after the solution cards
	 * @return a list of created players completely initialised
	 */
	public ArrayList<Player> initPlayers( List<Cards> players
			, PlayerListener game, LinkedList<LinkedList<Cards>> splits );
}
