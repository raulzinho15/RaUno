package cards;

/**
 * An enum storing different values for RaUno cards.
 * @author Raul Hernandez, 12/25/2023
 *
 */
public enum CardValue {
	
	/** The zero RaUno card value. */
	ZERO("0"),
	
	/** The one RaUno card value. */
	ONE("1"),

	/** The two RaUno card value. */
	TWO("2"),

	/** The three RaUno card value. */
	THREE("3"),

	/** The four RaUno card value. */
	FOUR("4"),

	/** The five RaUno card value. */
	FIVE("5"),

	/** The six RaUno card value. */
	SIX("6"),

	/** The seven RaUno card value. */
	SEVEN("7"),

	/** The eight RaUno card value. */
	EIGHT("8"),
	
	/** The nine RaUno card value. */
	NINE("9"),

	/** The reverse RaUno card value. */
	REVERSE("R"),

	/** The skip RaUno card value. */
	SKIP("S"),
	
	/** The draw two RaUno card value. */
	DRAW_TWO("+2"),

	/** The draw four RaUno card value. */
	DRAW_FOUR("+4", true),

	/** The wild RaUno card value. */
	WILD("W", true);
	
	/** The digit RaUno card values. */
	public static final CardValue[] DIGITS = {
		ZERO, ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE 
	};
	
	/** The non-special non-digit RaUno card values. */
	public static final CardValue[] REGULAR_POWERS = {
		REVERSE, SKIP, DRAW_TWO
	};
	
	/** The special non-digit RaUno card values. */
	public static final CardValue[] SPECIAL_POWERS = {
		DRAW_FOUR, WILD
	};
	
	/** Whether this value is a special (initially colorless) RaUno card value. */
	public final boolean special;
	
	/** The text to display on the RaUno card. */
	public final String text;

	/**
	 * Constructs a non-special RaUno card value.
	 * @param text The text to display on the RaUno card.
	 */
	private CardValue(String text) {
		this.text = text;
		special = false;
	}
	
	/**
	 * Constructs a RaUno card value.
	 * @param text The text to display on the RaUno card.
	 * @param special Whether the card value is special (initially colorless).
	 */
	private CardValue(String text, boolean special) {
		this.text = text;
		this.special = special;
	}
}
