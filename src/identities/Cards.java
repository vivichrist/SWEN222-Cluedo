package identities;

import game.Player;

import java.util.LinkedList;
import java.util.List;

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
	enum Types
	{
		CHARACTERS, WEAPONS, ROOMS, NONE;
	}
	private String name;
	private Types type;
	private List<Player> visibility = new LinkedList<Player>();

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
}





