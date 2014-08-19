package game;
import identities.Cards;

import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;

import ui.PlayerListener;


public class Player implements MouseListener
{
	// when player makes a false accusation 'playing' becomes false
	// and the player is then responsible for showing cards only
	private boolean playing = true;
	private JMenu callback = null;
	public boolean isPlaying()
	{
		return playing;
	}

	public void setPlaying( boolean playing )
	{
		this.playing = playing;
	}

	private boolean active = false;
	// individually dealt cards that exclude the solution
	private LinkedList<Cards> cards;
	private PlayerListener moveUpdate;
	private Place location;
	private Cards id;
	// private List<Place> places = null;
	private GameListener gameUpdate;

	public Player( Cards c, Place p, List<Cards> cards, PlayerListener moveUpdate, GameListener game )
	{
		id = c;
		location = p;
		this.cards = new LinkedList<Cards>(cards);
		for ( Cards cd: cards )
			cd.addVisibility(this);
		this.moveUpdate = moveUpdate;
		this.gameUpdate = game;
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

	public List<Place> canMove( int moves, JMenu menu )
	{
		callback = menu;
		List<Place> places = location.mark( moves, true );
		List<Shape> areas = new LinkedList<Shape>();
		for ( Place p: places )
			areas.add( p.getArea() );
		moveUpdate.showPossibleMoves( areas );
		return places;
	}

	public boolean moveMe( Place place )
	{
		if ( place == location ) return false;
		location = location.movePlayer( place );
		moveUpdate.playerMoved( id, location );
		try
		{
			Thread.sleep(500);
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
	public boolean addCard( Cards card )
	{
		card.addVisibility(this);
		return cards.add(card);
	}
	public String id()
	{
		return id.name();
	}

	public Cards cardID()
	{
		return id;
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

	public boolean isActive()
	{
		return active;
	}

	public void setActive( boolean active )
	{
		this.active = active;
	}
}
