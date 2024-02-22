package cards;

import java.util.ArrayList;

/**
 * Handles a deck of RaUno cards.
 * @author Raul Hernandez, 12/25/2023
 *
 */
public class Deck {
	
	/** 
	 * The cards currently in the deck.
	 * The top card is at index 0.
	 * The card at index i is on top of the card at index i+1.
	 */
	private final ArrayList<CardData> cards;

	/** 
	 * Creates a full deck for playing RaUno with shuffled cards.
	 */
	public Deck() {
		cards = defaultCards();
		shuffle();
	}
	
	/**
	 * Shuffles all the cards currently in the deck.
	 */
	public void shuffle() {
		final int size = cards.size();
		for (int i = size; i > 0; i--)
			cards.add(cards.remove((int)(i*Math.random())));
	}
	
	/** 
	 * Draws the card at the top of the deck.
	 * Removes the card from the deck.
	 * 
	 * @return The card drawn.
	 */
	public CardData draw() {
		return cards.remove(0);
	}
	
	/**
	 * @return The number of cards left in the deck.
	 */
	public int cardsLeft() {
		return cards.size();
	}
	
	/**
	 * Creates a list of cards containing:<br>
	 * - 2 of each digit card, in each color. (80 cards)<br>
	 * - 2 of each regular power card, in each color. (24 cards)<br>
	 * - 4 of each special power card. (8 cards)<br>
	 * 
	 * @return The list described above.
	 */
	private static ArrayList<CardData> defaultCards() {
		final ArrayList<CardData> cards = new ArrayList<CardData>();
		
		// Creates 2 of each color and digit card
		for (CardValue value : CardValue.DIGITS)
			for (CardColor color : CardColor.REGULAR_COLORS)
				for (int i = 0; i < 2; i++)
					cards.add(new CardData(color, value));
		
		// Creates 2 of each color and regular power card
		for (CardValue value : CardValue.REGULAR_POWERS)
			for (CardColor color : CardColor.REGULAR_COLORS)
				for (int i = 0; i < 2; i++)
					cards.add(new CardData(color, value));
		
		// Creates 4 of each special power card
		for (CardValue value : CardValue.SPECIAL_POWERS)
			for (int i = 0; i < 4; i++)
				cards.add(new CardData(CardColor.BLACK, value));
		
		return cards;
	}
}
