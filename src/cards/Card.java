package cards;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * A class representing a RaUno card.
 * @author Raul Hernandez, 12/25/2023
 *
 */
public class Card extends Group {

	/** The width of a RaUno card. */
	public static final int WIDTH = 90;

	/** The height of a RaUno card. */
	public static final int HEIGHT = 150;
	
	/** The border thickness of a RaUno card. */
	public static final int BORDER = 6;
	
	/** The opacity of the front and back side of the card. */
	public static final double OPACITY = 0.15;
	
	/** The font of the RaUno card's text. */
	public static final Font TEXT_FONT = new Font("System Bold", 24);
	
	/** The front side of the RaUno card. */
	private final Group front = new Group();
	
	/** The back side of the RaUno card. */
	private final Rectangle back = new Rectangle(WIDTH-BORDER, HEIGHT-BORDER, Color.BLACK);
	
	/** The data of the RaUno card. */
	public final CardData data;
	
	/**
	 * Constructs a RaUno card with the given data.
	 * @param data The data of the RaUno card.
	 */
	public Card(CardData data) {
		super();
		this.data = data;
		
		// Sets up card's back side
		back.setLayoutX(BORDER/2);
		back.setLayoutY(BORDER/2);
		back.setOpacity(OPACITY);
		back.setVisible(false);
		getChildren().add(back);
		
		// Sets up the card background
		final Rectangle background = new Rectangle(WIDTH-BORDER, HEIGHT-BORDER);
		background.setFill(data.getColor().color);
		background.setOpacity(OPACITY);
		background.setLayoutX(BORDER/2);
		background.setLayoutY(BORDER/2);
		front.getChildren().add(background);
		
		// Sets up the upper-left card text
		final String valueText = " " + data.getValue().text; 
		final Label upperLeftText = new Label(valueText);
		upperLeftText.setPrefSize(WIDTH-2*BORDER, HEIGHT/2-BORDER);
		upperLeftText.setLayoutX(BORDER);
		upperLeftText.setLayoutY(BORDER);
		upperLeftText.setAlignment(Pos.TOP_LEFT);
		upperLeftText.setFont(TEXT_FONT);
		upperLeftText.setTextFill(Color.WHITE);
		front.getChildren().add(upperLeftText);
		
		// Sets up the lower-right card text
		final Label lowerRightText = new Label(valueText);
		lowerRightText.setPrefSize(WIDTH-2*BORDER, HEIGHT/2-BORDER);
		upperLeftText.setLayoutX(BORDER);
		lowerRightText.setLayoutY(HEIGHT/2);
		lowerRightText.setAlignment(Pos.TOP_LEFT);
		lowerRightText.setFont(TEXT_FONT);
		lowerRightText.setTextFill(Color.WHITE);
		lowerRightText.setRotate(180);
		front.getChildren().add(lowerRightText);
		
		getChildren().add(front);
		
		// Sets up the card border
		final Rectangle topBorder = new Rectangle(WIDTH, BORDER, Color.WHITE);
		topBorder.setArcWidth(BORDER);
		topBorder.setArcHeight(BORDER);
		final Rectangle bottomBorder = new Rectangle(WIDTH, BORDER, Color.WHITE);
		bottomBorder.setLayoutY(HEIGHT-BORDER);
		bottomBorder.setArcWidth(BORDER);
		bottomBorder.setArcHeight(BORDER);
		final Rectangle leftBorder = new Rectangle(BORDER, HEIGHT, Color.WHITE);
		leftBorder.setArcWidth(BORDER);
		leftBorder.setArcHeight(BORDER);
		final Rectangle rightBorder = new Rectangle(BORDER, HEIGHT, Color.WHITE);
		rightBorder.setLayoutX(WIDTH-BORDER);
		rightBorder.setArcWidth(BORDER);
		rightBorder.setArcHeight(BORDER);
		getChildren().add(topBorder);
		getChildren().add(bottomBorder);
		getChildren().add(leftBorder);
		getChildren().add(rightBorder);
	}

	/**
	 * Constructs a RaUno card with the given properties.
	 * @param color The color of the RaUno card.
	 * @param value The value of the RaUno card.
	 */
	public Card(CardColor color, CardValue value) {
		this(new CardData(color, value));
	}
	
	/**
	 * Constructs a card with a special value.
	 * @param value The value of the RaUno card. Must be special.
	 */
	public Card(CardValue value) {
		this(new CardData(value));
	}
	
	/**
	 * Constructs an indeterminate (as opposed to random) RaUno card, initially face down.
	 */
	public Card() {
		this(new CardData(CardColor.BLUE, CardValue.ZERO));
		flip();
	}
	
	/**
	 * Flips the RaUno card to the side opposite of what is currently shown.
	 */
	public void flip() {
		front.setVisible(back.isVisible());
		back.setVisible(!back.isVisible());
	}
	
	public String toString() {
		return "Card(" + data.getColor() + ", " + data.getValue() + ")";
	}
}
