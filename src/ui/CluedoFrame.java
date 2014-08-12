package ui;
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
		try
		{
			add( new CluedoBoard( createBoardFromFile( "map.txt" ) ) );
		} catch ( IOException e )
		{
			e.printStackTrace();
		}
		// show window...

	}

	private static char[][] createBoardFromFile( String filename ) throws IOException
	{
		FileReader fr = new FileReader(filename);
		BufferedReader br = new BufferedReader(fr);
		LinkedList<LinkedList<Character>> map = new LinkedList<LinkedList<Character>>();
		map.add( new LinkedList<Character>() );
		int width = -1;
		int ch = 0;
		while( ch != -1 )
		{
			while ( (ch = br.read()) != '\n' && ch != -1) map.getLast().add( (char) ch );
			map.add( new LinkedList<Character>() );
			// now sanity check

			if( width == -1 )
			{
				width = map.getLast().size();
			} else if( width != map.getLast().size() )
			{
				br.close();fr.close();
				throw new IllegalArgumentException("Input file \"" + filename
						+ "\" is malformed; line " + map.size() + " incorrect width.");
			}
		}
		br.close();
		fr.close();
		int i = 0, j = 0;
		char[][] board = new char[ map.size() ][ map.getFirst().size() ];
		for ( LinkedList<Character> line: map )
		{
			for ( char c: line )
			{
				board[j][i] = c;
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
