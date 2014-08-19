package game;

import identities.Cards;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;


public class Room implements Place
{
	public final Cards id;
	public final Point location;
	private int move = 0;
	private List<Place> exits;
	private Place passage = null;
	private Area area;

	public Room( int x, int y, Cards id, Area area )
	{
		this.id = id;
		this.area = area;
		location = new Point( x, y );
		exits = new LinkedList<Place>();
	}

	@Override
	public List<Place> mark( int moves, boolean first )
	{
		LinkedList<Place> places = new LinkedList<Place>();
		if ( move >= moves ) return places;
		if ( !first ) places.add(this);
		else
		{	move = moves;
			if ( moves > 0 ) // this may already be true
			{
				for ( Place p: exits)
				{
					places.addAll( p.mark( moves - 1, false ) );
				}
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
			for ( Place p: exits)
			{
				p.unmark( moves - 1 );
			}
		}
	}

	@Override
	public Place movePlayer( Place target )
	{
		if ( target == null )
		{
			if ( passage != null ) return passage;
			else return this;
		}
		Point p = target.getLocation();
		Place result = this;
		double least = Double.POSITIVE_INFINITY;
		for ( Place e: exits )
		{
			double dist = p.distance( e.getLocation() );
			if ( dist < least )
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

	public boolean hasPassage()
	{
		return passage != null;
	}

	@Override
	public void connectTo( Place neighbour )
	{
		if ( neighbour instanceof Room )
		{
			passage = neighbour;
			System.out.println( "ConnectPassage:" + this + "->" + neighbour + " " );
		} else if ( !exits.contains( neighbour ) )
		{
			exits.add( neighbour );
			System.out.println( "Connect:" + this + "->" + neighbour + " " );
		}

	}

	public Place nearestNeighbour( Point point )
	{
		double dist = Double.POSITIVE_INFINITY;
		Place result = null;
		for ( Place p: exits )
		{
			double test = p.getLocation().distance( point.x, point.y );
			if ( test < dist )
			{
				dist = test;
				result = p;
			}
		}
		return result;
	}

	@Override
	public String toString()
	{
		return "Room [id=" + id + ", location=" + location + "]";
	}

	@Override
	public boolean contains( Point2D mouseClick )
	{
		return area.contains( mouseClick );
	}

	@Override
	public Shape getArea()
	{
		return new Area( area );
	}

}
