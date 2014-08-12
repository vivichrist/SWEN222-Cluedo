package game;

import java.awt.Point;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Square implements Place
{
	public final Point location; // centre
	public final Place[] adjacent;
	private Player occupant = null;
	private boolean move = false;

	public Point getLocation()
	{
		return location;
	}
	public Square( int x, int y, Place[] places )
	{
		location = new Point(x, y);
		adjacent = Arrays.copyOf( places, places.length );
	}
	@Override
	public List<Place> mark( int moves )
	{
		LinkedList<Place> places = new LinkedList<Place>();
		if (occupant != null || move) return places;
		move = !move;
		if ( moves > 0 )
		{
			for ( Place p: adjacent)
			{
				places.addAll( p.mark( moves - 1 ) );
			}
		}
		return places;
	}
	@Override
	public void unmark( int moves )
	{
		if ( moves > 0 )
		{
			for ( Place p: adjacent)
			{
				p.unmark( moves - 1 );
			}
		}
		move = false;
	}
	@Override
	public Place movePlayer( Place target )
	{
		Point nextLoc, loc = target.getLocation();
		double d, dist = Double.POSITIVE_INFINITY;
		Place result = this;
		for ( Place p: adjacent)
		{ // find the adjacent (connected) place closest to the target.
			// if the target and p are rooms then target is p
			// i.e we don't move through rooms to get to a room
			if ( p instanceof Room && target != p )
				continue;
			nextLoc = p.getLocation();
			d = nextLoc.distance( loc );
			if ( dist > d )
			{
				dist = d;
				result = p;
			}
		}
		return result;
	}
}
