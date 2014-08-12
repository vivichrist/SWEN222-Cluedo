import java.awt.Point;


public interface Place
{
	// query whether is nearer to the destination a player wishes to travel
	abstract boolean closer( Point target, Point source );
}
