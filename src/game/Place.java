package game;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.List;

public interface Place
{
	public void connectTo( Place neighbour );
	/**************************************************************************
	 * Mark places where the player can move to through propagation, so that
	 * the UI can show valid moves and the user can click on those places and
	 * move to them. Already marked places should not be visited again and
	 * "moves" should be decremented before forwarding to connecting places.
	 * Marking stops after a room is reached or "moves" == 0
	 *************************************************************************/
	public List<Place> mark( int moves, boolean first );
	/**************************************************************************
	 * Remove the mark after player has selected a move
	 *************************************************************************/
	public void unmark( int moves );
	/**************************************************************************
	 * moves the player one place closer to the target and returns the new
	 * place of the player. This will not necessarily indicate the end of
	 * moving or end of turn. check if returned place equals target and if
	 * the new place is
	 *************************************************************************/
	public Place movePlayer( Place target );

	public boolean contains( Point2D mouseClick );
	
	public void setOccupied( boolean taken );

	public Point getLocation();

	public Shape getArea();
}
