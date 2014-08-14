package ui;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.JFrame;


@SuppressWarnings( "serial" )
public class CluedoFrame extends JFrame
{
	
	public CluedoFrame()
	{
		super( "Cluedo" );
		setLayout( new BorderLayout() );// use border layout
		try
		{
			char[][] board = createBoardFromFile( "map.txt" );
			CluedoBoard cluedo = new CluedoBoard( board );
			add( cluedo, BorderLayout.CENTER );
		} catch ( IOException e )
		{
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setResizable(false);
		// prevent us from being resize-able
		setVisible( true );
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
			map.add( new LinkedList<Character>() );
			while ( ch != '\n')
			{
				map.getLast().add( (char) ch );
				ch  = br.read();
			}
			if( width == -1 )
			{
				width = map.getLast().size();
			} else if( width != map.getLast().size() )
			{
				br.close();fr.close();
				throw new IllegalArgumentException("Input file \"" + filename
						+ "\" is malformed; line " + map.size() + " incorrect width.");
			}
			ch  = br.read();
		} while( ch != -1 );
		map.removeLast();
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
