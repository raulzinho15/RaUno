package cards;

/**
 * Handles the data of RaUno cards.
 * @author Raul Hernandez, 12/26/2023
 *
 */
public class CardData {
	
	/** The color of the RaUno card. */
	private CardColor color;
	
	/** The value of the RaUno card. */
	private final CardValue value;

	/**
	 * Constructs a RaUno card with the given data.
	 * 
	 * @param color The color of the card. 
	 * @param value The value of the card.
	 */
	public CardData(CardColor color, CardValue value) {
		this.color = color;
		this.value = value;
	}
	
	/**
	 * Constructs a card with a special value.
	 * @param value The value of the RaUno card. Must be special.
	 */
	public CardData(CardValue value) {
		this(CardColor.BLACK, value);
		if (!value.special)
			throw new RuntimeException("The value of a color-less card must be special.");
	}
	
	/**
	 * Checks whether this card can be placed on top of the given card.
	 * @param card The card to check compatibility with.
	 * @return Whether this card can follow the given card.
	 */
	public boolean canFollow(CardData card) {
		return value.special || value == card.value || color == card.color;
	}
	
	/**
	 * @return The color of the RaUno card.
	 */
	public CardColor getColor() {
		return color;
	}
	
	/**
	 * Sets the RaUno card's color to the given color.
	 * @param color The color to which the card's color will be set.
	 */
	public void setColor(CardColor color) {
		this.color = color;
	}
	
	/**
	 * @return The value of the RaUno card.
	 */
	public CardValue getValue() {
		return value;
	}
	
	public String toString() {
		return "CardData(" + color + ", " + value + ")";
	}
}
