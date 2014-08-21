/* The CluedoBoard Class manages a grid of characters that represent the tiles
 * on a Cluedo game board. This Class is responsible for Displaying/Updating 
 * it's graphics context in reaction to PLayer and Weapon movements and through
 * GameListener interface. Mostly dealing with points, cards and characters
 * as symbolic and location references. Also responsible for creating the 
 * corresponding data representation of the game which only has callback
 * interactions with this class.
 */

package ui;

import game.Cluedo;
import game.PlayerListener;
import game.Place;
import game.Player;
import game.Room;
import game.Square;
import identities.Cards;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JMenuItem;

@SuppressWarnings( "serial" )
public class CluedoBoard extends JComponent implements GameListener
{
	private char[][]								tiles;
	private int										wallthickness	= 5;
	private List<Point>								startSquares	= new LinkedList<Point>();
	private int										tileW, tileH, height,
			width;
	private HashMap<Cards, Character>				weaponRooms		= new HashMap<Cards, Character>();
	private HashMap<Cards, Point>					playerPos		= new HashMap<Cards, Point>();
	private HashMap<Character, Point>				roomCenters		= new HashMap<Character, Point>();
	private List<Point>								centerArea		= new LinkedList<Point>();
	private HashMap<Character, LinkedList<Point>>	roomTiles		= new HashMap<Character, LinkedList<Point>>();
	private List<Point>								squares			= new LinkedList<Point>();
	private List<Shape>								highlights		= null;
	private List<Square> 							starts			= new LinkedList<Square>(); // only needed for Cluedo class
	private HashMap<Cards, Color>					colors			= new HashMap<Cards, Color>();
	private AffineTransform							transform;

	public CluedoBoard( char[][] board, RollListener rolls, List<JMenuItem> mi )
	{
		tiles = board;
		this.height = board[ 0 ].length;
		this.width = board.length;
		System.out.println( "width:" + width + " height:" + height );
		tileH = 25;
		tileW = 25;
		transform = AffineTransform.getScaleInstance( (tileW - 1) / 200.0f,
				(tileH - 1) / 200.0f );
		transform
				.concatenate( AffineTransform.getTranslateInstance( 200, 200 ) );
		transform.concatenate( AffineTransform.getRotateInstance( Math.PI ) );
		int i = 0, j = 0;
		char c;
		HashMap<Character, LinkedList<Point>> doors = new HashMap<Character, LinkedList<Point>>();
		for ( j = 0; j < height; ++j )
		{
			for ( i = 0; i < width; ++i )
			{
				c = board[ i ][ j ];
				if ( isRoom( c ) )
				{ // rooms
					addToRoom( c, i, j, roomTiles );
				} else if ( isInRoom( c ) )
				{
					char ch = findRoomType( i, j );
					addToRoom( ch, i, j, roomTiles );
					if ( c == 'd' )
					{
						char up = j - 1 > 0 ? board[ i ][ j - 1 ]
								: board[ i ][ j ];
						char down = j + 1 < height ? board[ i ][ j + 1 ]
								: board[ i ][ j ];
						if ( up == 'S' )
							addToRoom( ch, i, j - 1, doors );
						if ( down == 'S' )
							addToRoom( ch, i, j + 1, doors );
						// System.out.println( "Door At: " + i + ":" + j + " " );
					} else if ( c == 'D' )
					{
						char left = i - 1 > 0 ? board[ i - 1 ][ j ]
								: board[ i ][ j ];
						char right = j + 1 < width ? board[ i + 1 ][ j ]
								: board[ i ][ j ];
						if ( left == 'S' )
							addToRoom( ch, i - 1, j, doors );
						if ( right == 'S' )
							addToRoom( ch, i + 1, j, doors );
						// System.out.println( "Door At: " + i + ":" + j + " " );
					}
				} else if ( c == 'X' )
				{
					squares.add( new Point( i, j ) );
					startSquares.add( new Point( i, j ) );
					System.out.println( "StartSquare At: " + i + ":" + j + " " );
				} else if ( c == 'S' )
					squares.add( new Point( i, j ) );
				else if ( c == '?' )
					centerArea.add( new Point( i, j ) );
				else if ( c != 'W' )
					throw new RuntimeException( "Map contains a wrong char ("
							+ i + ", " + j + ")" );
			}
		}
		colors.put( Cards.COLONELMUSTARD, Color.decode( "#FFDB58" ) );
		colors.put( Cards.MISSSCARLETT, Color.decode( "#FF2400" ) );
		colors.put( Cards.MRSPEACOCK, Color.decode( "#0c59c3" ) );
		colors.put( Cards.PROFESSORPLUM, Color.decode( "#663393" ) );
		colors.put( Cards.MRSWHITE, Color.WHITE );
		colors.put( Cards.THEREVERENDGREEN, Color.GREEN );
		HashMap<Point, Square> squares = createSquares();
		List<Room> rooms = createRooms( doors, squares );
		new Cluedo( rooms, new LinkedList<Square>( squares.values() )
				  , this, rolls, mi );
		setVisible( true );
		repaint();
		// create and connect places
	}

	private HashMap<Point, Square> createSquares()
	{
		HashMap<Point, Square> sq = new HashMap<Point, Square>();
		// create squares
		for ( Point p : squares )
		{
			Square nSquare = new Square( p
					, new Rectangle( p.x * tileW, p.y * tileH, tileW, tileH ) );
			sq.put( p, nSquare );
			// collect start squares
			if ( startSquares.contains(p) )
				starts.add(nSquare);
			// connect squares
			for ( Square s : sq.values() )
			{
				if ( isNeighbour( nSquare, s ) )
				{
					nSquare.connectTo( s );
					s.connectTo( nSquare );
				}
			}
		}
		return sq;
	}
	private List<Room> createRooms(HashMap<Character, LinkedList<Point>> doors
						, HashMap<Point, Square> sq )
	{
		char[] chs = { 'K', 'B', 'A', 'C', 'L', 'H', 'U', 'N', 'I' };
		HashMap<Point, Room> rooms = new HashMap<Point, Room>();

		int leastx, mostx, leasty, mosty;
		for ( char ch : chs )
		{
			// calculate the center of the room
			LinkedList<Point> roomPoints = roomTiles.get( ch );
			Area roomArea = new Area();
			leastx = Integer.MAX_VALUE;
			mostx = 0;
			leasty = Integer.MAX_VALUE;
			mosty = 0;
			for ( Point p : roomPoints )
			{
				leastx = p.x < leastx ? p.x : leastx;
				leasty = p.y < leasty ? p.y : leasty;
				mostx = p.x > mostx ? p.x : mostx;
				mosty = p.y > mosty ? p.y : mosty;
				// also create the area for the room
				roomArea.add( new Area( new Rectangle(
						p.x * tileW, p.y * tileH, tileW, tileH ) ) );
			}
			// connect the room to squares (through doors mapping)
			Point center = new Point( ((mostx - leastx) / 2) + leastx, ((mosty - leasty) / 2) + leasty );
			Room room = new Room( center.x, center.y, Cards.roomID( ch ), roomArea );
			roomCenters.put( ch, center );
			for ( Point sp : doors.get( ch ) )
			{
				Place square = sq.get( sp );
				room.connectTo( square );
				square.connectTo( room );
			}
			rooms.put( center, room );
			// TODO: sort room tiles so we can pick the nearest to the middle
			
			for ( LinkedList<Point> lp : roomTiles.values() )
			{	// TODO: this doesn't sort correctly
				final Point pt = new Point( center );
				Collections.sort( lp, new Comparator<Point>()
				{
					@Override
					public int compare( Point o1, Point o2 )
					{
						return o2.distance( pt ) >= o1.distance( pt ) ? 1 : -1;
					}
				} );
			}
		}
		for ( Room room: rooms.values() )
		{
			// connect secret passages
			if ( room.id == Cards.KITCHEN )
				for ( Room r : rooms.values() )
				{
					if ( r.id == Cards.STUDY )
					{
						r.connectTo( room );
						room.connectTo( r );
						break;
					}
				}
			if ( room.id == Cards.CONSERVATORY )
				for ( Room r : rooms.values() )
				{
					if ( r.id == Cards.LOUNGE )
					{
						r.connectTo( room );
						room.connectTo( r );
						break;
					}
				}
		}
		// assign rooms to weapons
		Cards wpns[] = { Cards.DAGGER, Cards.CANDLESTICK, Cards.LEADPIPE,
				Cards.REVOLVER, Cards.ROPE, Cards.SPANNER };
		int i = 0;
		for ( Cards card : wpns )
		{
			weaponRooms.put( card, chs[ i ] );
			++i;
		}
		return new LinkedList<Room>( rooms.values() );
	}
	// this will be part of the setup of putting players pawn on the board to be displayed
	@Override
	public ArrayList<Player> initPlayers( List<Cards> players, PlayerListener game
								   , LinkedList<LinkedList<Cards>> splits )
	{
		ArrayList<Player> createdPlayers = new ArrayList<Player>();
		if ( players.size() != splits.size() )
			throw new IllegalArgumentException("Not enough cards for all players");
		int i;
		for ( Cards pcard: players )
		{	
			Point startp;
			do
			{
				i = Cards.rand.nextInt( 5 ); // there are 6 start squares
				startp = startSquares.get( i );
			} while( playerPos.containsValue( startp ) );
			Square square = starts.get( i );
			playerPos.put( pcard, startSquares.get( i ) );
			square.setOccupied( true );
			Player pl = new Player( pcard, square, splits.getFirst(), this, game );
			addMouseListener( pl );
			createdPlayers.add( pl );
			splits.removeFirst();
		}
		repaint();
		return createdPlayers;
	}

	// can you get to this Square from that Square
	private boolean isNeighbour( Square s1, Square s2 )
	{
		if ( (s1.location.x == s2.location.x
				&& (s1.location.y == s2.location.y + 1 || s1.location.y == s2.location.y - 1))
			|| (s1.location.y == s2.location.y
				&& (s1.location.x == s2.location.x + 1 || s1.location.x == s2.location.x - 1)) )
			return true;
		return false;
	}
	// add the points that represent tiles in the room
	private void addToRoom( char c, int x, int y,
			HashMap<Character, LinkedList<Point>> map )
	{
		LinkedList<Point> points = map.get( c );
		if ( points == null )
		{	map.put( c,
				new LinkedList<Point>( Arrays.asList( new Point( x, y ) ) ) );
		} else
		{
			points.add( new Point( x, y ) );
			map.put( c, points );
		}
	}

	private boolean isInRoom( char c )
	{
		switch ( c )
		{
		// Doors, Passages
		case 'D':
		case 'd':
		case 'P':
			return true;
		}
		return false;
	}

	private boolean isRoom( char c )
	{
		switch ( c )
		{
		// Kitchen, ...
		case 'K':
		case 'B':
		case 'A':
		case 'C':
		case 'L':
		case 'H':
		case 'U':
		case 'N':
		case 'I':
			return true;
		}
		return false;
	}

	private char findRoomType( int x, int y )
	{
		for ( int i = -1; i < 2; ++i )
			for ( int j = -1; j < 2; ++j )
			{
				int sx = x + i, sy = y + j;
				if ( sx > 0 && sy > 0 && sx < width && sy < height
						&& isRoom( tiles[ sx ][ sy ] ) )
					return tiles[ sx ][ sy ];
			}
		return 'W';
	}

	@Override
	protected void paintComponent( Graphics gr )
	{
		super.paintComponent( gr );
		Graphics2D g = (Graphics2D) gr;
		RenderingHints rh = new RenderingHints(
	             RenderingHints.KEY_TEXT_ANTIALIASING,
	             RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		rh.add( new RenderingHints(
	             RenderingHints.KEY_ALPHA_INTERPOLATION,
	             RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY) );
		g.setRenderingHints(rh);
		g.setColor( Color.DARK_GRAY );
		Rectangle r = g.getClipBounds();
		g.fillRect( r.x, r.y, r.width, r.height );
		int i, j;
		// draw mansion
		for ( i = 0; i < width; ++i )
		{
			for ( j = 0; j < height; ++j )
			{
				g.translate( i * tileW, j * tileH );
				char ch = tiles[ i ][ j ];
				char test = isInRoom( ch ) ? findRoomType( i, j ) : ch;
				// draw room tiles
				boolean highlight = highlights != null && highlights.contains( new Point( i, j ) );
				if ( isRoom( test ) )
				{
					if ( highlight )
						g.setColor( Color.GREEN );
					else g.setColor( Color.BLUE );
					g.fillRect( 0, 0, tileW, tileH );
					if ( ch == 'P' )
					{
						g.setColor( Color.DARK_GRAY );
						drawPassage( (Graphics2D) g );
					} else
					{
						g.setColor( Color.BLACK );
						if ( ch != 'd' )
						{
							if ( j > 0 && testForWalls( test, i, j - 1 ) )
								g.fillRect( 0, 0, tileW, wallthickness ); // wall
																			// above
							if ( j + 1 < height
									&& testForWalls( test, i, j + 1 ) )
								g.fillRect( 0, tileH - wallthickness, tileW,
										tileH ); // wall
													// below
						}
						if ( ch != 'D' )
						{
							if ( i > 0 && testForWalls( test, i - 1, j ) )
								g.fillRect( 0, 0, wallthickness, tileH ); // wall
																			// left
							if ( i + 1 < width && testForWalls( test, i + 1, j ) )
								g.fillRect( tileW - wallthickness, 0, tileW,
										tileH ); // wall
													// right
						}
					}
					// draw square tiles
				} else if ( ch == 'S' || ch == 'X' )
				{
					if ( highlight ) g.setColor( Color.GREEN );
					else g.setColor( Color.YELLOW );
					g.fillRect( 0, 0, tileW, tileH );
					g.setColor( Color.BLACK );
					drawSquare( (Graphics2D) g );
				} else
				{ // draw wall
					g.setColor( Color.BLACK );
					g.fillRect( 0, 0, tileW, tileH );
				}
				g.translate( -i * tileW, -j * tileH );
			}
		}
		// draw room titles
		g.setColor( Color.MAGENTA );
		Rectangle b = this.getBounds();
		g.drawString( Cards.KITCHEN.name(), (int) (b.width * 0.07f),
				(int) (b.height * 0.15f) );
		g.drawString( Cards.BALLROOM.name(), (int) (b.width * 0.44f),
				(int) (b.height * 0.20f) );
		g.drawString( Cards.CONSERVATORY.name(), (int) (b.width * 0.79f),
				(int) (b.height * 0.15f) );
		g.drawString( Cards.DININGROOM.name(), (int) (b.width * 0.07f),
				(int) (b.height * 0.52f) );
		g.drawString( Cards.BILLIARDROOM.name(), (int) (b.width * 0.79f),
				(int) (b.height * 0.43f) );
		g.drawString( Cards.LIBRARY.name(), (int) (b.width * 0.82f),
				(int) (b.height * 0.67f) );
		g.drawString( Cards.LOUNGE.name(), (int) (b.width * 0.09f),
				(int) (b.height * 0.89f) );
		g.drawString( Cards.HALL.name(), (int) (b.width * 0.47f),
				(int) (b.height * 0.87f) );
		g.drawString( Cards.STUDY.name(), (int) (b.width * 0.82f),
				(int) (b.height * 0.94f) );
		// draw weapons
		g.setColor( Color.GRAY );
		Point p = roomTiles.get( weaponRooms.get( Cards.CANDLESTICK ) )
				.getFirst();
		drawCandelStick( (Graphics2D) g, p.x * tileW, p.y * tileH );
		p = roomTiles.get( weaponRooms.get( Cards.DAGGER ) ).getFirst();
		drawDagger( (Graphics2D) g, p.x * tileW, p.y * tileH );
		p = roomTiles.get( weaponRooms.get( Cards.LEADPIPE ) ).getFirst();
		drawleadPipe( (Graphics2D) g, p.x * tileW, p.y * tileH );
		p = roomTiles.get( weaponRooms.get( Cards.REVOLVER ) ).getFirst();
		drawRevolver( (Graphics2D) g, p.x * tileW, p.y * tileH );
		p = roomTiles.get( weaponRooms.get( Cards.ROPE ) ).getFirst();
		drawRope( (Graphics2D) g, p.x * tileW, p.y * tileH );
		p = roomTiles.get( weaponRooms.get( Cards.SPANNER ) ).getFirst();
		drawSpanner( (Graphics2D) g, p.x * tileW, p.y * tileH );
		// draw Areas that can be moved to if there are any
		if ( highlights != null )
		{
			g.setColor( Color.GREEN );
			for ( Shape s: highlights )
				g.draw( s );
		}
		// draw players
		if ( playerPos.size() > 2  )
		{
			for ( Cards c: playerPos.keySet() ){
				p = playerPos.get(c);
				drawPawn( (Graphics2D) g, p.x * tileW, p.y * tileH, colors.get( c ) );
			}
		}
	}

	private boolean testForWalls( char test, int i, int j )
	{
		char c = tiles[ i ][ j ];
		return c != test && !isInRoom( c ) && c != 'W';
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension( width * tileW, height * tileH );
	}

	@Override
	public void playerMoved( Cards player, Place location )
	{
		highlights = null; // turn off highlighting
		LinkedList<Point> tiles = null;
		// place the player at a random place in the
		// room not occupied by anyone else.
		if ( roomCenters.containsValue( location ) )
			tiles = roomTiles.get(player);
		else
		{	// trusting that location is in a non-occupied square
			playerPos.put( player, location.getLocation() );
			paintImmediately( getVisibleRect() );
			return;
		}
		int index = 0;
		while ( index == -1 )
		{
			index = Cards.rand.nextInt( tiles.size() );
			if ( playerPos.containsValue( tiles.get(index)) )
				index = -1;
		}
		playerPos.put( player, tiles.get( index ) );
		paintImmediately( getVisibleRect() );
	}

	@Override
	public void showPossibleMoves( List<Shape> shapes )
	{
		highlights = shapes;
		repaint();
	}

	private void drawSquare( Graphics2D g )
	{
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 0.00000000, 200.00000000 );
		gp.lineTo( 200.00000000, 200.00000000 );
		gp.lineTo( 200.00000000, 0.00000000 );
		gp.lineTo( 145.00000000, 55.00000000 );
		gp.lineTo( 55.00000000, 55.00000000 );
		gp.lineTo( 0.00000000, 0.00000000 );
		gp.lineTo( 0.00000000, 200.00000000 );
		gp.closePath();
		gp.moveTo( 0.00000000, 0.00000000 );
		gp.lineTo( 200.00000000, 0.00000000 );
		gp.lineTo( 0.00000000, 0.00000000 );
		gp.transform( transform );
		g.draw( gp );
	}

	private void drawCandelStick( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 97.84375000, 183.25000000 );
		gp.curveTo( 79.89449600, 183.25000000, 65.34375000, 182.13071200,
				65.34375000, 180.75000000 );
		gp.curveTo( 65.34375000, 179.79684600, 72.36277100, 178.98425700,
				82.56250000, 178.56250000 );
		gp.lineTo( 82.56250000, 149.50000000 );
		gp.curveTo( 79.65711000, 148.19791800, 77.90625000, 146.54933200,
				77.90625000, 144.71875000 );
		gp.curveTo( 77.90625000, 142.42241400, 80.66527200, 140.34450300,
				85.00000000, 138.96875000 );
		gp.lineTo( 85.00000000, 49.90625000 );
		gp.curveTo( 80.04177500, 52.04368000, 70.04209300, 40.40697000,
				65.00000000, 45.00000000 );
		gp.lineTo( 40.00000000, 20.00000000 );
		gp.curveTo( 80.00000000, 20.20032000, 115.00000000, 19.55753000,
				155.00000000, 20.00000000 );
		gp.lineTo( 130.00000000, 45.00000000 );
		gp.curveTo( 125.10429000, 40.17035000, 115.09565000, 51.65656000,
				110.00000000, 49.53125000 );
		gp.lineTo( 110.00000000, 138.75000000 );
		gp.curveTo( 114.79537000, 140.12008900, 117.90625000, 142.28353300,
				117.90625000, 144.71875000 );
		gp.curveTo( 117.90625000, 146.68477600, 115.86318000, 148.44378200,
				112.56250000, 149.78125000 );
		gp.lineTo( 112.56250000, 178.53125000 );
		gp.curveTo( 123.07960000, 178.94520300, 130.34375000, 179.77882200,
				130.34375000, 180.75000000 );
		gp.curveTo( 130.34375000, 182.13071200, 115.79300000, 183.25000000,
				97.84375000, 183.25000000 );
		gp.lineTo( 97.84375000, 183.25000000 );
		gp.closePath();
		gp.transform( transform );
		g.setColor(Color.BLACK);
		g.draw( gp );
		g.setColor( Color.YELLOW );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawDagger( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 69.22524900, 114.95104602 );
		gp.lineTo( 95.63170711, 138.59115632 );
		gp.curveTo( 122.73056012, 113.23255027, 161.38870553, 73.41301338,
				182.76476042, 42.93105383 );
		gp.curveTo( 192.87405572, 27.37161090, 197.36499504, 13.52312079,
				199.80603851, 0.29896349 );
		gp.lineTo( 69.22524900, 114.95104602 );
		gp.closePath();
		gp.moveTo( 0.86805365, 179.72276353 );
		gp.lineTo( 22.43945565, 199.03433953 );
		gp.curveTo( 25.44206241, 192.06297436, 28.32282635, 184.98253065,
				38.18043315, 184.14803383 );
		gp.curveTo( 38.54873370, 172.34076964, 46.74732389, 171.51997476,
				53.92141065, 169.26172813 );
		gp.curveTo( 55.76764850, 162.92873600, 57.15162083, 156.40939822,
				69.66238815, 154.37542243 );
		gp.curveTo( 72.52236653, 145.16714208, 78.49678211, 141.49902680,
				85.40336565, 139.48911673 );
		gp.lineTo( 63.83196365, 120.17754073 );
		gp.lineTo( 0.86805365, 179.72276353 );
		gp.closePath();
		gp.moveTo( 58.43911315, 115.34964673 );
		gp.lineTo( 96.18906665, 149.14490473 );
		gp.lineTo( 101.43605915, 144.18280283 );
		gp.lineTo( 63.68610565, 110.38754483 );
		gp.lineTo( 58.43911315, 115.34964673 );
		gp.closePath();
		gp.transform( transform );
		g.setColor(Color.BLACK);
		g.draw( gp );
		g.setColor( Color.RED );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawRevolver( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 42.62646800, 74.47348000 );
		gp.lineTo( 57.79560900, 52.04258000 );
		gp.lineTo( 65.31066500, 63.74374000 );
		gp.lineTo( 110.82616000, 9.20438000 );
		gp.lineTo( 136.70893000, 36.31588000 );
		gp.lineTo( 86.75895500, 97.58123000 );
		gp.lineTo( 137.69128000, 177.66332000 );
		gp.lineTo( 116.43337000, 191.11467000 );
		gp.lineTo( 42.62646800, 74.47348000 );
		gp.closePath();
		gp.transform( transform );
		g.draw( gp );
		g.setColor( Color.BLACK );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawRope( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 97.76290400, 183.38100000 );
		gp.curveTo( 59.31244600, 183.45960000, 67.58159500, 151.86498000,
				69.94557100, 113.32194000 );
		gp.lineTo( 68.59229500, 113.32194000 );
		gp.curveTo( 64.76337300, 109.48016000, 65.14409000, 106.19263000,
				68.59229500, 103.28671000 );
		gp.lineTo( 70.37160200, 103.28671000 );
		gp.curveTo( 70.41244200, 101.62020000, 70.44678200, 99.93529000,
				70.44678200, 98.25346000 );
		gp.curveTo( 70.44678200, 96.48119000, 70.45078200, 94.92063000,
				70.44678200, 93.22021000 );
		gp.lineTo( 68.59229300, 93.22021000 );
		gp.curveTo( 65.17277000, 89.46252000, 64.19631600, 86.00941000,
				68.59229300, 83.18498000 );
		gp.lineTo( 70.42172100, 83.18498000 );
		gp.curveTo( 70.07574800, 21.92968000, 68.67516600, 13.20328000,
				97.76290200, 13.12588000 );
		gp.curveTo( 107.98512000, 13.09888000, 114.08220000, 14.38738000,
				117.68613000, 19.37838000 );
		gp.lineTo( 120.11701000, 9.56198000 );
		gp.lineTo( 127.05881000, 14.59518000 );
		gp.lineTo( 122.04668000, 34.82198000 );
		gp.curveTo( 123.04048000, 45.31898000, 122.75398000, 60.78149000,
				122.62307000, 83.18498000 );
		gp.lineTo( 125.68048000, 83.18498000 );
		gp.curveTo( 129.68348000, 86.53436000, 130.61840000, 89.87085000,
				125.68048000, 93.22021000 );
		gp.lineTo( 122.59801000, 93.22021000 );
		gp.curveTo( 122.59401000, 94.92082000, 122.57291000, 96.48117000,
				122.57291000, 98.25346000 );
		gp.curveTo( 122.57291000, 99.97217000, 122.60481000, 101.63369000,
				122.62301000, 103.28671000 );
		gp.lineTo( 125.68042000, 103.28671000 );
		gp.curveTo( 128.74861000, 105.95215000, 130.89240000, 108.81079000,
				125.68042000, 113.32194000 );
		gp.lineTo( 122.82350000, 113.32194000 );
		gp.curveTo( 124.69304000, 170.45856000, 135.48463000, 183.30393000,
				97.76284200, 183.38100000 );
		gp.lineTo( 97.76290400, 183.38100000 );
		gp.closePath();
		gp.moveTo( 97.68772400, 173.28324000 );
		gp.curveTo( 124.19255000, 173.27314000, 116.27670000, 164.84623000,
				114.77909000, 113.32194000 );
		gp.lineTo( 110.84457000, 113.32194000 );
		gp.curveTo( 112.55189000, 156.07654000, 120.14267000, 168.34728000,
				97.93833100, 169.09408000 );
		gp.curveTo( 74.38702300, 169.88618000, 79.82652700, 143.04082000,
				81.87444700, 113.32194000 );
		gp.lineTo( 78.11534800, 113.32194000 );
		gp.curveTo( 76.38992700, 153.29329000, 69.76363300, 173.29391000,
				97.68772400, 173.28324000 );
		gp.lineTo( 97.68772400, 173.28324000 );
		gp.closePath();
		gp.moveTo( 97.86314900, 160.77828000 );
		gp.curveTo( 112.69078000, 160.77828000, 105.70670000, 140.41166000,
				103.25119000, 113.32194000 );
		gp.lineTo( 89.91891900, 113.32194000 );
		gp.curveTo( 88.05480800, 140.39153000, 83.04288000, 160.77828000,
				97.86314900, 160.77828000 );
		gp.lineTo( 97.86314900, 160.77828000 );
		gp.closePath();
		gp.moveTo( 78.44113700, 103.28671000 );
		gp.lineTo( 82.40072100, 103.28671000 );
		gp.curveTo( 82.45027100, 101.60530000, 82.47590100, 99.93115000,
				82.47590100, 98.25346000 );
		gp.curveTo( 82.47590100, 96.57572000, 82.45825100, 94.89527000,
				82.42578100, 93.22021000 );
		gp.lineTo( 78.44113600, 93.22021000 );
		gp.curveTo( 78.45157600, 94.86578000, 78.46619600, 96.53154000,
				78.46619600, 98.25346000 );
		gp.curveTo( 78.46619600, 99.95857000, 78.46690200, 101.63967000,
				78.44113600, 103.28671000 );
		gp.lineTo( 78.44113700, 103.28671000 );
		gp.closePath();
		gp.moveTo( 90.42013300, 103.28671000 );
		gp.lineTo( 102.62467000, 103.28671000 );
		gp.curveTo( 102.56707000, 101.62341000, 102.52443000, 99.94877000,
				102.52443000, 98.25346000 );
		gp.curveTo( 102.52443000, 96.55814000, 102.56703000, 94.87880000,
				102.62467000, 93.22021000 );
		gp.lineTo( 90.42013300, 93.22021000 );
		gp.curveTo( 90.46506300, 94.87883000, 90.49531300, 96.55811000,
				90.49531300, 98.25346000 );
		gp.curveTo( 90.49531300, 99.94880000, 90.46505300, 101.62337000,
				90.42013300, 103.28671000 );
		gp.lineTo( 90.42013300, 103.28671000 );
		gp.closePath();
		gp.moveTo( 110.56890000, 103.28671000 );
		gp.lineTo( 114.57861000, 103.28671000 );
		gp.curveTo( 114.56271000, 101.64112000, 114.55351000, 99.97537000,
				114.55351000, 98.25346000 );
		gp.curveTo( 114.55351000, 96.47401000, 114.57361000, 94.91585000,
				114.57861000, 93.22021000 );
		gp.lineTo( 110.56890000, 93.22021000 );
		gp.curveTo( 110.55440000, 94.86225000, 110.54380000, 96.53402000,
				110.54380000, 98.25346000 );
		gp.curveTo( 110.54380000, 99.97289000, 110.54680000, 101.64464000,
				110.56890000, 103.28671000 );
		gp.lineTo( 110.56890000, 103.28671000 );
		gp.closePath();
		gp.moveTo( 78.31583300, 83.18498000 );
		gp.lineTo( 82.07493300, 83.18498000 );
		gp.curveTo( 80.79788900, 53.80955000, 77.84432300, 27.97538000,
				97.93833100, 28.10058000 );
		gp.curveTo( 108.20281000, 28.16458000, 111.38190000, 31.90368000,
				111.97230000, 42.44998000 );
		gp.lineTo( 113.95209000, 34.41558000 );
		gp.curveTo( 112.32386000, 24.87248000, 108.10354000, 23.73818000,
				97.68772400, 23.59878000 );
		gp.curveTo( 72.73789300, 23.26488000, 77.36341500, 31.75658000,
				78.31583300, 83.18498000 );
		gp.lineTo( 78.31583300, 83.18498000 );
		gp.closePath();
		gp.moveTo( 89.91891900, 83.18498000 );
		gp.lineTo( 103.27625000, 83.18498000 );
		gp.curveTo( 103.53344000, 80.37945000, 103.82775000, 77.68990000,
				104.15337000, 75.05675000 );
		gp.lineTo( 103.92783000, 74.90044000 );
		gp.lineTo( 104.42904000, 72.89964000 );
		gp.curveTo( 107.19737000, 51.50778000, 110.70265000, 36.76028000,
				97.86314900, 36.76028000 );
		gp.curveTo( 83.05353000, 36.76028000, 88.05163500, 56.36592000,
				89.91891900, 83.18498000 );
		gp.lineTo( 89.91891900, 83.18498000 );
		gp.closePath();
		gp.moveTo( 110.74433000, 83.18498000 );
		gp.lineTo( 114.62873000, 83.18498000 );
		gp.curveTo( 114.68763000, 75.76211000, 114.74237000, 69.73802000,
				114.80415000, 64.05238000 );
		gp.lineTo( 110.86963000, 79.96495000 );
		gp.lineTo( 110.81953000, 79.93365000 );
		gp.curveTo( 110.78763000, 80.96787000, 110.77213000, 82.11254000,
				110.74433000, 83.18495000 );
		gp.lineTo( 110.74433000, 83.18498000 );
		gp.closePath();
		gp.transform( transform );
		g.setColor(Color.BLACK);
		g.draw( gp );
		g.setColor( Color.WHITE );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawSpanner( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 47.50000000, 185.00000000 );
		gp.curveTo( 45.88005700, 185.00000000, 44.32431300, 184.80676200,
				42.78125000, 184.56250000 );
		gp.lineTo( 56.18750000, 169.81250000 );
		gp.curveTo( 56.06653000, 168.39736800, 55.87352500, 167.08428300,
				55.59375000, 165.84375000 );
		gp.curveTo( 55.31397500, 164.60321700, 54.94024200, 163.43910500,
				54.50000000, 162.37500000 );
		gp.curveTo( 54.05975800, 161.31089500, 53.53987300, 160.32334900,
				52.93750000, 159.43750000 );
		gp.curveTo( 52.33512700, 158.55165100, 51.64116600, 157.76826400,
				50.87500000, 157.06250000 );
		gp.curveTo( 50.10883400, 156.35673600, 49.27537300, 155.74260100,
				48.34375000, 155.21875000 );
		gp.curveTo( 47.41212700, 154.69489900, 46.37999200, 154.24635900,
				45.28125000, 153.90625000 );
		gp.curveTo( 44.18250800, 153.56614100, 43.01752500, 153.34203800,
				41.75000000, 153.18750000 );
		gp.curveTo( 40.48247500, 153.03296200, 39.12547000, 152.96713900,
				37.68750000, 153.00000000 );
		gp.lineTo( 22.28125000, 169.93750000 );
		gp.curveTo( 20.82185600, 166.88600700, 20.00000000, 163.53966900,
				20.00000000, 160.00000000 );
		gp.curveTo( 20.00000000, 146.19288100, 32.31216900, 135.00000000,
				47.50000000, 135.00000000 );
		gp.curveTo( 51.03480700, 135.00000000, 54.42694400, 135.61431700,
				57.53125000, 136.71875000 );
		gp.lineTo( 128.21875000, 52.81250000 );
		gp.curveTo( 125.98558000, 49.19809000, 124.71875000, 45.03438000,
				124.71875000, 40.59375000 );
		gp.curveTo( 124.71875000, 26.78663000, 137.03092000, 15.59375000,
				152.21875000, 15.59375000 );
		gp.curveTo( 154.36815000, 15.59375000, 156.43004000, 15.85722000,
				158.43750000, 16.28125000 );
		gp.lineTo( 145.06250000, 30.93750000 );
		gp.curveTo( 145.17958000, 32.35296000, 145.37988000, 33.66495000,
				145.65625000, 34.90625000 );
		gp.curveTo( 145.93262000, 36.14755000, 146.28143000, 37.30969000,
				146.71875000, 38.37500000 );
		gp.curveTo( 147.15607000, 39.44031000, 147.68131000, 40.42500000,
				148.28125000, 41.31250000 );
		gp.curveTo( 148.88119000, 42.20000000, 149.54827000, 42.97963000,
				150.31250000, 43.68750000 );
		gp.curveTo( 151.07673000, 44.39537000, 151.94482000, 45.03610000,
				152.87500000, 45.56250000 );
		gp.curveTo( 153.80518000, 46.08890000, 154.80845000, 46.50063000,
				155.90625000, 46.84375000 );
		gp.curveTo( 157.00405000, 47.18687000, 158.17040000, 47.46699000,
				159.43750000, 47.62500000 );
		gp.curveTo( 160.70460000, 47.78301000, 162.06195000, 47.84143000,
				163.50000000, 47.81250000 );
		gp.lineTo( 177.93750000, 32.00000000 );
		gp.curveTo( 179.02942000, 34.69379000, 179.71875000, 37.55534000,
				179.71875000, 40.59375000 );
		gp.curveTo( 179.71875000, 54.40087000, 167.40658000, 65.59375000,
				152.21875000, 65.59375000 );
		gp.curveTo( 149.53016000, 65.59375000, 146.92476000, 65.24766000,
				144.46875000, 64.59375000 );
		gp.lineTo( 72.65625000, 149.87500000 );
		gp.curveTo( 74.16712200, 152.97100000, 75.00000000, 156.39480800,
				75.00000000, 160.00000000 );
		gp.curveTo( 75.00000000, 173.80711900, 62.68783100, 185.00000000,
				47.50000000, 185.00000000 );
		gp.lineTo( 47.50000000, 185.00000000 );
		gp.closePath();
		gp.transform( transform );
		g.setColor(Color.BLACK);
		g.draw( gp );
		g.setColor( Color.DARK_GRAY );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawleadPipe( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 88.43956100, 183.84396000 );
		gp.curveTo( 93.43956100, 188.38560000, 98.43956100, 187.67655000,
				103.43956000, 183.84396000 );
		gp.curveTo( 103.57825000, 127.15595000, 126.18307000, 67.01163000,
				103.43956000, 13.84398000 );
		gp.curveTo( 96.33238800, 9.20458000, 89.27652000, 9.45258000,
				88.43956100, 13.84398000 );
		gp.curveTo( 112.37247000, 68.86008000, 88.96145000, 127.14130000,
				88.43956100, 183.84396000 );
		gp.lineTo( 88.43956100, 183.84396000 );
		gp.closePath();
		gp.transform( transform );
		g.setColor( Color.LIGHT_GRAY );
		g.draw( gp );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawPawn( Graphics2D g, int tx, int ty, Color color )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 99.93750000, 199.93750000 );
		gp.curveTo( 89.99999500, 199.87577637, 85.00000000, 190.00000000,
				85.00000000, 130.00000000 );
		gp.curveTo( 85.00000000, 114.94955000, 84.94598000, 92.41163000,
				85.00000000, 80.00000000 );
		gp.curveTo( 80.73601700, 60.22102000, 55.00000000, 43.15323000,
				55.00000000, 30.00000000 );
		gp.curveTo( 55.00000000, 13.43146000, 75.00000000, 0.00000000,
				100.00000000, 0.00000000 );
		gp.curveTo( 125.00000000, 0.00000000, 145.00000000, 13.43146000,
				145.00000000, 30.00000000 );
		gp.curveTo( 145.00000000, 43.17832000, 118.59686000, 59.86603000,
				115.00000000, 80.00000000 );
		gp.curveTo( 114.50342000, 92.78549000, 115.00000000, 114.96123000,
				115.00000000, 130.00000000 );
		gp.curveTo( 115.00000000, 190.00000000, 110.00000000, 200.00000000,
				99.93750000, 199.93750000 );
		gp.lineTo( 99.93750000, 199.93750000 );
		gp.closePath();
		gp.transform( transform );
		g.setColor( Color.BLACK );
		g.draw( gp );
		g.setColor( color );
		g.fill( gp );
		g.translate( -tx, -ty );
	}

	private void drawPassage( Graphics2D g )
	{
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 200.00000000, 0.00000000 );
		gp.lineTo( 200.00000000, 200.00000000 );
		gp.lineTo( 0.00000000, 200.00000000 );
		gp.lineTo( 0.00000000, 0.00000000 );
		gp.lineTo( 200.00000000, 0.00000000 );
		gp.closePath();
		gp.moveTo( 0.26525199, 200.26525199 );
		gp.lineTo( 200.00000000, 0.00000000 );
		gp.lineTo( 104.24403000, 200.53050398 );
		gp.lineTo( 0.26525199, 200.26525199 );
		gp.moveTo( 200.00000000, 0.00000000 );
		gp.lineTo( 0.53050398, 95.75597000 );
		gp.lineTo( 200.00000000, 0.00000000 );
		gp.transform( transform );
		g.draw( gp );
	}
}
