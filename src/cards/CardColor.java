package cards;

import javafx.scene.paint.Color;

/**
 * An enum storing different colors for RaUno cards.
 * @author Raul Hernandez, 12/25/2023
 *
 */
public enum CardColor {

	/** The blue RaUno card color. */
	BLUE(Color.rgb(0, 0, 255)),

	/** The green RaUno card color. */
	GREEN(Color.rgb(0, 255, 0)),

	/** The red RaUno card color. */
	RED(Color.rgb(255, 0, 0)),

	/** The yellow RaUno card color. */
	YELLOW(Color.rgb(255, 255, 0)),

	/** The black RaUno card color (only for special cards). */
	BLACK(Color.BLACK);
	
	/** The JavaFX color associated with the RaUno card color. */
	public final Color color;
	
	/** The non-special colors. */
	public static final CardColor[] REGULAR_COLORS = {BLUE, GREEN, RED, YELLOW};
	
	/**
	 * Constructs a RaUno card color.
	 * @param color The JavaFX color associated with the RaUno card color.
	 */
	private CardColor(Color color) {
		this.color = color;
	}
}
