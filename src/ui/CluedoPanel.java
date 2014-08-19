package ui;

import identities.Cards;

import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class CluedoPanel extends JPanel implements RollListener {

	private final JLabel Dice1;
	private final JLabel Dice2;

	public CluedoPanel(){
		Dice1 = new JLabel("Dice1");
		this.add(Dice1);
		Dice2 = new JLabel("Dice2");
		this.add(Dice2);


		JLabel card = new JLabel(("card"));

		card.setIcon(null);

		this.add(card);
		this.add(new JLabel("weapon"));
		this.add(new JLabel("room"));
	}

	@Override
	public void message(int die1, int die2) {
		Dice1.setText("Dice1 is " + die1 + "");
		Dice2.setText("Dice2 is "  + die2 + "");
	}

	@Override
	public void cards( List<Cards> cards, Cards player ) {
		// TODO Auto-generated method stub
	}
}
