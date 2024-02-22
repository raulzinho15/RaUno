package cards;

import java.util.ArrayList;

/**
 * Handles the card data in a hand.
 * @author Raul Hernandez, 12/26/2023
 *
 */
public class HandData {
	
	/** The size of a starting hand. */
	public static final int START_SIZE = 7;
	
	/** The data of the cards in this hand. */
	private final ArrayList<CardData> cards = new ArrayList<CardData>();

	/**
	 * Constructs a hand data object storing the data of cards in this hand. 
	 * @param cards The cards in this hand.
	 */
	public HandData(CardData...cards) {
		for (CardData card : cards)
			this.cards.add(card);
	}
	
	/**
	 * Adds the given card to the hand.
	 * @param card The card to add to the hand.
	 */
	public void add(CardData card) {
		cards.add(card);
	}
	
	/**
	 * Removes the given card from the hand, if it is in the hand.
	 * @param card The card to remove from the hand.
	 */
	public void remove(CardData card) {
		for (CardData data : cards) {
			if (data.getColor() == card.getColor() && data.getValue() == card.getValue()
					|| data.getValue() == card.getValue() && card.getValue().special) {
				cards.remove(data);
				break;
			}
		}
	}
	
	/**
	 * Removes the card at the given index.
	 * @param index The index from which to remove a card.
	 */
	public void remove(int index) {
		cards.remove(index);
	}
	
	/**
	 * Checks whether this hand has the given card.
	 * @param card The card to check for in the hand.
	 * @return Whether the hand has the card.
	 */
	public boolean has(CardData card) {
		for (CardData data : cards)
			if (data.getColor() == card.getColor() && data.getValue() == card.getValue()
				|| data.getValue() == card.getValue() && card.getValue().special)
				return true;
		return false;
	}
	
	/**
	 * @return The number of cards in this hand.
	 */
	public int size() {
		return cards.size();
	}
	
	/**
	 * @return The card data in this hand.
	 */
	public CardData[] getCards() {
		return cards.toArray(new CardData[this.cards.size()]);
	}
}
