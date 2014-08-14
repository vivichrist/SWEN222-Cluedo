package ui;

import game.Cluedo;
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
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.beans.Transient;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.management.RuntimeErrorException;
import javax.swing.JComponent;

@SuppressWarnings( "serial" )
public class CluedoBoard extends JComponent
{
	private char[][]						tiles;
	private int								wallthickness	= 5;
	private List<Point>						startSquares	= new LinkedList<Point>();
	private int								tileW, tileH, height, width;
	private List<Point>						players			= new LinkedList<Point>();
	private List<Point>						centerArea		= new LinkedList<Point>();
	HashMap<Character, LinkedList<Point>>	roomTiles		= new HashMap<Character, LinkedList<Point>>();
	HashMap<Character, Color>				colors			= new HashMap<Character, Color>();
	private Cluedo							cluedo;

	public CluedoBoard( char[][] board )
	{
		tiles = board;
		this.height = board.length; 
		this.width = board[0].length;
		System.out.println( "width:"+width+" height:"+height );
		tileH = 20;
		tileW = 20;
		int i = 0, j = 0;
		char c;
		Room[] rooms = new Room[ 9 ];
		HashMap<Character, LinkedList<Point>> doors = new HashMap<Character, LinkedList<Point>>();
		HashMap<Character, LinkedList<Point>> passages = new HashMap<Character, LinkedList<Point>>();
		List<Point> squares = new LinkedList<Point>();
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
					if ( c == 'P' )
					{
						addToRoom( ch, i, j, passages );
						if ( ch == 'K' )
							addToRoom( 'U', i, j, passages );
						if ( ch == 'C' )
							addToRoom( 'L', i, j, passages );
						if ( ch == 'U' )
							addToRoom( 'K', i, j, passages );
						if ( ch == 'L' )
							addToRoom( 'C', i, j, passages );
					}
					addToRoom( ch, i, j, doors );
				} else if ( c == 'X' )
				{
					squares.add( new Point( i, j ) );
					startSquares.add( new Point( i, j ) );
				} else if ( c == 'S')
					squares.add( new Point( i, j ) );
				else if ( c == '?' )
					centerArea.add( new Point( i, j ) );
				else if ( c != 'W' )
					throw new RuntimeException( "Map contains a wrong char ("
							+ i + ", " + j + ")" );
			}
		}
		// create and connect places
		setVisible( true );
		repaint();
	}

	private void addToRoom( char c, int x, int y,
			HashMap<Character, LinkedList<Point>> map )
	{
		LinkedList<Point> points = map.get( c );
		if ( points == null )
			map.put( c,
					new LinkedList<Point>( Arrays.asList( new Point( x, y ) ) ) );
		else
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
				if ( sx > 0 && sy > 0
						&& sx < width && sy < height 
						&& isRoom( tiles[sx][sy] ) )
					return tiles[sx][sy];
			}
		return 'W';
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
		g.setColor( Color.DARK_GRAY );
		Rectangle r = g.getClipBounds();
		g.fillRect( r.x, r.y, r.width, r.height );
		int i, j;
		for ( i = 0; i < width; ++i )
		{
			for ( j = 0; j < height; ++j )
			{
				g.translate( i * tileW, j * tileH );
				char ch = tiles[i][j];
				char test = isInRoom( ch ) ? findRoomType( i, j ) : ch;
				g.setColor( Color.BLUE );
				if ( isRoom( test ) )
				{
					g.fillRect( 0, 0, tileW, tileH );
					g.setColor( Color.BLACK );
					if ( ch != 'd' )
					{
						if ( j > 0 && testForWalls( test, i, j - 1 ))
							g.fillRect( 0, 0, tileW, wallthickness ); // wall above
						if ( j + 1 < height && testForWalls( test, i, j + 1 ))
							g.fillRect( 0, tileH - wallthickness, tileW, tileH ); // wall below
					}
					if ( ch != 'D' )
					{
						if ( i > 0 && testForWalls( test, i - 1, j ))
							g.fillRect( 0, 0, wallthickness, tileH ); // wall left
						if ( i + 1 < width && testForWalls( test, i + 1, j ))
							g.fillRect( tileW - wallthickness, 0, tileW, tileH ); // wall right
					}
				}
				else if ( ch == 'S' || ch == 'X' )
				{
					g.setColor( Color.YELLOW );
					g.fillRect( 0, 0, tileW, tileH );
					g.setColor( Color.BLACK );
					drawSquare( (Graphics2D) g );
				}
				else 
				{
					g.setColor( Color.BLACK );
					g.fillRect( 0, 0, tileW, tileH );
				}
				g.translate( -i * tileW, -j * tileH );
			}
		}
	}
	
	private boolean testForWalls( char test, int i, int j )
	{
		char c = tiles[i][j];
		return c != test && !isInRoom( c ) && c != 'W';
	}

	@Override
	@Transient
	public Dimension getPreferredSize()
	{
		return new Dimension( (width + 1) * tileW, (height + 1)* tileH );
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
		gp.transform( AffineTransform.getScaleInstance( 0.1d, 0.1f ) );
		g.draw( gp );
	}

	private void drawCandelStick( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 97.84375000, -183.25000000 );
		gp.curveTo( 79.89449600, -183.25000000, 65.34375000, -182.13071200,
				65.34375000, -180.75000000 );
		gp.curveTo( 65.34375000, -179.79684600, 72.36277100, -178.98425700,
				82.56250000, -178.56250000 );
		gp.lineTo( 82.56250000, -149.50000000 );
		gp.curveTo( 79.65711000, -148.19791800, 77.90625000, -146.54933200,
				77.90625000, -144.71875000 );
		gp.curveTo( 77.90625000, -142.42241400, 80.66527200, -140.34450300,
				85.00000000, -138.96875000 );
		gp.lineTo( 85.00000000, -49.90625000 );
		gp.curveTo( 80.04177500, -52.04368000, 70.04209300, -40.40697000,
				65.00000000, -45.00000000 );
		gp.lineTo( 40.00000000, -20.00000000 );
		gp.curveTo( 80.00000000, -20.20032000, 115.00000000, -19.55753000,
				155.00000000, -20.00000000 );
		gp.lineTo( 130.00000000, -45.00000000 );
		gp.curveTo( 125.10429000, -40.17035000, 115.09565000, -51.65656000,
				110.00000000, -49.53125000 );
		gp.lineTo( 110.00000000, -138.75000000 );
		gp.curveTo( 114.79537000, -140.12008900, 117.90625000, -142.28353300,
				117.90625000, -144.71875000 );
		gp.curveTo( 117.90625000, -146.68477600, 115.86318000, -148.44378200,
				112.56250000, -149.78125000 );
		gp.lineTo( 112.56250000, -178.53125000 );
		gp.curveTo( 123.07960000, -178.94520300, 130.34375000, -179.77882200,
				130.34375000, -180.75000000 );
		gp.curveTo( 130.34375000, -182.13071200, 115.79300000, -183.25000000,
				97.84375000, -183.25000000 );
		gp.lineTo( 97.84375000, -183.25000000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}

	private void drawDagger( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 79.93652100, -129.79535000 );
		gp.lineTo( 104.41936000, -129.79535000 );
		gp.curveTo( 104.55478000, -104.11131000, 102.91276000, -65.58526000,
				97.74222900, -39.90118000 );
		gp.curveTo( 94.72093500, -27.16248000, 89.88539200, -17.91298000,
				84.38794800, -9.93658000 );
		gp.lineTo( 79.93652100, -129.79535000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}

	private void drawRevolver( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 42.62646800, -74.47348000 );
		gp.lineTo( 57.79560900, -52.04258000 );
		gp.lineTo( 65.31066500, -63.74374000 );
		gp.lineTo( 110.82616000, -9.20438000 );
		gp.lineTo( 136.70893000, -36.31588000 );
		gp.lineTo( 86.75895500, -97.58123000 );
		gp.lineTo( 137.69128000, -177.66332000 );
		gp.lineTo( 116.43337000, -191.11467000 );
		gp.lineTo( 42.62646800, -74.47348000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}

	private void drawRope( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 97.76290400, -183.38100000 );
		gp.curveTo( 59.31244600, -183.45960000, 67.58159500, -151.86498000,
				69.94557100, -113.32194000 );
		gp.lineTo( 68.59229500, -113.32194000 );
		gp.curveTo( 64.76337300, -109.48016000, 65.14409000, -106.19263000,
				68.59229500, -103.28671000 );
		gp.lineTo( 70.37160200, -103.28671000 );
		gp.curveTo( 70.41244200, -101.62020000, 70.44678200, -99.93529000,
				70.44678200, -98.25346000 );
		gp.curveTo( 70.44678200, -96.48119000, 70.45078200, -94.92063000,
				70.44678200, -93.22021000 );
		gp.lineTo( 68.59229300, -93.22021000 );
		gp.curveTo( 65.17277000, -89.46252000, 64.19631600, -86.00941000,
				68.59229300, -83.18498000 );
		gp.lineTo( 70.42172100, -83.18498000 );
		gp.curveTo( 70.07574800, -21.92968000, 68.67516600, -13.20328000,
				97.76290200, -13.12588000 );
		gp.curveTo( 107.98512000, -13.09888000, 114.08220000, -14.38738000,
				117.68613000, -19.37838000 );
		gp.lineTo( 120.11701000, -9.56198000 );
		gp.lineTo( 127.05881000, -14.59518000 );
		gp.lineTo( 122.04668000, -34.82198000 );
		gp.curveTo( 123.04048000, -45.31898000, 122.75398000, -60.78149000,
				122.62307000, -83.18498000 );
		gp.lineTo( 125.68048000, -83.18498000 );
		gp.curveTo( 129.68348000, -86.53436000, 130.61840000, -89.87085000,
				125.68048000, -93.22021000 );
		gp.lineTo( 122.59801000, -93.22021000 );
		gp.curveTo( 122.59401000, -94.92082000, 122.57291000, -96.48117000,
				122.57291000, -98.25346000 );
		gp.curveTo( 122.57291000, -99.97217000, 122.60481000, -101.63369000,
				122.62301000, -103.28671000 );
		gp.lineTo( 125.68042000, -103.28671000 );
		gp.curveTo( 128.74861000, -105.95215000, 130.89240000, -108.81079000,
				125.68042000, -113.32194000 );
		gp.lineTo( 122.82350000, -113.32194000 );
		gp.curveTo( 124.69304000, -170.45856000, 135.48463000, -183.30393000,
				97.76284200, -183.38100000 );
		gp.lineTo( 97.76290400, -183.38100000 );
		gp.closePath();
		gp.moveTo( 97.68772400, -173.28324000 );
		gp.curveTo( 124.19255000, -173.27314000, 116.27670000, -164.84623000,
				114.77909000, -113.32194000 );
		gp.lineTo( 110.84457000, -113.32194000 );
		gp.curveTo( 112.55189000, -156.07654000, 120.14267000, -168.34728000,
				97.93833100, -169.09408000 );
		gp.curveTo( 74.38702300, -169.88618000, 79.82652700, -143.04082000,
				81.87444700, -113.32194000 );
		gp.lineTo( 78.11534800, -113.32194000 );
		gp.curveTo( 76.38992700, -153.29329000, 69.76363300, -173.29391000,
				97.68772400, -173.28324000 );
		gp.lineTo( 97.68772400, -173.28324000 );
		gp.closePath();
		gp.moveTo( 97.86314900, -160.77828000 );
		gp.curveTo( 112.69078000, -160.77828000, 105.70670000, -140.41166000,
				103.25119000, -113.32194000 );
		gp.lineTo( 89.91891900, -113.32194000 );
		gp.curveTo( 88.05480800, -140.39153000, 83.04288000, -160.77828000,
				97.86314900, -160.77828000 );
		gp.lineTo( 97.86314900, -160.77828000 );
		gp.closePath();
		gp.moveTo( 78.44113700, -103.28671000 );
		gp.lineTo( 82.40072100, -103.28671000 );
		gp.curveTo( 82.45027100, -101.60530000, 82.47590100, -99.93115000,
				82.47590100, -98.25346000 );
		gp.curveTo( 82.47590100, -96.57572000, 82.45825100, -94.89527000,
				82.42578100, -93.22021000 );
		gp.lineTo( 78.44113600, -93.22021000 );
		gp.curveTo( 78.45157600, -94.86578000, 78.46619600, -96.53154000,
				78.46619600, -98.25346000 );
		gp.curveTo( 78.46619600, -99.95857000, 78.46690200, -101.63967000,
				78.44113600, -103.28671000 );
		gp.lineTo( 78.44113700, -103.28671000 );
		gp.closePath();
		gp.moveTo( 90.42013300, -103.28671000 );
		gp.lineTo( 102.62467000, -103.28671000 );
		gp.curveTo( 102.56707000, -101.62341000, 102.52443000, -99.94877000,
				102.52443000, -98.25346000 );
		gp.curveTo( 102.52443000, -96.55814000, 102.56703000, -94.87880000,
				102.62467000, -93.22021000 );
		gp.lineTo( 90.42013300, -93.22021000 );
		gp.curveTo( 90.46506300, -94.87883000, 90.49531300, -96.55811000,
				90.49531300, -98.25346000 );
		gp.curveTo( 90.49531300, -99.94880000, 90.46505300, -101.62337000,
				90.42013300, -103.28671000 );
		gp.lineTo( 90.42013300, -103.28671000 );
		gp.closePath();
		gp.moveTo( 110.56890000, -103.28671000 );
		gp.lineTo( 114.57861000, -103.28671000 );
		gp.curveTo( 114.56271000, -101.64112000, 114.55351000, -99.97537000,
				114.55351000, -98.25346000 );
		gp.curveTo( 114.55351000, -96.47401000, 114.57361000, -94.91585000,
				114.57861000, -93.22021000 );
		gp.lineTo( 110.56890000, -93.22021000 );
		gp.curveTo( 110.55440000, -94.86225000, 110.54380000, -96.53402000,
				110.54380000, -98.25346000 );
		gp.curveTo( 110.54380000, -99.97289000, 110.54680000, -101.64464000,
				110.56890000, -103.28671000 );
		gp.lineTo( 110.56890000, -103.28671000 );
		gp.closePath();
		gp.moveTo( 78.31583300, -83.18498000 );
		gp.lineTo( 82.07493300, -83.18498000 );
		gp.curveTo( 80.79788900, -53.80955000, 77.84432300, -27.97538000,
				97.93833100, -28.10058000 );
		gp.curveTo( 108.20281000, -28.16458000, 111.38190000, -31.90368000,
				111.97230000, -42.44998000 );
		gp.lineTo( 113.95209000, -34.41558000 );
		gp.curveTo( 112.32386000, -24.87248000, 108.10354000, -23.73818000,
				97.68772400, -23.59878000 );
		gp.curveTo( 72.73789300, -23.26488000, 77.36341500, -31.75658000,
				78.31583300, -83.18498000 );
		gp.lineTo( 78.31583300, -83.18498000 );
		gp.closePath();
		gp.moveTo( 89.91891900, -83.18498000 );
		gp.lineTo( 103.27625000, -83.18498000 );
		gp.curveTo( 103.53344000, -80.37945000, 103.82775000, -77.68990000,
				104.15337000, -75.05675000 );
		gp.lineTo( 103.92783000, -74.90044000 );
		gp.lineTo( 104.42904000, -72.89964000 );
		gp.curveTo( 107.19737000, -51.50778000, 110.70265000, -36.76028000,
				97.86314900, -36.76028000 );
		gp.curveTo( 83.05353000, -36.76028000, 88.05163500, -56.36592000,
				89.91891900, -83.18498000 );
		gp.lineTo( 89.91891900, -83.18498000 );
		gp.closePath();
		gp.moveTo( 110.74433000, -83.18498000 );
		gp.lineTo( 114.62873000, -83.18498000 );
		gp.curveTo( 114.68763000, -75.76211000, 114.74237000, -69.73802000,
				114.80415000, -64.05238000 );
		gp.lineTo( 110.86963000, -79.96495000 );
		gp.lineTo( 110.81953000, -79.93365000 );
		gp.curveTo( 110.78763000, -80.96787000, 110.77213000, -82.11254000,
				110.74433000, -83.18495000 );
		gp.lineTo( 110.74433000, -83.18498000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}

	private void drawSpanner( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 52.92401600, -180.04187000 );
		gp.curveTo( 51.73491700, -180.04187000, 50.56210400, -179.95817000,
				49.42171300, -179.78486000 );
		gp.curveTo( 54.84701500, -174.25596000, 57.45093800, -173.48156000,
				61.79460400, -167.64926000 );
		gp.curveTo( 67.04049700, -160.60553000, 48.25355900, -144.79164000,
				41.31019900, -150.24496000 );
		gp.curveTo( 36.39640100, -154.10426000, 35.71524000, -155.18907000,
				30.21646200, -160.68031000 );
		gp.curveTo( 30.04221400, -159.54590000, 29.95809500, -158.37923000,
				29.95809500, -157.19637000 );
		gp.curveTo( 29.95809500, -144.57915000, 40.24028800, -134.35087000,
				52.92401600, -134.35087000 );
		gp.curveTo( 65.60774500, -134.35087000, 75.88993800, -144.57915000,
				75.88993800, -157.19637000 );
		gp.curveTo( 75.88993800, -169.81360000, 65.60774500, -180.04187000,
				52.92401600, -180.04187000 );
		gp.lineTo( 52.92401600, -180.04187000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}

	private void drawleadPipe( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 88.43956100, -183.84396000 );
		gp.curveTo( 93.43956100, -188.38560000, 98.43956100, -187.67655000,
				103.43956000, -183.84396000 );
		gp.curveTo( 103.57825000, -127.15595000, 126.18307000, -67.01163000,
				103.43956000, -13.84398000 );
		gp.curveTo( 96.33238800, -9.20458000, 89.27652000, -9.45258000,
				88.43956100, -13.84398000 );
		gp.curveTo( 112.37247000, -68.86008000, 88.96145000, -127.14130000,
				88.43956100, -183.84396000 );
		gp.lineTo( 88.43956100, -183.84396000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}

	private void drawPawn( Graphics2D g, int tx, int ty )
	{
		g.translate( tx, ty );
		GeneralPath gp = new GeneralPath();
		gp.moveTo( 99.93750000, -199.93750000 );
		gp.curveTo( 89.99999500, -199.87577637, 85.00000000, -190.00000000,
				85.00000000, -130.00000000 );
		gp.curveTo( 85.00000000, -114.94955000, 84.94598000, -92.41163000,
				85.00000000, -80.00000000 );
		gp.curveTo( 80.73601700, -60.22102000, 55.00000000, -43.15323000,
				55.00000000, -30.00000000 );
		gp.curveTo( 55.00000000, -13.43146000, 75.00000000, 0.00000000,
				100.00000000, 0.00000000 );
		gp.curveTo( 125.00000000, 0.00000000, 145.00000000, -13.43146000,
				145.00000000, -30.00000000 );
		gp.curveTo( 145.00000000, -43.17832000, 118.59686000, -59.86603000,
				115.00000000, -80.00000000 );
		gp.curveTo( 114.50342000, -92.78549000, 115.00000000, -114.96123000,
				115.00000000, -130.00000000 );
		gp.curveTo( 115.00000000, -190.00000000, 110.00000000, -200.00000000,
				99.93750000, -199.93750000 );
		gp.lineTo( 99.93750000, -199.93750000 );
		gp.closePath();
		g.draw( gp );
		g.translate( -tx, -ty );
	}
}
