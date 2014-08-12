package ui;

import game.Place;
import game.Player;

import identities.Cards;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;


@SuppressWarnings( "serial" )
public class CluedoBoard extends JComponent
{
	char[][] tiles;
	int height, width;
	List<Cards> players = new LinkedList<Cards>();
	HashMap<String, Place> places = new HashMap<String, Place>();

	public CluedoBoard( char[][] board )
	{

		tiles = board;
	}
	public void start()
	{

	}
	public void startTurn()
	{

	}
	public void endTurn()
	{

	}
	@Override
	protected void paintComponent( Graphics g )
	{
		super.paintComponent( g );

	}


}
