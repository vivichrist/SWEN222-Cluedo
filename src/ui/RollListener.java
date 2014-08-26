

package ui;

import identities.Cards;

import java.util.List;

/**
 * @author Vivian Stewart
 * Interface to update the left panel of new dice rolls and a players
 * turn and their cards to be displayed.
 */
public interface RollListener
{
	/**
	 * Indicate to listening class a new dice roll
	 * @param die1
	 * @param die2
	 */
	public void diceRool( int die1, int die2);
	/**
	 * Update the displayed current player information.
	 * @param cards : the cards held by the current player
	 * @param player : the current players identity
	 */
	public void cards( List<Cards> cards, Cards player );
}
