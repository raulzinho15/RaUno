package requests;

/**
 * Stores the client requests.
 * @author Raul Hernandez, 12/26/2023
 *
 */
public enum ClientRequest {

	/**
	 * A request from the client indicating it wants to use the given card.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CARD HAND INDEX, CARD COLOR, CARD VALUE]
	 */
	USE_CARD(),

	/**
	 * A request from the client indicating it wants to draw a card from the deck.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * []
	 */
	DRAW_CARD(),

	/**
	 * A request from the client indicating it called Uno for the given client at
	 * the given system time in milliseconds.
	 * 
	 * The byte array structure of this request is as follows:<br>
	 * 
	 * [CLIENT ID, 8 LSB OF TIME, 8 NEXT LSB BITS OF TIME, 8 NEXT LSB BITS OF TIME]
	 */
	CALLED_UNO();
	
	/**
	 * Creates a client request.
	 * @param ID The ID of the client request.
	 */
	private ClientRequest() {}
}
