/* This JFrame class is the beginnings of this program, hence it has code for
 * loading the board array from the map.txt file. Creation of the menu which
 * later attaches to each player class through MenuListener. Creation of the 
 * CluedoPanel to display the dice rolls, players cards and players name (of
 * the current player). 
 */
package ui;
import game.Cluedo;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

@SuppressWarnings( "serial" )
public class CluedoFrame extends JFrame
{
	public CluedoFrame()
	{
		super( "Cluedo" );
		char[][] board;
		CluedoBoard cluedo = null;
		setLayout( new BorderLayout() );// use border layout
		// add a menu for game decision options
		JMenuBar bar = new JMenuBar();
		JMenu menu = new JMenu("Cluedo");
		bar.add(menu);
		List<JMenuItem> mi = new LinkedList<JMenuItem>();
		// order that these are added is important!
		addMenuItem( mi, menu, Cluedo.MenuIndex.START.name, true );
		addMenuItem( mi, menu, Cluedo.MenuIndex.DICE.name, false );
		addMenuItem( mi, menu, Cluedo.MenuIndex.PASSAGE.name, false );
		addMenuItem( mi, menu, Cluedo.MenuIndex.ACCUSE.name, false );
		addMenuItem( mi, menu,  Cluedo.MenuIndex.SUGGEST.name, false );
		addMenuItem( mi, menu,  Cluedo.MenuIndex.END.name, false );
		add(bar, BorderLayout.NORTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// add a panel to display dice, cards held by player
		// and players name.
		CluedoPanel panel = new CluedoPanel();
		DetectivePanel panel2 = new DetectivePanel();

		add( panel, BorderLayout.WEST );
		add( panel2, BorderLayout.EAST );
		try
		{
			board = createBoardFromFile( "map.txt" );
			cluedo = new CluedoBoard( board, panel, panel2, mi );
			add( cluedo, BorderLayout.CENTER );
		} catch ( IOException e )
		{
			e.printStackTrace();
		}
		pack();
		setResizable(false); // prevent us from being resize-able
		setVisible( true );
	}

	private JMenuItem addMenuItem( List<JMenuItem> mi, JMenu menu, String str, boolean enable )
	{
		JMenuItem item  = new JMenuItem( str );
		item.setActionCommand( str );
		item.setEnabled( enable );
		menu.add(item);
		mi.add( item );
		return item;
	}

	private static char[][] createBoardFromFile( String filename ) throws IOException
	{
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		LinkedList<LinkedList<Character>> map = new LinkedList<LinkedList<Character>>();
		int width = -1;
		int ch  = br.read();
		if ( ch == -1 )
		{
			br.close();fr.close();
			throw new IllegalArgumentException("Input file \"" + filename + "\" is empty");
		}
		do
		{
			LinkedList<Character> line = new LinkedList<Character>();
			while ( ch != '\n')
			{
				line.add( (char) ch );
				ch  = br.read();
			}
			if( width == -1 )
			{
				width = line.size();
			} else if( width != line.size() )
			{
				br.close();fr.close();
				throw new IllegalArgumentException("Input file \"" + filename
						+ "\" is malformed; line " + map.size() + " incorrect width.");
			}
			map.add( line );
			ch  = br.read();
		} while( ch != -1 );
		br.close();
		fr.close();
		int i = 0, j = 0;
		char[][] board = new char[ map.getFirst().size() ][ map.size() ];
		for ( LinkedList<Character> line: map )
		{
			if ( !line.isEmpty() ) for ( char c: line )
			{
				board[i][j] = c;
				++i;
			}
			++j;
			i = 0;
		}
		for ( char[] ca: board ) // proof of loading
		{
			for ( char c: ca )
				System.out.print( c );
			System.out.print('\n');
		}
		return board;
	}
	public static void main( String[] args )
	{
		new CluedoFrame();
	}


}
