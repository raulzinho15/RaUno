package cards;

import client.RaUnoClient;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;

/**
 * Handles RaUno hands.
 * @author Raul Hernandez, 12/25/2023
 *
 */
public class Hand extends Group {
	
	/** The spacing between cards in the hand. */
	private static final int SPACING = Card.WIDTH + 10;
	
	/** The cards in this hand. */
	private final HandData cards;
	
	/**
	 * Creates a hand with the given cards.
	 * @param cards The cards in this hand.
	 */
	public Hand(HandData cards) {
		super();
		this.cards = cards;
		for (CardData card : cards.getCards())
			addNode(new Card(card));
	}
	
	/**
	 * Creates a hand with no cards.
	 */
	public Hand() {
		this(new HandData());
	}
	
	/**
	 * Adds the given card to the hand.
	 * @param card The card to be added to the hand.
	 */
	public void add(Card card) {
		addNode(new Card(card.data));
		cards.add(card.data);
	}
	
	/**
	 * Adds the given card's node to the hand.
	 * @param card The card whose node will be added.
	 */
	private void addNode(Card card) {
		
		// Computes the card's x-coordinate
		final int numCards = size();
		final double cardX = cardX(numCards);
		
		// Adds mouse functionality
		card.setOnMouseClicked(m -> {
			final CardData[] cards = this.cards.getCards();
			for (int i = 0; i < cards.length; i++) {
				if (cards[i] == card.data) {
					RaUnoClient.useCard(card.data, i);
					break;
				}
			}
		});
		card.setOnMouseEntered(m -> card.setCursor(Cursor.HAND));
		card.setOnMouseExited(m -> card.setCursor(Cursor.DEFAULT));
		
		// Adds the card to the hand
		card.setLayoutX(cardX);
		card.setVisible(false);
		getChildren().add(card);
		
		setLayoutX((RaUnoClient.WIDTH-width()-SPACING)/2);
	}
	
	/**
	 * Displays the card at the given index.
	 * @param index The index at which to display the card.
	 */
	public void show(int index) {
		getChildren().get(index).setVisible(true);
	}
	
	/**
	 * Removes the card from the hand.
	 * @param card The card to remove.
	 */
	public void remove(int index) {
		
		// Shifts next cards down
		for (int i = index+1; i < size(); i++) {
			final Node node = getChildren().get(i);
			node.setLayoutX(node.getLayoutX()-SPACING);
		}
		
		// Removes from the hand data
		getChildren().remove(index);
		cards.remove(index);
		
		setLayoutX((RaUnoClient.WIDTH-width())/2);
	}
	
	/**
	 * @return The width of the hand group.
	 */
	public double width() {
		final int numCards = size();
		if (numCards == 0)
			return Card.WIDTH;
		return Card.WIDTH + SPACING * (numCards-1);
	}
	
	/**
	 * Computes the x-coordinate of the card local to the hand.
	 * 
	 * @param index The index of the card.
	 * @return The x-coordinate of the card in the hand's local coordinates.
	 */
	public double cardX(int index) {
		return SPACING*index;
	}
	
	/**
	 * Computes the parent x-coordinate of the card.
	 * 
	 * @param index The index of the card.
	 * @return The x-coordinate of the card in the parent's coordinates.
	 */
	public double cardParentX(int index) {
		return getLayoutX() + cardX(index);
	}
	
	/**
	 * @return The number of cards in the hand.
	 */
	public int size() {
		return cards.size();
	}
}
