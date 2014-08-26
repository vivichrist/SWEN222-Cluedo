package game;
import identities.Cards;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

import ui.GameListener;


public class Player implements MouseListener
{
	/**
	 * when player makes a false accusation 'playing' becomes false
	 * and the player is then responsible for showing cards only
	 */
	private boolean playing = true;
	private JMenu callback = null;
	private boolean active = false;
	/**
	 * individually dealt cards that exclude the solution.
	 * game requires this players cards are do not include
	 * other players cards nor the solution cards
	 */
	private LinkedList<Cards> cards;
	private GameListener moveUpdate;
	private Place location;
	private Cards id;
	// private List<Place> places = null;
	private PlayerListener gameUpdate;
	
	public Player( Cards c, Place p, List<Cards> cards, GameListener moveUpdate, PlayerListener game )
	{
		id = c;
		location = p;
		this.cards = new LinkedList<Cards>(cards);
		for ( Cards cd: cards )
			cd.addVisibility( this.id );
		this.moveUpdate = moveUpdate;
		this.gameUpdate = game;
	}

	public String id()
	{
		return id.name();
	}

	public Cards cardID()
	{
		return id;
	}

	public boolean isActive()
	{
		return active;
	}

	public boolean isPlaying()
	{
		return playing;
	}

	public void setActive( boolean active )
	{
		this.active = active;
	}

	public void setPlaying( boolean playing )
	{
		this.playing = playing;
	}

	public Place getLocation()
	{
		return location;
	}

	public boolean isInARoom()
	{
		return location instanceof Room;
	}
	public boolean roomHasPassage()
	{
		if ( location instanceof Room )
			return isInARoom() && ((Room)location).hasPassage();
		return false;
	}

	/**
	 * Sets up a preliminary display of places on the board that a player can
	 * move to after selecting dice roll and move within their turn.
	 *  
	 * @param moves : the dice roll of number of places in sequence a player
	 * can move.
	 * @param menu : the menu to be stored and returned when the player selects
	 * a place to move to.
	 * @return : all places that the player can move to.
	 */
	public List<Place> canMove( int moves, JMenu menu )
	{
		callback = menu;
		List<Place> places = location.mark( moves, true );
		List<Shape> areas = new LinkedList<Shape>();
		for ( Place p: places )
			areas.add( p.getArea() );
		moveUpdate.showPossibleMoves( areas );
		location.unmark( moves );
		return places;
	}

	/**
	 * Traverses the player one place toward the destination and updates the
	 * board to show animation between start and destination.
	 * 
	 * @param place : the desired destination of the player
	 * @return : true if destination is not reached.
	 */
	public boolean moveMe( Place place )
	{
		if ( place == location ) return false;
		location = location.movePlayer( place );
		moveUpdate.playerMoved( id, location );
		try
		{
			Thread.sleep(250);
		} catch ( InterruptedException e )
		{
			e.printStackTrace();
		}
		return true;
	}
	
	public List<Cards> playerCards()
	{
		return new LinkedList<Cards>( cards );
	}

	public boolean haveCard( Cards card )
	{
		return cards.contains(card);
	}

	@Override
	public void mouseClicked( MouseEvent e )
	{
		if ( active && playing )
		{
			gameUpdate.clickedOption( e.getX(), e.getY(), callback );
		}
	}

	@Override
	public void mousePressed( MouseEvent e ){}
	@Override
	public void mouseReleased( MouseEvent e ){}
	@Override
	public void mouseEntered( MouseEvent e ){}
	@Override
	public void mouseExited( MouseEvent e ){}
}
