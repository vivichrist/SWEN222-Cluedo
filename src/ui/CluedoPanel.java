package ui;

import identities.Cards;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

@SuppressWarnings( "serial" )
public class CluedoPanel extends JPanel implements RollListener {

	/**
	 * 
	 */
	public CluedoPanel()
	{
		super();
		this.setLayout( new GridLayout( 0, 2 ) );
		add( die1 );
		add( die2 );
		setBackground( Color.BLACK );
	}

	private JLabel die1 = new JLabel( new ImageIcon( Cards.convertInt( 0 ) ) );
	private JLabel die2 = new JLabel( new ImageIcon( Cards.convertInt( 0 ) ) );

	@Override
	public void message(int die1, int die2) {
		this.die1.setIcon( new ImageIcon( Cards.convertInt( die1 ) ) );
		this.die2.setIcon( new ImageIcon( Cards.convertInt( die2 ) ) );
	}

	@Override
	public void cards( List<Cards> cards, Cards player ) {
		this.removeAll();
		add( die1 );
		add( die2 );
		JLabel card;
		for ( Cards c: cards )
		{
			card = new JLabel( new ImageIcon( Cards.imageFromCard( c ) ) );
			card.setToolTipText( c.toString() );
			add( card );
		}
		validate();
		repaint();
		JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
		topFrame.setTitle( player.toString() );
	}
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension( 120, this.getParent().getHeight() );
	}
}
