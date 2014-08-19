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
	 * moves the player one place closer to the @param target and @return(s) the 
	 * new place of the player. This will not necessarily indicate the end of
	 * moving or end of turn. check if returned place equals target and if
	 * the new place is
	 *************************************************************************/
	public Place movePlayer( Place target );

	/**
	 * @param mouseClick : place class checks the bounds of it's total area
	 * @return true if user clicked on this Place on the board display
	 */
	public boolean contains( Point2D mouseClick );
	
	/**
	 * @param taken : this Place will be sensed as occupied. Square cares about
	 * this but Room does not.
	 */
	public void setOccupied( boolean taken );

	/**
	 * @return the current position relative to the array of tiles in the board.
	 */
	public Point getLocation();

	/**
	 * @return the area relative to position, taken up on the canvas by this
	 * Place.
	 */
	public Shape getArea();
}
