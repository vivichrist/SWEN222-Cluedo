package ui;

import identities.Cards;

import java.util.List;

public interface RollListener
{
	public void message( int die1, int die2);
	public void cards( List<Cards> cards, Cards player );
}
