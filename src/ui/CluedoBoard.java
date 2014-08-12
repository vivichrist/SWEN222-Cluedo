package ui;

import game.Place;
import game.Player;
import identities.Cards;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;


@SuppressWarnings( "serial" )
public class CluedoBoard extends JComponent
{
	private char[][] tiles;
	private Place[] startSquares = new Place[6];
	int height, width;
	List<Point> players = new LinkedList<Point>();
	HashMap<Character, LinkedList<Point>> roomTiles = new HashMap<Character, LinkedList<Point>>();
	HashMap<Character, Color> colors = new HashMap<Character, Color>();

	public CluedoBoard( char[][] board )
	{
		int i = 0, j = 0;
		Room[] rooms = new Room[9];
		for( char[] ca: tiles )
		{
			for( char c: ca )
			{
				switch ( c )
				{
				case 'K' : // rooms
				case 'B' :
				case 'A' :
				case 'C' :
				case 'L' :
				case 'H' :
				case 'U' :
				case 'N' :
				case 'I' :
				{
					LinkedList<Point> points = roomTiles.get( c );
					if ( points == null )
						roomTiles.put( c, new LinkedList<Point>( Arrays.asList( new Point( i, j ) ) ) );
					else
					{
						points.add( new Point( i, j ) );
						roomTiles.put( c, points );
					}
					
					break; // end rooms
				}
				case 'P' : // passage
				case 'd' : // doors are
				case 'D' :

				case 'X' : // squares
				case 'S' :
				case '?' :
				}
				++i;
			}
			i = 0;
			++j;
		}
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
