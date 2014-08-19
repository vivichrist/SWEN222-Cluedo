package ui;

import java.awt.Point;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import game.GameListener;
import game.Place;
import game.Player;
import identities.Cards;

public interface PlayerListener
{
	public void showPossibleMoves( List<Shape> shapes);
	public void playerMoved( Cards player, Place location );
	public ArrayList<Player> initPlayers( List<Cards> players
			, GameListener game
			, LinkedList<LinkedList<Cards>> splits );
}
