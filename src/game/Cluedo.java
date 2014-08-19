package game;

import identities.Cards;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import ui.PlayerListener;
import ui.RollListener;

public class Cluedo implements ActionListener, GameListener
{
	private ArrayList<Player> players;
	private int currentPlayer = 0;
	private int numPlayers = 0;
	private boolean clickwait = false;
	// private CluedoBoard board;
	private List<Cards> solution;
	private List<Place> places = null;
	private RollListener rolls;
	private PlayerListener playerUpdates;
	public Cluedo( List<Room> rooms, List<Square> squares
			, PlayerListener playerUpdates, RollListener rolls
			, List<JMenuItem> menu)
	{
		this.rolls = rolls;
		this.playerUpdates = playerUpdates;
		for ( JMenuItem m: menu )
			m.addActionListener(this);
	}
	/**************************************************************************
	 * Selects the number of players (3-6) and asks each player which of the
	 * six possible suspects they wish to play as and creates them
	 *************************************************************************/
	public void start()
	{

		Integer[] possibleValues = { 3, 4, 5, 6 };
		numPlayers = (Integer) JOptionPane.showInputDialog(null, "Number of Players",
				"Please select the number of players to play Cluedo",
				JOptionPane.INFORMATION_MESSAGE, null,
				possibleValues, possibleValues[0]);
		Cards[] selectedValues = new Cards[ numPlayers ];
		@SuppressWarnings( "serial" )
		List<Cards> pawns = new LinkedList<Cards>(){{
			add(Cards.COLONELMUSTARD);
			add(Cards.MISSSCARLETT);
			add(Cards.MRSPEACOCK);
			add(Cards.MRSWHITE);
			add(Cards.PROFESSORPLUM);
			add(Cards.THEREVERENDGREEN);
			}};
		Cards[] pawnsArray = new Cards[6];
		pawns.toArray(pawnsArray);
		for ( int i = 0; i < numPlayers; ++i )
		{	selectedValues[i] = (Cards)JOptionPane.showInputDialog( null,
									"Pick a Pawn", "Possible Characters",
									JOptionPane.INFORMATION_MESSAGE, null,
									pawnsArray, pawnsArray[0] );
			pawns.remove(selectedValues[i]);
			pawnsArray = new Cards[6-i];
			pawns.toArray(pawnsArray);
		}
		// Shuffle deck
		List<Cards> cards = Cards.shuffleCards();
		solution = Cards.generateSolution( cards );
		LinkedList<LinkedList<Cards>> splits = new LinkedList<LinkedList<Cards>>();
		for ( int i = 0; i < numPlayers; ++i )
			splits. add( new LinkedList<Cards>() );
		while ( !cards.isEmpty() )
		{
			for ( LinkedList<Cards> lc: splits)
			{
				if ( cards.isEmpty() ) break;
				lc.add( cards.remove(0) );
			}
		}
		System.out.println( splits );
		players = playerUpdates.initPlayers( new LinkedList<Cards>( Arrays.asList( selectedValues ) )
											, this, splits );
		currentPlayer = 0;
		players.get(currentPlayer).setActive(true);
		rolls.cards( players.get(currentPlayer).playerCards(), players.get(currentPlayer).cardID() );
	}

	private void makeSuggestion( Player player )
	{
		List<Cards> susp = Cards.getAll( Cards.Types.CHARACTERS );
		List<Cards> weapons = Cards.getAll( Cards.Types.WEAPONS );
		List<Cards> rooms = Cards.getAll( Cards.Types.ROOMS );

		Cards[] choices = new Cards[6];
		susp.toArray(choices);
		Cards suspect = (Cards)JOptionPane.showInputDialog(null, "Which Suspect?",
				"Please select the Suspect you wish to Suggest.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );

		weapons.toArray(choices);
		Cards weapon = (Cards)JOptionPane.showInputDialog(null, "Which Weapon?",
				"Please select the Weapon they mite have used to kill the victim.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );

		choices = new Cards[9];
		rooms.toArray(choices);
		Cards room = (Cards)JOptionPane.showInputDialog(null, "Which Room?",
				"Please select the Room the murder could have taken place in.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );
		for ( Player pl: players )
		{
			if ( pl.haveCard( suspect ) )
			{
				suspect.addVisibility( player );
				player.addCard( suspect );
			}
			if ( pl.haveCard( weapon ) )
			{
				weapon.addVisibility( player );
				player.addCard( weapon );
			}
			if ( pl.haveCard( room ) )
			{
				room.addVisibility( player );
				player.addCard( room );
			}
		}
	}

	public void takePassage( Player player )
	{
		if ( player.roomHasPassage() ) player.moveMe( null );
	}

	private void newTurn()
	{
		currentPlayer = 0;
		players.get(currentPlayer).setActive(true);
		rolls.cards( players.get(currentPlayer).playerCards(), players.get(currentPlayer).cardID() );
	}

	private void endTurn()
	{
		players.get(currentPlayer).setActive(false);
		++currentPlayer;
		if ( currentPlayer >= numPlayers )
		{
			newTurn();
			return;
		}
		players.get(currentPlayer).setActive(true);
		rolls.cards( players.get(currentPlayer).playerCards(), players.get(currentPlayer).cardID() );
	}

	private void move( Player player, JMenu menu )
	{
		int dice1 = ( Cards.rand.nextInt( 6 ) + 1 );
		int dice2 = ( Cards.rand.nextInt( 6 ) + 1 );
		rolls.message( dice1, dice2 );
		places = players.get(currentPlayer).canMove( dice1 + dice2 + 1, menu );
		clickwait = true;
	}

	private void makeAccusation( Player player )
	{
		List<Cards> susp = Cards.getAll( Cards.Types.CHARACTERS );
		List<Cards> weapons = Cards.getAll( Cards.Types.WEAPONS );
		List<Cards> rooms = Cards.getAll( Cards.Types.ROOMS );

		Cards[] choices = (Cards[])susp.toArray();
		Cards suspect = (Cards)JOptionPane.showInputDialog(null, "Which Suspect?",
				"Please select the Suspect you wish to Accuse.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );

		choices = (Cards[])weapons.toArray();
		Cards weapon = (Cards)JOptionPane.showInputDialog(null, "Which Weapon?",
				"Please select the Weapon they used to kill the victim.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );

		choices = (Cards[])rooms.toArray();
		Cards room = (Cards)JOptionPane.showInputDialog(null, "Which Room?",
				"Please select the Room the murder took place in.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );
		if ( solution.contains(suspect) && solution.contains(weapon) && solution.contains(room) )
		{
			JOptionPane.showMessageDialog( null, "You WIN!!", "You WIN!!", JOptionPane.PLAIN_MESSAGE );
			start();
			return;
		}
		player.setPlaying( false );
		endTurn();
	}

	public static enum MenuIndex {
		START("Start Cluedo"), DICE("Roll Dice and Move"),
		PASSAGE("Take Secret Passage"), ACCUSE("Accusation"),
		SUGGEST("Suggestion"), END("End Turn");
		public final String name;

		private MenuIndex( String name )
		{
			this.name = name;
		}
	}

	private void disableMenuItems( JMenu menu )
	{
		int limit = menu.getItemCount();
		for ( int i = 0; i < limit; ++i )
			menu.getItem( i ).setEnabled(false);
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{

		JMenu menu = (JMenu)((JPopupMenu)((JMenuItem)e.getSource()).getParent()).getInvoker();
		Player p;
		System.out.println( menu.getText() );
		if ( e.getActionCommand() == MenuIndex.START.name )
		{
			start();
			disableMenuItems( menu );
			menu.getItem( MenuIndex.DICE.ordinal() ).setEnabled(true);
			menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
		}
		else if ( e.getActionCommand() == MenuIndex.DICE.name )
		{
			p = players.get(currentPlayer);
			disableMenuItems(menu);
			move( p, menu );
		}
		else if ( e.getActionCommand() == MenuIndex.ACCUSE.name )
		{
			p = players.get(currentPlayer);
			disableMenuItems(menu);
			makeAccusation( p );
			menu.getItem( MenuIndex.DICE.ordinal() ).setEnabled(true);
			if ( p.roomHasPassage() )
				menu.getItem(MenuIndex.PASSAGE.ordinal()).setEnabled(true);
			menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
		}
		else if ( e.getActionCommand() == MenuIndex.SUGGEST.name )
		{
			p = players.get(currentPlayer);
			disableMenuItems(menu);
			menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
			menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
			makeSuggestion( p );
		}
		else if ( e.getActionCommand() == MenuIndex.PASSAGE.name )
		{
			p = players.get(currentPlayer);
			takePassage( p );
			disableMenuItems(menu);
			menu.getItem( MenuIndex.SUGGEST.ordinal() ).setEnabled(true);
			menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
			
		}
		else if ( e.getActionCommand() == MenuIndex.END.name )
		{
			endTurn();
			p = players.get(currentPlayer);
			disableMenuItems( menu );
			menu.getItem( MenuIndex.DICE.ordinal() ).setEnabled(true);
			if ( p.roomHasPassage() )
				menu.getItem(MenuIndex.PASSAGE.ordinal()).setEnabled(true);
			menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
			
		}
	}
	@Override
	public void clickedOption( int x, int y, JMenu menu )
	{
		if (clickwait)
		{
			Place target = null;
			for ( Place p: places )
				if ( p.getArea().contains( new Point( x, y ) ) )
					target = p;
			if ( target == null )
			{
				System.out.println("No such place: " +x+ ":" +y+ " PLace: " + target);
				return;
			}
			if ( target instanceof Room )
			{
				Room r = (Room) target;
				Place nTarget = r.nearestNeighbour( target.getLocation() );
				while ( players.get(currentPlayer).moveMe( nTarget ));
				players.get(currentPlayer).moveMe( target );
			} else while ( players.get(currentPlayer).moveMe( target ));
			clickwait = false;
			if ( players.get(currentPlayer).isInARoom() )
				menu.getItem( MenuIndex.SUGGEST.ordinal() ).setEnabled(true);
			menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
		}

	}
}
