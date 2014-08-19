

package ui;

import identities.Cards;

import java.util.List;

/**
 * @author Vivian Stewart
 * Interface to update the panel of new dice rolls and a players
 * turn and their cards to be displayed.
 */
public interface RollListener
{
	public void message( int die1, int die2);
	public void cards( List<Cards> cards, Cards player );
}
