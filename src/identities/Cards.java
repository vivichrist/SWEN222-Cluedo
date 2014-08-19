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
	, KITCHEN( "Kitchen", Types.WEAPONS ) // Rooms
	, BALLROOM( "Ball Room", Types.WEAPONS )
	, CONSERVATORY( "Conservatory", Types.WEAPONS )
	, DININGROOM( "Dining Room", Types.WEAPONS )
	, BILLIARDROOM( "Billiard Room", Types.WEAPONS )
	, LIBRARY( "Library", Types.WEAPONS )
	, LOUNGE( "Lounge", Types.WEAPONS )
	, HALL( "Hall", Types.WEAPONS )
	, STUDY( "Study", Types.WEAPONS )
	, HIDDEN( "Unknown", Types.NONE );
	public static enum Types
	{
		CHARACTERS, WEAPONS, ROOMS, NONE;
	}
	public static final Random rand = new Random( System.currentTimeMillis() );
	private String name;
	private Types type;
	private List<Player> visibility = new LinkedList<Player>();

	private Image ballroom = loadImage("ballroom.png");
	private Image billiardroom = loadImage("billiard room.png");
	private Image candlestick = loadImage("candlestick.png");
	private Image colonelMustard = loadImage("colonel mustard.png");
	private Image conservatory = loadImage("conservatory");
	private Image diningroom = loadImage("dinning room.png");
	private Image dagger = loadImage("dragger.png");
	private Image hall = loadImage("hall.png");
	private Image kitchen = loadImage("kitchen.png");
	private Image leadPipe = loadImage("lead pipe.png");
	private Image library = loadImage("library.png");
	private Image lounge = loadImage("lounge.png");
	private Image missScarlett = loadImage("miss scarlett.png");
	private Image mrsPeacock = loadImage("mrs peacocl.png");
	private Image mrsWhite = loadImage("mrs white.png");
	private Image professorPlum = loadImage("professor plum.png");
	private Image revGreen = loadImage("rev green.png");
	private Image rope = loadImage("rope.png");
	private Image rovolver = loadImage("revolver.png");
	private Image spanner = loadImage("spanner.png");
	private Image study = loadImage("study.png");

	private Image loadImage( String str )
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

		while ( solution.size() != 3 )
		{
			Cards c = cards.get( rand.nextInt( 21 - solution.size() ) );
			Types t = c.type;
			if ( t == Types.NONE ) continue;
			for ( Cards s: solution )
				if ( s.type == t ) continue;
			solution.add( c );
			cards.remove( c );
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

	public Image imageName ( Cards c){
		if( c.equals(BALLROOM)){
			return ballroom;
		}
		else if ( c.equals(BILLIARDROOM)){
			return billiardroom;
		}
		else if ( c.equals(CANDLESTICK)){
			return candlestick;
		}
		else if ( c.equals(COLONELMUSTARD)){
			return colonelMustard;
		}
		else if (c.equals(CONSERVATORY)){
			return conservatory;
		}
		else if (c.equals(DAGGER)){
			return dagger;
		}
		else if ( c.equals(DININGROOM)){
			return diningroom;
		}
		else if (c.equals(HALL)){
			return hall;
		}
		else if (c.equals(KITCHEN)){
			return kitchen;
		}
		else if (c.equals(LEADPIPE)){
			return leadPipe;
		}
		else if (c.equals(LIBRARY)){
			return library;
		}
		else if (c.equals(LOUNGE)){
			return lounge;
		}
		else if (c.equals(MISSSCARLETT)){
			return missScarlett;
		}
		else if (c.equals(MRSPEACOCK)){
			return mrsPeacock;
		}
		else if (c.equals(MRSWHITE)){
			return mrsWhite;
		}
		else if (c.equals(PROFESSORPLUM)){
			return professorPlum;
		}
		else if (c.equals(REVOLVER)){
			return rovolver;
		}
		else if (c.equals(ROPE)){
			return rope;
		}
		else if (c.equals(SPANNER)){
			return spanner;
		}
		else if (c.equals(STUDY)){
			return study;
		}
		else{
			return revGreen;
		}
	}
}





