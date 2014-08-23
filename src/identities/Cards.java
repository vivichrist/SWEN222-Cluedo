package identities;

import game.Player;

import java.awt.Image;
import java.beans.Visibility;
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
	private List<Cards> visibility = new LinkedList<Cards>();

	public static final Image ballroom = loadImage("ballroom.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image billiardroom = loadImage("billiardroom.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image candlestick = loadImage("candlestick.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image colonelMustard = loadImage("colonelmustard.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image conservatory = loadImage("conservatory.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image diningroom = loadImage("dinningroom.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image dagger = loadImage("dragger.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image hall = loadImage("hall.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image kitchen = loadImage("kitchen.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image leadPipe = loadImage("leadpipe.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image library = loadImage("library.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image lounge = loadImage("lounge.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image missScarlett = loadImage("missscarlett.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image mrsPeacock = loadImage("mrspeacock.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image mrsWhite = loadImage("mrswhite.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image professorPlum = loadImage("professorplum.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image revGreen = loadImage("revgreen.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image rope = loadImage("rope.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image rovolver = loadImage("revolver.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image spanner = loadImage("spanner.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image study = loadImage("study.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image hidden = loadImage("hidden.jpg")
			.getScaledInstance( 50, 70, Image.SCALE_AREA_AVERAGING );
	public static final Image zero = loadImage("zero.png");
	public static final Image one = loadImage("one.png");
	public static final Image two = loadImage("two.png");
	public static final Image three = loadImage("three.png");
	public static final Image four = loadImage("four.png");
	public static final Image five = loadImage("five.png");
	public static final Image six = loadImage("six.png");

	public static Image loadImage(String filename) {

		try {
			Image img = ImageIO.read( new File( filename ) );
			return img;
		} catch (IOException e) {
			// we've encountered an error loading the image. There's not much we
			// can actually do at this point, except to abort the game.
			throw new RuntimeException("Unable to load image: " + filename);
		}
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
			c.visibility.clear();
			if ( !c.equals( HIDDEN ) ) cards.add( c );
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
	
	public Cards visible ( Cards player )
	{
		if ( ( visibility.contains( player ) ) ) return this;
		return Cards.HIDDEN;
	}

	public boolean addVisibility( Cards player )
	{
		return visibility.add( player );
	}
	public String see( Cards player )
	{
		if ( ( visibility.contains( player ) ) ) return name;
		return "Unknown";
	}
	public Types getType( Cards player )
	{
		if ( ( visibility.contains( player ) ) ) return type;
		return Types.NONE;
	}
	
	public static Image convertInt( int num )
	{
		switch (num)
		{
		case 1 :
			return one;
		case 2 :
			return two;
		case 3 :
			return three;
		case 4 :
			return four;
		case 5 :
			return five;
		case 6 :
			return six;
		}
		return zero;
	}

	public static Image imageFromCard ( Cards c)
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
		case HIDDEN : return hidden;
		}
		return hidden;
	}
}





