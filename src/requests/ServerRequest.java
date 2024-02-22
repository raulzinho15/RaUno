package requests;

/**
 * Stores the server requests.
 * @author Raul Hernandez, 12/26/2023
 *
 */
public enum ServerRequest {

	/**
	 * A request from the server with a list of cards to draw from
	 * the deck.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [NUMBER OF CARDS, CARD #1 COLOR, CARD #1 VALUE, ...]
	 */
	DRAW_FROM_DECK(),

	/**
	 * A request from the server with the client's ID, signaling
	 * a game start.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CLIENT ID, NUMBER OF PLAYERS, CENTER CARD COLOR, CENTER CARD VALUE]
	 */
	START_GAME(),

	/**
	 * A request from the server indicating that the card trying to be used
	 * is invalid given the current state of the game.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * []
	 */
	INVALID_CARD_USE(),

	/**
	 * A request from the server indicating that the given card was
	 * placed as the center card.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CARD COLOR, CARD VALUE]
	 */
	CENTER_CARD(),

	/**
	 * A request from the server indicating that the turn is over.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [NEXT CLIENT ID]
	 */
	END_TURN(),

	/**
	 * A request from the server indicating for the client to remove the
	 * card at the given index from the given client's hand.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CLIENT ID, CARD HAND INDEX, CARD COLOR, CARD VALUE]
	 */
	REMOVE_FROM_HAND(),

	/**
	 * A request from the server indicating that the specified opponent
	 * drew the given number of cards.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [OPPONENT CLIENT ID, NUMBER OF CARDS]
	 */
	OPPONENT_DRAW_FROM_DECK(),

	/**
	 * A request from the server indicating that the specified player
	 * has won the game.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CLIENT ID]
	 */
	PLAYER_WON(),

	/**
	 * A request from the server indicating that the specified player
	 * has reached one card.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CLIENT ID]
	 */
	UNO(),

	/**
	 * A request from the server requesting the client to remove the
	 * Uno button.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * []
	 */
	REMOVE_UNO();

	/**
	 * Creates a server request.
	 * @param ID The ID of the server request.
	 */
	private ServerRequest() {}
}
