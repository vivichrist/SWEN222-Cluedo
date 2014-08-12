package game;
import identities.Cards;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.event.MouseInputListener;


public class Player implements KeyListener, MouseInputListener
{
	// when player makes a false accusation 'playing' becomes false
	// and the player is then responsible for showing cards only
	private boolean playing = true;
	// individually dealt cards that exclude the solution
	private LinkedList<Cards> cards;
	private Place location;

	public Player( Place p, List<Cards> cards )
	{
		location = p;
		this.cards = new LinkedList<Cards>(cards);
	}
	@Override
	public void keyTyped( KeyEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed( KeyEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased( KeyEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved( MouseEvent e )
	{
		// TODO Auto-generated method stub

	}

}
