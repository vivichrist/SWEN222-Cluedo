package ui;

import game.Place;
import game.Player;
import game.Room;
import game.Square;
import identities.Cards;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.swing.JComponent;


@SuppressWarnings( "serial" )
public class CluedoBoard extends JComponent
{
	private char[][] tiles;
	private List<Point> startSquares = new LinkedList<Point>();
	int height, width;
	private List<Point> players = new LinkedList<Point>();
	private List<Point> centerArea = new LinkedList<Point>();
	HashMap<Character, LinkedList<Point>> roomTiles = new HashMap<Character, LinkedList<Point>>();
	HashMap<Character, Color> colors = new HashMap<Character, Color>();

	public CluedoBoard( char[][] board, int width, int height )
	{
		tiles = board;
		int i = 0, j = 0;
		char c;
		Room[] rooms = new Room[9];
		List<Point> squares = new LinkedList<Point>();
		for( j = 0; j < height; ++j )
		{
			for( i = 0; i < width; ++i )
			{
				c = board[i][j];
				if ( isRoom( c ) )
				{	// rooms
					LinkedList<Point> points = roomTiles.get( c );
					if ( points == null )
						roomTiles.put( c, new LinkedList<Point>( Arrays.asList( new Point( i, j ) ) ) );
					else
					{
						points.add( new Point( i, j ) );
						roomTiles.put( c, points );
					}
				} else if ( c == 'P' || c == 'd' || c == 'D' )
				{
					char ch = findRoomType( i, j );
					LinkedList<Point> points = roomTiles.get( ch );
					if ( points == null )
						roomTiles.put( ch, new LinkedList<Point>( Arrays.asList( new Point( i, j ) ) ) );
					else
					{
						points.add( new Point( i, j ) );
						roomTiles.put( ch, points );
					}
				} else if ( c == 'X' )
				{
					squares.add( new Point( i, j ) );
					startSquares.add( new Point( i, j ) );
				} else if ( c == 'S' )
					squares.add( new Point( i, j ) );
				else if ( c == '?' )
					centerArea.add( new Point( i, j ) );
				else throw new RuntimeException( "Map contains a wrong char (" + i + ", " + j + ")");
			}
		}
		// create and connect places
	}
	private boolean isRoom( char c )
	{
		switch ( c )
		{
		case 'K' :
		case 'B' :
		case 'A' :
		case 'C' :
		case 'L' :
		case 'H' :
		case 'U' :
		case 'N' :
		case 'I' :
			return true;
		}
		return false;
	}
	private char findRoomType( int x, int y )
	{
		if ( x >= 0 && tiles[x-1][y] != 'S' && tiles[x-1][y] != 'W' )
			return tiles[x-1][y];
		return tiles[x+1][y];
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
