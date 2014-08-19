package identities;

import game.Player;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

public enum Cards
{
	MISSSCARLETT( "Miss Scarlett", Types.CHARACTERS ) // Characters
	, COLONELMUSTARD( "Colonel Mustard", Types.CHARACTERS )
	, MRSWHITE( "Mrs. White", Types.CHARACTERS )
	, THEREVERENDGREEN( "The Reverend Green", Types.CHARACTERS )
	, MRSPEACOCK( "Mrs. Peacock", Types.CHARACTERS )
	, PROFESSORPLUM( "Professor Plum", Types.CHARACTERS )
	, CANDLESTICK( "Candlestick", Types.WEAPONS ) // Weapons
	, DAGGER( "Dagger", Types.WEAPONS )
	, LEADPIPE( "Lead Pipe", Types.WEAPONS )
	, REVOLVER( "Revolver", Types.WEAPONS )
	, ROPE( "Rope", Types.WEAPONS )
	, SPANNER( "Spanner", Types.WEAPONS )
	, KITCHEN( "Kitchen", Types.ROOMS ) // Rooms
	, BALLROOM( "Ball Room", Types.ROOMS )
	, CONSERVATORY( "Conservatory", Types.ROOMS )
	, DININGROOM( "Dining Room", Types.ROOMS )
	, BILLIARDROOM( "Billiard Room", Types.ROOMS )
	, LIBRARY( "Library", Types.ROOMS )
	, LOUNGE( "Lounge", Types.ROOMS )
	, HALL( "Hall", Types.ROOMS )
	, STUDY( "Study", Types.ROOMS )
	, HIDDEN( "Unknown", Types.NONE );
	public static enum Types
	{
		CHARACTERS, WEAPONS, ROOMS, NONE;
	}
	public static final Random rand = new Random( System.currentTimeMillis() );
	private String name;
	private Types type;
	private List<Player> visibility = new LinkedList<Player>();

	public static final Image ballroom = loadImage("ballroom.png");
	public static final Image billiardroom = loadImage("billiard room.png");
	public static final Image candlestick = loadImage("candlestick.png");
	public static final Image colonelMustard = loadImage("colonelmustard.png");
	public static final Image conservatory = loadImage("conservatory");
	public static final Image diningroom = loadImage("dinning room.png");
	public static final Image dagger = loadImage("dragger.png");
	public static final Image hall = loadImage("hall.png");
	public static final Image kitchen = loadImage("kitchen.png");
	public static final Image leadPipe = loadImage("leadpipe.png");
	public static final Image library = loadImage("library.png");
	public static final Image lounge = loadImage("lounge.png");
	public static final Image missScarlett = loadImage("missscarlett.png");
	public static final Image mrsPeacock = loadImage("mrspeacock.png");
	public static final Image mrsWhite = loadImage("mrswhite.png");
	public static final Image professorPlum = loadImage("professorplum.png");
	public static final Image revGreen = loadImage("revgreen.png");
	public static final Image rope = loadImage("rope.png");
	public static final Image rovolver = loadImage("revolver.png");
	public static final Image spanner = loadImage("spanner.png");
	public static final Image study = loadImage("study.png");

	private static Image loadImage( String str )
	{
		BufferedImage img = null;
		try {
		    img = ImageIO.read(new File(str));
		} catch (IOException e) {
		}
		return img;
	}

	Cards( String value, Types t )
	{
		this.name = value;
		this.type = t;
	}
	public static Cards roomID( char c )
	{
		switch ( c )
		{
		case 'K':
			return KITCHEN;
		case 'B':
			return BILLIARDROOM;
		case 'A':
			return BALLROOM;
		case 'C':
			return CONSERVATORY;
		case 'L':
			return LOUNGE;
		case 'H':
			return HALL;
		case 'U':
			return STUDY;
		case 'N':
			return DININGROOM;
		case 'I':
			return LIBRARY;
		}
		return HIDDEN;

	}
	public static List<Cards> shuffleCards()
	{
		LinkedList<Cards> cards = new LinkedList<Cards>();
		for ( Cards c: Cards.values() )
		{
			cards.add( c );
		}
		Collections.shuffle( cards, rand );
		// System.out.println( cards );
		return cards;
	}

	public static List<Cards> generateSolution( List<Cards> cards )
	{
		LinkedList<Cards> solution = new LinkedList<Cards>();
		boolean same;
		while ( solution.size() < 3 )
		{
			Cards c = cards.get( rand.nextInt( 21 - solution.size() ) );
			Types t = c.type;
			if ( t == Types.NONE ) continue;
			same = false;
			for ( Cards s: solution )
				if ( s.type == t ) same = true;
			if ( !same )
			{
				solution.add( c );
				cards.remove( c );
			}
		}
		return solution;
	}

	public static List<Cards> getAll( Types t )
	{
		LinkedList<Cards> cards = new LinkedList<Cards>();
		for ( Cards c: Cards.values() )
		{
			if ( c.type == t ) cards.add( c );
		}
		return cards;
	}

	public String toString()
	{
		return this.name;
	}

	public boolean addVisibility( Player player )
	{
		return visibility.add( player );
	}
	public String see( Player player )
	{
		if ( ( visibility.contains( player ) ) ) return name;
		return "Unknown";
	}
	public Types getType( Player player )
	{
		if ( ( visibility.contains( player ) ) ) return type;
		return Types.NONE;
	}

	public Image imageName ( Cards c)
	{
		switch ( c )
		{
		case BALLROOM :	return ballroom;
		case BILLIARDROOM : return billiardroom;
		case CANDLESTICK : return candlestick;
		case COLONELMUSTARD : return colonelMustard;
		case CONSERVATORY : return conservatory;
		case DAGGER : return dagger;
		case DININGROOM : return diningroom;
		case HALL : return hall;
		case KITCHEN : return kitchen;
		case LEADPIPE : return leadPipe;
		case LIBRARY : return library;
		case LOUNGE : return lounge;
		case MISSSCARLETT : return missScarlett;
		case MRSPEACOCK : return mrsPeacock;
		case MRSWHITE : return mrsWhite;
		case PROFESSORPLUM : return professorPlum;
		case REVOLVER : return rovolver;
		case ROPE : return rope;
		case SPANNER : return spanner;
		case STUDY : return study;
		case THEREVERENDGREEN : return revGreen;
		case HIDDEN :
			break;
		default :
			break;
		}
		return null;
	}
}





