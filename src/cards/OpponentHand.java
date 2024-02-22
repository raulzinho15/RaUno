package cards;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Handles the hand of an opponent.
 * @author Raul Hernandez, 12/30/2023
 *
 */
public class OpponentHand extends Group {
	
	/** The width of all opponent hand groups. */
	public static final int WIDTH = Card.WIDTH + 120;
	
	/** The label displaying the number of cards in this hand. */
	private final Label count = new Label("");
	
	/**
	 * Whether the player name will be shown on the right.
	 * {@code true} to show on the right. {@code false} to show on the left.
	 */
	private final boolean nameRight;
	
	/** The number of cards in this hand. */
	private int cards;
	
	/**
	 * Constructs an opponent hand with the given number of cards.
	 * @param cards The number of cards to display.
	 * @param playerID The ID of the opponent.
	 * @param nameRight Whether the player name will be shown on the right.
	 * 					{@code true} to show on the right. {@code false} to show on the left.
	 */
	public OpponentHand(int cards, int opponentID, boolean nameRight) {
		this.nameRight = nameRight;
		setCards(cards);
		
		// Sets up the card background
		final Card card = new Card();
		if (!nameRight)
			card.setLayoutX(120);
		getChildren().add(card);
		
		// Sets up the hand count text
		if (!nameRight)
			count.setLayoutX(120);
		count.setFont(new Font("System Bold", 36));
		count.setTextFill(Color.WHITE);
		count.setPrefSize(Card.WIDTH, Card.HEIGHT);
		count.setAlignment(Pos.CENTER);
		count.setTextAlignment(TextAlignment.CENTER);
		getChildren().add(count);
		
		// Sets up the opponent ID display
		final Label ID = new Label("Player " + opponentID);
		if (nameRight)
			ID.setLayoutX(Card.WIDTH + 20);
		ID.setPrefSize(100, Card.HEIGHT);
		ID.setFont(new Font("System Bold", 24));
		ID.setTextFill(Color.WHITE);
		ID.setAlignment(Pos.CENTER);
		ID.setTextAlignment(TextAlignment.CENTER);
		getChildren().add(ID);
	}
	
	/**
	 * @return The x-coordinate of the card node in the parent's coordinates.
	 */
	public double cardParentX() {
		return getLayoutX() + (nameRight ? 0 : 120);
	}
	
	/**
	 * @return The number of cards in this hand.
	 */
	public int getCards() {
		return cards;
	}
	
	/**
	 * Updates the card count to the given value.
	 * @param cards The number of cards to update to.
	 */
	public void setCards(int cards) {
		this.cards = cards;
		count.setText("x" + cards);
	}
	
	/**
	 * Adds the given number of cards to the hand.
	 * @param cards The number of cards to add to the hand. Can be negative.
	 */
	public void addCards(int cards) {
		setCards(this.cards+cards);
	}
	
	/**
	 * Increments the number of cards in the hand by 1.
	 */
	public void increment() {
		addCards(1);
	}
	
	/**
	 * Decrements the number of cards in the hand by 1.
	 */
	public void decrement() {
		addCards(-1);
	}
}
