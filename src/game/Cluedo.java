package game;

import identities.Cards;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import ui.CardListener;
import ui.GameListener;
import ui.RollListener;

/**
 * @author Vivian Stewart
 * The main game logic is found here and triggered by menu selections and mouse
 * clicks received from the active player.  
 */
public class Cluedo implements ActionListener, PlayerListener
{
	private ArrayList<Player> players;
	private int currentPlayer = 0;
	private int numPlayers = 0;
	private boolean clickwait = false;
	// private CluedoBoard board;
	private List<Cards> solution;
	private List<Place> places = null;
	private RollListener rolls;
	private CardListener detective;
	private GameListener playerUpdates;
	/**
	 * @param rooms : data representation and list of all rooms in the game
	 * @param squares : all corridor squares
	 * @param playerUpdates : for updating the board display
	 * @param rolls : for updating the display of dice and cards 
	 * @param detective 
	 * @param menu : listening to action commands from the frame menu
	 */
	public Cluedo( List<Room> rooms, List<Square> squares
			, GameListener playerUpdates, RollListener rolls
			, CardListener detective, List<JMenuItem> menu )
	{
		this.rolls = rolls;
		this.detective = detective;
		this.playerUpdates = playerUpdates;
		for ( JMenuItem m: menu )
			m.addActionListener(this);
	}
	/**************************************************************************
	 * Selects the number of players (3-6) and asks each player which of the
	 * six possible suspects they wish to play as and creates them
	 *************************************************************************/
	public boolean start()
	{

		Integer[] possibleValues = { 3, 4, 5, 6 };
		Object o = JOptionPane.showInputDialog(null, "Number of Players",
				"Please select the number of players to play Cluedo",
				JOptionPane.INFORMATION_MESSAGE, null,
				possibleValues, possibleValues[0]);
		if ( o == null ) return false; // cancel
		numPlayers = (Integer) o;
		Cards[] selectedValues = new Cards[ numPlayers ];
		List<Cards> pawns = Cards.getAll( Cards.Types.CHARACTERS );
		Cards[] pawnsArray = new Cards[6];
		pawns.toArray(pawnsArray);
		for ( int i = 0; i < numPlayers; ++i )
		{	selectedValues[i] = (Cards)JOptionPane.showInputDialog( null,
									"Pick a Pawn", "Possible Characters",
									JOptionPane.INFORMATION_MESSAGE, null,
									pawnsArray, pawnsArray[0] );
			if ( selectedValues[i] == null ) return false; // cancel
			pawns.remove(selectedValues[i]);
			pawnsArray = new Cards[6 - i];
			pawns.toArray(pawnsArray);
		}
		// Shuffle deck
		List<Cards> cards = Cards.shuffleCards();
		solution = Cards.generateSolution( cards );
		LinkedList<LinkedList<Cards>> splits = new LinkedList<LinkedList<Cards>>();
		for ( int i = 0; i < numPlayers; ++i )
			splits.add( new LinkedList<Cards>() );
		while ( !cards.isEmpty() )
		{
			for ( LinkedList<Cards> lc: splits)
			{
				if ( cards.isEmpty() ) break;
				lc.add( cards.remove(0) );
			}
		}
		// deal cards to players and put them on the board
		players = playerUpdates.initPlayers( new LinkedList<Cards>( Arrays.asList( selectedValues ) )
											, this, splits );
		currentPlayer = 0;
		Player p = players.get(currentPlayer);
		p.setActive(true);
		rolls.cards( p.playerCards(), p.cardID() );
		detective.cards( getOthersCards( p ), p.cardID() );
		return true;
	}
	
	/**
	 * @param player : the player to be moved
	 * @param menu : to be returned and updated for new options,
	 * by the player class through the PlayerListener interface.
	 */
	private void move( Player player, JMenu menu )
	{
		int dice1 = ( Cards.rand.nextInt( 6 ) + 1 );
		int dice2 = ( Cards.rand.nextInt( 6 ) + 1 );
		rolls.diceRool( dice1, dice2 );
		places = player.canMove( dice1 + dice2 + 1, menu );
		clickwait = true;
	}
	/**
	 * The player skips dice roll and opts for the passage between diagonally
	 * adjacent rooms.
	 * @param player : the current player
	 */
	public void takePassage( Player player )
	{
		if ( player.roomHasPassage() ) player.moveMe( null ); // null means move through passage
	}
	/**
	 * @param player : the player making the accusation
	 */
	private byte makeAccusation( Player player )
	{
		List<Cards> susp = Cards.getAll( Cards.Types.CHARACTERS );
		List<Cards> weapons = Cards.getAll( Cards.Types.WEAPONS );
		List<Cards> rooms = Cards.getAll( Cards.Types.ROOMS );
		Cards[] choices = new Cards[6];
		susp.toArray(choices);
		Cards suspect = (Cards)JOptionPane.showInputDialog(null, "Which Suspect?",
				"Please select the Suspect\nyou wish to Accuse.",
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );
		if ( suspect == null ) return 0;
		weapons.toArray(choices);
		Cards weapon = (Cards)JOptionPane.showInputDialog(null
				, "Please select the Weapon they\nused to kill the victim."
				, "Which Weapon?"
				, JOptionPane.INFORMATION_MESSAGE, null
				, choices, choices[0] );
		if ( weapon == null ) return 0;
		choices = new Cards[9];
		rooms.toArray(choices);
		Cards room = (Cards)JOptionPane.showInputDialog(null
				, "Please select the Room the\n murder took place in."
				, "Which Room?"
				,
				JOptionPane.INFORMATION_MESSAGE, null,
				choices, choices[0] );
		if ( room == null ) return 0;
		if ( solution.contains(suspect) && solution.contains(weapon) && solution.contains(room) )
		{
			JOptionPane.showMessageDialog( null, "You WIN!!", "You WIN!!", JOptionPane.PLAIN_MESSAGE );
			return 1;
		}
		player.setPlaying( false );
		boolean restart = false;
		for ( Player p: players ) 
			restart |= p.isPlaying();
		if ( !restart ) // everybody made false accusation
		{
			JOptionPane.showMessageDialog( null, "No One Wins!", "You All Lose!!", JOptionPane.PLAIN_MESSAGE );
			return 1;
		}
		return 2;
	}
	/**
	 * After the current player has moved they can make a suggestion
	 * @param player : current player
	 */
	private boolean makeSuggestion( Player player )
	{
		List<Cards> susp = Cards.getAll( Cards.Types.CHARACTERS );
		List<Cards> weapons = Cards.getAll( Cards.Types.WEAPONS );
		List<Cards> rooms = Cards.getAll( Cards.Types.ROOMS );

		Cards[] choices = new Cards[6];
		susp.toArray(choices);
		Cards suspect = (Cards)JOptionPane.showInputDialog( null
				, "Please select the Suspect you wish to Suggest."
				, "Which Suspect?"
				, JOptionPane.INFORMATION_MESSAGE, null
				, choices, choices[0] );
		if ( suspect == null ) return false;
		weapons.toArray(choices);
		Cards weapon = (Cards)JOptionPane.showInputDialog(null
				, "Please select the Weapon they mite\nhave used to kill the victim."
				, "Which Weapon?"
				, JOptionPane.INFORMATION_MESSAGE, null
				, choices, choices[0] );
		if ( weapon == null ) return false;
		choices = new Cards[9];
		rooms.toArray(choices);
		Cards room = ((Room)player.getLocation()).id;
		List<Cards> ocards = new LinkedList<Cards>();
		boolean refute = false;
		for ( Player pl: players )
		{
			if ( pl.haveCard( suspect ) )
				ocards.add( suspect );
			if ( pl.haveCard( weapon ) )
				ocards.add( weapon );
			if ( pl.haveCard( room ) )
				ocards.add( room );
			if ( ocards.isEmpty() ) continue;
			refute = true;
			choices = new Cards[ocards.size()];
			ocards.toArray(choices);
			Cards card = (Cards)JOptionPane.showInputDialog( null
					, "Please select the Card you, " + pl.id() + "\nwish to Show, " + player.id()
					, "Which Card to Show?"
					, JOptionPane.PLAIN_MESSAGE, null
					, choices, choices[0] );
			if ( card != null ) card.addVisibility( player.cardID() );
			break;
		}
		if ( refute == false )
			JOptionPane.showMessageDialog( null
					, "There is no refuting evidence!", "Hmmm...."
					, JOptionPane.PLAIN_MESSAGE );
		detective.cards( getOthersCards( player ), player.cardID() );
		return true;
	}

	/**
	 * Move on to the next players turn and skip over players that have been
	 * disqualified for false accusation.
	 */
	private Player endTurn( Player player )
	{
		player.setActive(false);
		do
		{
			++currentPlayer;
			if ( currentPlayer == numPlayers )
			{
				currentPlayer = 0; // reset to first player
				player = players.get( currentPlayer );
				player.setActive(true);
				rolls.cards( player.playerCards(), player.cardID() );
				detective.cards( getOthersCards( player ), player.cardID() );
				return player;
			} else
				player = players.get(currentPlayer);
		} while ( !player.isPlaying() );
		player.setActive(true);
		rolls.cards( player.playerCards(), player.cardID() );
		detective.cards( getOthersCards( player ), player.cardID() );
		return player;
	}

	/**
	 * @param p : get all cards other than the cards the current player has
	 * @return each players cards excluding the current player
	 */
	private HashMap<Cards, List<Cards>> getOthersCards( Player p )
	{
		HashMap<Cards, List<Cards>> others = new HashMap<Cards, List<Cards>>();
		for ( Player pl: players )
		{
			if ( pl != p ) others.put( pl.cardID(), pl.playerCards() );
		}
		return others;
	}

	/**
	 * @author Vivian Stewart
	 * Order of menu items for user selection and just makes it a bit easier to
	 * read the menu code.
	 */
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
	
	/**
	 * @param menu : Grays out all of the menu items in this menu
	 */
	private void disableMenuItems( JMenu menu )
	{
		int limit = menu.getItemCount();
		for ( int i = 0; i < limit; ++i )
			menu.getItem( i ).setEnabled(false);
	}

	@Override
	public void actionPerformed( ActionEvent e )
	{
		// obtaining a menu is ridiculous 
		JMenu menu = (JMenu)((JPopupMenu)((JMenuItem)e.getSource()).getParent()).getInvoker();
		Player p;
		System.out.println( menu.getText() );
		if ( e.getActionCommand() == MenuIndex.START.name )
		{
			if ( start() )
			{
				disableMenuItems( menu );
				menu.getItem( MenuIndex.DICE.ordinal() ).setEnabled(true);
				menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
			}
		}
		else if ( e.getActionCommand() == MenuIndex.DICE.name )
		{
			p = players.get(currentPlayer);
			disableMenuItems(menu);
			// menu comes back when clickOption returns a valid click 
			move( p, menu );
		}
		else if ( e.getActionCommand() == MenuIndex.PASSAGE.name )
		{
			p = players.get(currentPlayer);
			takePassage( p );
			disableMenuItems(menu);
			menu.getItem( MenuIndex.SUGGEST.ordinal() ).setEnabled(true);
			menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
			
		}
		else if ( e.getActionCommand() == MenuIndex.ACCUSE.name )
		{
			p = players.get(currentPlayer);
			byte option = makeAccusation( p );
			if ( option == 0 ) return; // cancelled
			disableMenuItems(menu);
			if ( option == 1 ) // win or no winner
			{
				menu.getItem( MenuIndex.START.ordinal() ).setEnabled(true);
				return;
			} // 2 false accusation
			menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
		}
		else if ( e.getActionCommand() == MenuIndex.SUGGEST.name )
		{
			p = players.get(currentPlayer);
			if ( makeSuggestion( p ) ) // false if cancelled
			{
				disableMenuItems(menu);
				menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
				menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
			}
		}
		else if ( e.getActionCommand() == MenuIndex.END.name )
		{
			p = players.get(currentPlayer);
			p = endTurn( p );
			disableMenuItems( menu );
			menu.getItem( MenuIndex.DICE.ordinal() ).setEnabled(true);
			if ( p.roomHasPassage() )
				menu.getItem(MenuIndex.PASSAGE.ordinal()).setEnabled(true);
			menu.getItem( MenuIndex.ACCUSE.ordinal() ).setEnabled(true);
		}
	}
	/* One player is activated during which, the player clicks on the board,
	 * triggering the clickOption callback, to select a highlighted area to
	 * move to and then animated to that point. This method is called from the
	 * Player Class that implements MouseListener.
	 * @see game.PlayerListener#clickedOption(int, int, javax.swing.JMenu)
	 */
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
				JOptionPane.showMessageDialog( null
						, "Please Click inside a highlighted place on the board"
						, "Can Not Move There!"
						, JOptionPane.PLAIN_MESSAGE );
				return;
			}
			Player p = players.get(currentPlayer);
			if ( target instanceof Room )
			{
				Room r = (Room) target;
				Place nTarget = r.nearestNeighbour( target.getLocation() );
				while ( p.moveMe( nTarget ));
				p.moveMe( target );
			} else 
				while ( p.moveMe( target ));
			clickwait = false;
			// this stops a user from using the menu while selecting a move with mouse 
			if ( p.isInARoom() )
				menu.getItem( MenuIndex.SUGGEST.ordinal() ).setEnabled(true);
			menu.getItem( MenuIndex.END.ordinal() ).setEnabled(true);
		}
	}
}
