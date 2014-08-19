package game;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

public class Square implements Place
{
	public final Point location; // centre
	private List<Place> adjacent;
	private Player occupant = null;
	private int move = 0;
	private Rectangle area;

	public Point getLocation()
	{
		return location;
	}
	public Square( Point p, Rectangle rect )
	{
		area = rect;
		location = p;
		adjacent = new LinkedList<Place>();
	}
	@Override
	public List<Place> mark( int moves, boolean first )
	{
		LinkedList<Place> places = new LinkedList<Place>();
		if ( occupant != null || move >= moves ) return places;
		if ( !first ) places.add(this);
		move = moves;
		if ( moves > 0 ) // this may already be true
		{
			for ( Place p: adjacent)
			{
				places.addAll( p.mark( moves - 1, false ) );
			}
		}
		return places;
	}
	@Override
	public void unmark( int moves )
	{
		if ( move == 0 ) return;
		move = 0;
		if ( moves > 0 )
		{
			for ( Place p: adjacent)
			{
				p.unmark( moves - 1 );
			}
		}
	}
	@Override
	public Place movePlayer( Place target )
	{
		Point nextLoc, loc = target.getLocation();
		double d, dist = Double.POSITIVE_INFINITY;
		Place result = this;
		for ( Place p: adjacent)
		{
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

	@Override
	public String toString()
	{
		return "Square [location=" + location + "]";
	}
	@Override
	public boolean contains( Point2D mouseClick )
	{
		return area.contains( mouseClick );
	}

	@Override
	public void connectTo( Place neighbour )
	{
		if ( !adjacent.contains(neighbour) )
		{
			adjacent.add( neighbour );
			System.out.println( "Connect:" + this + "->" + neighbour + " " );
		}
	}
	@Override
	public Shape getArea()
	{
		return new Rectangle( area );
	}
}
