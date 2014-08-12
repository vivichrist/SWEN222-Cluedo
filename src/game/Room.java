package game;

import identities.Cards;

import java.awt.Point;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;


public class Room implements Place
{
	public final Cards id;
	public final Point location;
	private boolean move = false;
	private final List<Place> exits;

	public Room( int x, int y, Cards id, List<Place> neighbours)
	{
		this.id = id;
		location = new Point( x, y );
		exits = new LinkedList<Place>(neighbours);
	}

	@Override
	public List<Place> mark( int moves )
	{
		LinkedList<Place> places = new LinkedList<Place>();
		places.add( this );
		move  = !move;
		return places;
	}

	@Override
	public void unmark( int moves )
	{
		move = false;
	}

	@Override
	public Place movePlayer( Place target )
	{
		Point p = target.getLocation();
		Place result = this;
		double least = Double.POSITIVE_INFINITY;
		for ( Place e: exits )
		{
			double dist = p.distance( e.getLocation() );
			if ( dist < least && target.getClass() == e.getClass() )
			{
				least = dist;
				result = e;
			}
		}
		return result;
	}

	@Override
	public Point getLocation()
	{
		return location;
	}

}
