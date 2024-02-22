package server;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import cards.CardColor;
import cards.CardData;
import cards.CardValue;
import cards.Deck;
import cards.HandData;
import requests.ClientRequest;
import requests.ServerRequest;

/**
 * Handles RaUno game sessions.
 * @author Raul Hernandez, 12/26/2023
 *
 */
public class RaUnoSession {
	
	/** The maximum number of players allowed in a session. */
	private static final int MAX_SESSION_SIZE = 5;
	
	/** The socket for the players. */
	private final Socket[] players;

	/** The input streams for the players. */
	private final InputStream[] inputs;

	/** The output stream for players. */
	private final OutputStream[] outputs;
	
	/** Whether a request is being handled by the players. */
	private final boolean[] handlingRequest;
	
	/** The call time (in milliseconds) for Uno for the players. */
	private final int[] unoCallTime;
	
	/** The hands of each player. */
	private final HandData[] hands;
	
	/** The number of players in the session. */
	private final int sessionSize;
	
	/** The deck for this game session. */
	private final Deck deck = new Deck();
	
	/** The card currently at the center. */
	private CardData centerCard;
	
	/** The ID of the player whose turn it currently is. */
	private int clientTurn = 0;
	
	/** Stores the direction to change the turns (either 1 or -1). */
	private int turnDirection = 1;
	
	/** Whether this session is closed. */
	private boolean closed = false;
	
	/** The client ID of the client on whom Uno is currently active. -1 if none. */
	private byte unoActive = -1;
	
	/**
	 * Creates a RaUno game session with the players.
	 * @param players The sockets for the players.
	 */
	public RaUnoSession(Socket...players) throws Exception {
	
		// Checks for valid session size
		sessionSize = players.length;
		if (sessionSize > MAX_SESSION_SIZE)
			throw new RuntimeException("Cannot host a game session with " + sessionSize
										+ " players (max: " + MAX_SESSION_SIZE + ").");
		
		// Initializes the player arrays
		this.players = new Socket[sessionSize];
		inputs = new InputStream[sessionSize];
		outputs = new OutputStream[sessionSize];
		handlingRequest = new boolean[sessionSize];
		unoCallTime = new int[sessionSize];
		hands = new HandData[sessionSize];
		
		// Stores the players' sockets and I/O streams
		System.out.println("Setting up the players' I/O streams...");
		for (int clientID = 0; clientID < sessionSize; clientID++) {
			this.players[clientID] = players[clientID];
			inputs[clientID] = players[clientID].getInputStream();
			outputs[clientID] = players[clientID].getOutputStream();
			hands[clientID] = new HandData();
			unoCallTime[clientID] = Integer.MAX_VALUE;
		}
		
		// Sends the players the starting data
		do
			centerCard = deck.draw();
		while (centerCard.getValue().special);
		System.out.println("Sending the players their starting hands...");
		for (int clientID = 0; clientID < sessionSize; clientID++) {
			
			// Sends the client's ID and signals the game has started
			outputs[clientID].write(new byte[] {
				(byte) ServerRequest.START_GAME.ordinal(),
				(byte) clientID,
				(byte) sessionSize,
				(byte) centerCard.getColor().ordinal(),
				(byte) centerCard.getValue().ordinal(),
			});
		}
		
		// Sends players their starting hands
		for (int clientID = 0; clientID < sessionSize; clientID++) {
			
			// Sets up the base of the request
			final byte[] request = new byte[2 + 2*HandData.START_SIZE];
			request[0] = (byte) ServerRequest.DRAW_FROM_DECK.ordinal();
			request[1] = (byte) HandData.START_SIZE;
			
			// Stores the hand card data
			for (int i = 0; i < HandData.START_SIZE; i++) {
				final CardData card = deck.draw();
				hands[clientID].add(card);
				request[2 + 2*i] = (byte) card.getColor().ordinal();
				request[2 + 2*i+1] = (byte) card.getValue().ordinal();
			}
			
			outputs[clientID].write(request);
		}
		
		// Tells players that their opponents drew their initial cards
		for (int clientID = 0; clientID < sessionSize; clientID++) {
			for (byte opponentID = 0; opponentID < sessionSize; opponentID++) {
				if (clientID == opponentID) continue; // Skips if the same client
				
				outputs[clientID].write(new byte[] {
					(byte) ServerRequest.OPPONENT_DRAW_FROM_DECK.ordinal(),
					opponentID,
					HandData.START_SIZE,
				});
			}
		}
		
		// Set up client request listeners
		System.out.println("Listening for client requests...");
		for (int i = 0; i < sessionSize; i++) {
			final int clientID = i;
			new Thread() {
				public void run() { try { while (true) {
					
					// Checks for a valid request
					final int requestID = inputs[clientID].read();
					if (requestID == -1)
						continue;
					
					// Processes the request
					handlingRequest[clientID] = true;
					handleRequest(ClientRequest.values()[requestID], clientID);
					while (handlingRequest[clientID]) Thread.sleep(250); // Allows for asynchronous tasks
						
				}} catch (Exception e) {e.printStackTrace();} closeConnections(); }
			}.start();
		}
		
		System.out.println("Connection successful!");
	}
	
	/**
	 * Handles the given request from the given client.
	 * @param request The request received from the client.
	 * @param clientID The ID of the client who sent the request.
	 */
	private void handleRequest(ClientRequest request, int clientID) { try {
		System.out.println("Handling Client #" + clientID + " request: " + request);

		// Client said they want to use a card
		if (request == ClientRequest.USE_CARD) {
			
			// Stores the request's values
			final byte[] reqArray = new byte[3];
			inputs[clientID].read(reqArray);
			
			// Stores the card trying to be used
			final CardData card = new CardData(
				CardColor.values()[reqArray[1]],
				CardValue.values()[reqArray[2]]
			);

			// Card validity checks
			if (clientID != clientTurn // Checks if it is this client's turn
				|| !card.canFollow(centerCard) // Checks if this card can be used
				|| !hands[clientID].has(card)) { // Checks that this client has this card
				
				outputs[clientID].write(ServerRequest.INVALID_CARD_USE.ordinal());
			}
			
			// Registers the card as used
			else {
				
				// Updates the session card/client data
				centerCard = card;
				hands[clientID].remove(card);
				
				// Tells all clients the card was used
				for (OutputStream out : outputs) {
					out.write(new byte[] {
						(byte) ServerRequest.REMOVE_FROM_HAND.ordinal(),
						(byte) clientID,
						reqArray[0],
						(byte) card.getColor().ordinal(),
						(byte) card.getValue().ordinal(),
					});
				}
				
				// Checks if the player won the game
				if (hands[clientID].size() == 0) {
					for (OutputStream out : outputs) {
						out.write(new byte[] {
							(byte) ServerRequest.PLAYER_WON.ordinal(),
							(byte) clientID
						});
					}
					handlingRequest[clientID] = false;
					return;
				}

				// Checks if the player has uno
				if (hands[clientID].size() == 1) {
					for (OutputStream out : outputs) {
						out.write(new byte[] {
							(byte) ServerRequest.UNO.ordinal(),
							(byte) clientID
						});
					}
					unoActive = (byte) clientID;
					
					// Checks for an Uno call every 3 seconds
					// Currently not synchronized to check Unos happening in quick succession
					// Perhaps try making this thread into its own object to avoid starting multiple copies of it accidentally?
					new Thread() {
						public void run() { try { while (unoActive != -1) {
							Thread.sleep(3_000);
							
							// Checks for the fastest Uno call
							int minTime = Integer.MAX_VALUE;
							int minID = -1;
							for (int i = 0; i < sessionSize; i++) {
								if (unoCallTime[i] < minTime) {
									minTime = unoCallTime[i];
									minID = i;
								}
							}
							
							// Skips if there was no Uno call
							if (minID == -1) continue;
							
							// Tells clients to remove the Uno call button
							for (OutputStream out : outputs)
								out.write(ServerRequest.REMOVE_UNO.ordinal());
							
							// Checks if a draw needs to happen
							if (unoActive != minID) {
								
								// Makes the Uno person draw their card
								final CardData card = deck.draw();
								hands[unoActive].add(card);
								outputs[unoActive].write(new byte[] {
									(byte) ServerRequest.DRAW_FROM_DECK.ordinal(),
									1,
									(byte) card.getColor().ordinal(),
									(byte) card.getValue().ordinal()
								});
								
								// Broadcasts to other players that the card was drawn
								for (int i = 0; i < sessionSize; i++) {
									if (i == unoActive) continue; // Skips if is the draw recipient
									outputs[i].write(new byte[] {
										(byte) ServerRequest.OPPONENT_DRAW_FROM_DECK.ordinal(),
										unoActive,
										1
									});
								}
							}
							
							// Resets the Uno call checking
							unoActive = -1;
							for (int i = 0; i < sessionSize; i++)
								unoCallTime[i] = Integer.MAX_VALUE;
						}} catch (Exception e) {}}
					}.start();
				}
				
				// SPECIAL CARDS
				// Makes the next person draw 2 and skip their turn
				if (card.getValue() == CardValue.DRAW_TWO) {
					final byte cards = 2;
					nextPlayer();
					
					// Prepares the request to be sent to the draw recipient
					final byte[] drawReq = new byte[2 + cards*2];
					drawReq[0] = (byte) ServerRequest.DRAW_FROM_DECK.ordinal();
					drawReq[1] = cards;
					
					// Draws the cards
					for (int i = 0; i < cards; i++) {
						final CardData drawnCard = deck.draw();
						hands[clientTurn].add(drawnCard);
						drawReq[2 + 2*i] = (byte) drawnCard.getColor().ordinal();
						drawReq[2 + 2*i+1] = (byte) drawnCard.getValue().ordinal();
					}
					
					// Sends the request to the draw recipient
					outputs[clientTurn].write(drawReq);
					
					// Prepares the request to be sent to all other clients
					for (int client = 0; client < sessionSize; client++) {
						if (client == clientTurn) continue; // Skips the draw recipient
						
						outputs[client].write(new byte[] {
							(byte) ServerRequest.OPPONENT_DRAW_FROM_DECK.ordinal(),
							(byte) clientTurn,
							cards
						});
					}

				// Makes the next person draw 4 and skip their turn
				} else if (card.getValue() == CardValue.DRAW_FOUR) {
					final int cards = 4;
					nextPlayer();
					
					// Prepares the request
					final byte[] drawReq = new byte[2 + cards*2];
					drawReq[0] = (byte) ServerRequest.DRAW_FROM_DECK.ordinal();
					drawReq[1] = (byte) cards;
					
					// Draws the cards
					for (int i = 0; i < cards; i++) {
						final CardData drawnCard = deck.draw();
						hands[clientTurn].add(drawnCard);
						drawReq[2 + 2*i] = (byte) drawnCard.getColor().ordinal();
						drawReq[2 + 2*i+1] = (byte) drawnCard.getValue().ordinal();
					}
					
					// Sends the request to the draw recipient
					outputs[clientTurn].write(drawReq);
					
					// Prepares the request to be sent to all other clients
					for (int client = 0; client < sessionSize; client++) {
						if (client == clientTurn) continue; // Skips the draw recipient
						
						outputs[client].write(new byte[] {
							(byte) ServerRequest.OPPONENT_DRAW_FROM_DECK.ordinal(),
							(byte) clientTurn,
							cards
						});
					}
					
				// Switches the direction of turn progression, and
				// skips the next person if it is a 2-player game
				} else if (card.getValue() == CardValue.REVERSE) {
					turnDirection *= -1;
					if (sessionSize == 2)
						nextPlayer();
					
				// Skips the next player
				} else if (card.getValue() == CardValue.SKIP) {
					nextPlayer();
				}
				
				endTurn();
			}
		
		// Client said it wants to draw a card
		} else if (request == ClientRequest.DRAW_CARD) {
			
			// Draws a card and sends it
			final CardData card = deck.draw();
			hands[clientID].add(card);
			final byte[] req = {
				(byte) ServerRequest.DRAW_FROM_DECK.ordinal(),
				(byte) 1,
				(byte) card.getColor().ordinal(),
				(byte) card.getValue().ordinal(),
			};
			outputs[clientID].write(req);
			
			// Tells all other clients about the draw
			for (int client = 0; client < sessionSize; client++) {
				if (client == clientID) continue; // Skips the draw recipient
				
				outputs[client].write(new byte[] {
					(byte) ServerRequest.OPPONENT_DRAW_FROM_DECK.ordinal(),
					(byte) clientID,
					1
				});
			}
			
			endTurn();
			
		// Client called Uno
		} else if (request == ClientRequest.CALLED_UNO) {
			
			// Stores the request's values
			final byte[] reqArray = new byte[4];
			inputs[clientID].read(reqArray);

			final int time = (int)reqArray[1] + (((int)reqArray[2]) << 8) + (((int)reqArray[3]) << 16);
			unoCallTime[clientID] = time;
		}
		
		handlingRequest[clientID] = false;
	} catch (Exception e) {e.printStackTrace();}}
	
	/**
	 * Increments the turn count to the next player according
	 * to the current direction of turn progression.
	 */
	private void nextPlayer() {
		clientTurn = (sessionSize + clientTurn + turnDirection) % sessionSize;
	}
	
	/**
	 * Ends the turn and moves to the next player.
	 */
	private void endTurn() throws Exception {
		nextPlayer();
		for (OutputStream out : outputs) {
			out.write(new byte[] {
				(byte) ServerRequest.END_TURN.ordinal(),
				(byte) clientTurn
			});
		}
	}
	
	/**
	 * Closes the connection with all the clients in this session.
	 */
	private void closeConnections() { try {
		closed = true;
		for (int i = 0; i < sessionSize; i++) {
			inputs[i].close();
			outputs[i].close();
			players[i].close();
		}
	} catch (Exception e) {e.printStackTrace();}}
	
	/**
	 * @return Whether this session is closed.
	 */
	public boolean isClosed() {
		return closed;
	}
}
