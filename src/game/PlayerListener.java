package game;

import javax.swing.JMenu;

/**
 * @author Vivian Stewart
 * This Interface lessens coupling between player and the game logic.
 * Allowing Player to be responsible for its own movement and a way of
 * manipulating the menu later. This could be better though...
 */
public interface PlayerListener
{
	/**
	 * @param x : mouse click coordinate relative to the board canvas  
	 * @param y : "
	 * @param menu : to finally display options after the player piece moves
	 */
	public void clickedOption( int x, int y, JMenu menu );
}
