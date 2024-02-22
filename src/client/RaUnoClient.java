package client;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

import cards.Card;
import cards.CardColor;
import cards.CardData;
import cards.CardValue;
import cards.Hand;
import cards.OpponentHand;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import misc.LooseBox;
import misc.Noise;
import misc.PostAnimation;
import misc.Vector2D;
import requests.ClientRequest;
import requests.ServerRequest;

/**
 * Handles the RaUno client.
 * @author Raul Hernandez, 12/25/2023
 *
 */
public class RaUnoClient extends Application {
	
	/** The width of the client scene. */
	public static final int WIDTH = 1300;
	
	/** The height of the client scene. */
	private static final int HEIGHT = 700;
	
	/**
	 * The IP on which the RaUno server is hosted.
	 * Computers on the same network as the server cannot connect via WAN.
	 * They must connect through LAN.
	 */
	private static String SERVER_IP = "127.0.0.1";
//	private static String SERVER_IP = "TYPE LAN IP HERE";
//	private static String SERVER_IP = "TYPE PUBLIC IP HERE";
	
	/** The port on which the RaUno server is hosted. */
	private static final int SERVER_PORT = 29175;
	
	/** The socket for the server. */
	private static Socket SERVER;
	
	/** The input stream for the server. */
	private static InputStream INPUT;
	
	/** The output stream for the server. */
	private static OutputStream OUTPUT;
	
	/** Whether the client is processing for a server request. */
	private static boolean PROCESSING_REQUEST;
	
	/** The pane onto which everything in the scene is placed. */
	private static final AnchorPane PANE = new AnchorPane();
	
	/** The hand of the client. */
	private static final Hand HAND = new Hand();
	
	/** The hands of the opponents. */
	private static final OpponentHand[] OPPONENTS = {
		new OpponentHand(0, 1, true),
		new OpponentHand(0, 2, true),
		new OpponentHand(0, 3, false),
		new OpponentHand(0, 4, false),
	};
	
	/** 
	 * The player ID for each opponent.
	 * The index of the array is the client ID. 
	 * The value at that index is the player ID for that client.
	 */
	private static final int[] OPPONENT_IDS = {-1, -1, -1, -1, -1};
	
	/** The center card group in the game. */
	private static final Group CENTER_CARD = new Group();
	
	/** The deck card in the game. */
	private static final Card DECK_CARD = new Card(CardColor.BLUE, CardValue.ZERO);
	
	/** The indicator saying whether it is this client's turn. */
	private static final Label TURN_TEXT = new Label("");
	
	/** The data of the center card. */
	private static CardData CENTER_CARD_DATA;
	
	/** The ID of the client to the server session. */
	private static int clientID;
	
	/** The ID of the client whose turn it is. */
	private static int clientTurn;
	
	/** The number of opponents the client has in this game. */
	private static int numOpponents;
	
	/** Whether the client is currently using a card. */
	private static boolean acting;
	
	/** The index of the last card that was clicked. */
	private static int lastCardClicked;
	
	/** The box containing the color choices when a special card is used. */
	private static final VBox COLOR_CHOICES = new VBox();
	
	/** The data of the last special card that was clicked. */
	private static CardData specialCardUsed;
	
	/** The button to press to call Uno. */
	private static final Group UNO_BUTTON = new Group();

	public static void main(String[] args) {
		
		// Initializes the server connection
		if (args.length > 0) {
			SERVER_IP = args[0];
		}
		System.out.println("Server IP: " + SERVER_IP);
		new Thread() {
			public void run() { try {
				connectToServer();
				listenToServer();
			} catch (Exception e) {e.printStackTrace();}}
		}.start();
		
		launch(args);
	}
	
	/**
	 * Connects to the RaUno server and establishes the I/O streams.
	 */
	private static void connectToServer() throws Exception {
		
		// Connects to the server
		System.out.println("Connecting to the server...");
		SERVER = new Socket(InetAddress.getByName(SERVER_IP), SERVER_PORT);
		
		// Sets up the I/O streams
		System.out.println("Setting up the I/O streams...");
		INPUT = SERVER.getInputStream();
		OUTPUT = SERVER.getOutputStream();
		
		System.out.println("Connection successful!");
	}
	
	/**
	 * Listens perpetually for server requests.
	 */
	private static void listenToServer() {
		
		// Sets up the listening thread
		new Thread() {
			public void run() { try { while (!SERVER.isClosed()) {
				
				// Checks for a valid request
				System.out.println("Listening for a request...");
				final int requestID = INPUT.read();
				if (requestID == -1)
					continue;
				
				// Processes the request
				PROCESSING_REQUEST = true;
				handleRequest(ServerRequest.values()[requestID]);
				while (PROCESSING_REQUEST) Thread.sleep(250); // Allows for asynchronous tasks
				System.out.println("Request processed!");
					
			}} catch (Exception e) {e.printStackTrace();} closeConnections(); }
		}.start();
		
	}
	
	/**
	 * Handles a server request.
	 * @param request The server request to process.
	 */
	private static void handleRequest(final ServerRequest request) {
		System.out.println("Handling request: " + request + "...");
		
		// Queues the request
		Platform.runLater(new Runnable() {
			public void run() { try {
				
				// Starts the game
				if (request == ServerRequest.START_GAME) {
					
					// Reads the request values
					final byte[] reqArray = new byte[2 + 2];
					INPUT.read(reqArray);
					
					// Stores the ID of the client
					clientID = reqArray[0];
					clientTurn = 0;
					
					// Stores the opponent IDs
					numOpponents = reqArray[1]-1;
					int opponentID = 0;
					for (int i = 0; i < numOpponents+1; i++) {
						if (i == clientID) continue;
						OPPONENTS[opponentID].setVisible(true);
						OPPONENT_IDS[i] = opponentID++;
					}
					
					// Indicates whether it is the client's turn
					if (clientTurn == clientID)
						TURN_TEXT.setText("YOUR TURN");
					else
						TURN_TEXT.setText("PLAYER " + (OPPONENT_IDS[clientTurn]+1) + "'S TURN");
					
					// Stores the initial position for the animations
					final double x0 = DECK_CARD.getLayoutX();
					final double y0 = DECK_CARD.getLayoutY();
					
					// Stores the initial/final position for the animation
					final double x1 = CENTER_CARD.getLayoutX();
					final double y1 = CENTER_CARD.getLayoutY();
					
					// Prepares the center card
					final Card card = new Card(
						CardColor.values()[reqArray[2]],
						CardValue.values()[reqArray[3]]
					);
					
					// Starts the animation
					moveAnimation(x0, y0, x1, y1, 1, card, () -> {
						PANE.getChildren().remove(card);
						card.setLayoutX(0);
						card.setLayoutY(0);
						CENTER_CARD_DATA = card.data;
						CENTER_CARD.getChildren().clear();
						CENTER_CARD.getChildren().add(card);
					});
					PANE.getChildren().add(card);
							
				// Adds cards to the hand
				} else if (request == ServerRequest.DRAW_FROM_DECK) {
					
					// Reads the request values
					final byte[] reqArray = new byte[2*INPUT.read()];
					INPUT.read(reqArray);
					
					// Stores the cards in the hand
					final Card[] cards = new Card[reqArray.length/2];
					for (int i = 0; i < cards.length; i++) {
						cards[i] = new Card(CardColor.values()[reqArray[2*i]], CardValue.values()[reqArray[2*i+1]]);
						HAND.add(cards[i]);
					}
					
					// Animates the card placement into the hand
					final double x0 = DECK_CARD.getLayoutX();
					final double y0 = DECK_CARD.getLayoutY();
					final double y1 = HAND.getLayoutY();
					final int init = HAND.size()-cards.length;
					for (int i = init; i < HAND.size(); i++) {
						final int index = i;
						
						// Adds card to the scene & animates
						final Card card = cards[i-init];
						moveAnimation(x0, y0, HAND.cardParentX(i), y1, 1, card, () -> {
							PANE.getChildren().remove(card);
							HAND.show(index);
						});
						PANE.getChildren().add(card);
					}
					
				} else if (request == ServerRequest.OPPONENT_DRAW_FROM_DECK) {
					
					// Reads the request values
					final byte[] reqArray = new byte[2];
					INPUT.read(reqArray);
					
					// Stores the initial/final positions for the animation
					final OpponentHand hand = OPPONENTS[OPPONENT_IDS[reqArray[0]]];
					final double x0 = DECK_CARD.getLayoutX();
					final double y0 = DECK_CARD.getLayoutY();
					final double x1 = hand.cardParentX();
					final double y1 = hand.getLayoutY();
					
					// Animates each opponent drawing from the deck
					final Card card = new Card();
					moveAnimation(x0, y0, x1, y1, 1, card, () -> {
						PANE.getChildren().remove(card);
						hand.addCards(reqArray[1]);
					});
					PANE.getChildren().add(card);
					
				// Placed a new center card
				} else if (request == ServerRequest.CENTER_CARD) {
					
					// Reads the request values
					final byte[] reqArray = new byte[2];
					INPUT.read(reqArray);
					
					// Sets the center card
					CENTER_CARD_DATA = new CardData(
						CardColor.values()[reqArray[0]],
						CardValue.values()[reqArray[1]]
					);
					CENTER_CARD.getChildren().clear();
					CENTER_CARD.getChildren().add(new Card(CENTER_CARD_DATA));
					
				// Ends the turn and moves into the next turn
				} else if (request == ServerRequest.END_TURN) {
					
					// Stores the new client's turn
					final byte[] reqArray = new byte[1];
					INPUT.read(reqArray);
					clientTurn = reqArray[0];
					
					acting = false; ////// UPDATE ACTING TO PREVENT CLICKS WHEN AN ANIMATION IS PLAYING
					
					// Indicates whether it is the client's turn
					if (clientTurn == clientID)
						TURN_TEXT.setText("YOUR TURN");
					else
						TURN_TEXT.setText("PLAYER " + (OPPONENT_IDS[clientTurn]+1) + "'S TURN");
					
				// Removes the last clicked card from the hand
				} else if (request == ServerRequest.REMOVE_FROM_HAND) {
					
					// Stores the request's values
					final byte[] reqArray = new byte[2 + 2];
					INPUT.read(reqArray);
					
					// Creates the card node to be animated
					final Card card = new Card(
						CardColor.values()[reqArray[2]],
						CardValue.values()[reqArray[3]]
					);
					
					// Stores the ending position of the card animation
					final double x1 = CENTER_CARD.getLayoutX();
					final double y1 = CENTER_CARD.getLayoutY();
					
					// Checks if an opponent card use animation is needed
					if (reqArray[0] != clientID) {
						final OpponentHand hand = OPPONENTS[OPPONENT_IDS[reqArray[0]]];
						final double x0 = hand.cardParentX();
						final double y0 = hand.getLayoutY();
						
						// Animates the removal from the hand
						moveAnimation(x0, y0, x1, y1, 1, card, () -> {
							PANE.getChildren().remove(card);
							card.setLayoutX(0);
							card.setLayoutY(0);
							CENTER_CARD_DATA = card.data;
							CENTER_CARD.getChildren().clear();
							CENTER_CARD.getChildren().add(card);
						});
						hand.decrement();
					} else {
					
						// Stores the starting position of the card animation
						final double x0 = HAND.cardParentX(lastCardClicked);
						final double y0 = HAND.getLayoutY();
						
						// Removes the card from the hand
						HAND.remove(lastCardClicked);
						
						// Animates the removal from the hand
						moveAnimation(x0, y0, x1, y1, 1, card, () -> {
							PANE.getChildren().remove(card);
							card.setLayoutX(0);
							card.setLayoutY(0);
							CENTER_CARD_DATA = card.data;
							CENTER_CARD.getChildren().clear();
							CENTER_CARD.getChildren().add(card);
						});
					}
					PANE.getChildren().add(card);
					
				// The card used was invalid
				} else if (request == ServerRequest.INVALID_CARD_USE) {
					acting = false;
				
				// A player won
				} else if (request == ServerRequest.PLAYER_WON) {
					
					// Stores the request's values
					final byte[] reqArray = new byte[1];
					INPUT.read(reqArray);
					
					if (reqArray[0] == clientID)
						TURN_TEXT.setText("YOU WON!!!");
					else
						TURN_TEXT.setText("PLAYER " + (OPPONENT_IDS[reqArray[0]]+1) + " WON!!!");
					acting = true;
					UNO_BUTTON.setVisible(false);
					
				// A player reached Uno
				} else if (request == ServerRequest.UNO) {
					
					// Stores the request's values
					final byte[] reqArray = new byte[1];
					INPUT.read(reqArray);
					showUnoButton(reqArray[0]);
					
				// Removes the Uno button
				} else if (request == ServerRequest.REMOVE_UNO) {
					UNO_BUTTON.setVisible(false);
				}
			} catch (Exception e) {e.printStackTrace(); } PROCESSING_REQUEST = false;}
		});
	}
	
	/**
	 * Attempts to use the given card.
	 * @param card The card data of the card trying to be used.
	 * @param index The index (in the hand) of the card being used. 
	 */
	public static void useCard(CardData card, int index) {

		// Checks if an action is already being done
		if (acting)
			return;
		
		// Checks if it is this player's turn
		if (clientID != clientTurn)
			return;
		
		// Checks if this is a valid card to play
		if (!card.canFollow(CENTER_CARD_DATA))
			return;
		
		// Checks if a special card
		if (card.getValue().special) {
			specialCardUsed = card;
			acting = true;
			lastCardClicked = index;
			COLOR_CHOICES.setVisible(true);
			return;
		}
		
		// Requests the server to use the card
		try {
			OUTPUT.write(new byte[] {
				(byte) ClientRequest.USE_CARD.ordinal(),
				(byte) lastCardClicked,
				(byte) card.getColor().ordinal(),
				(byte) card.getValue().ordinal()
			});
			acting = true;
			lastCardClicked = index;
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/**
	 * Closes the connections and I/O streams with the RaUno server.
	 */
	private static void closeConnections() { try {
		INPUT.close();
		OUTPUT.close();
		SERVER.close();
	} catch (Exception e) {e.printStackTrace();}}
	
	private static void moveAnimation(double x0, double y0, double x1, double y1, double duration, Node node, PostAnimation post) {
		
		// Sets up initial position
		node.setLayoutX(x0);
		node.setLayoutY(y0);
		
		// Animates the motion
		final long sTime = System.nanoTime();
		new AnimationTimer() {
			public void handle(long now) {
				final double dt = (now-sTime)*1E-9 / duration;
				
				// Ends the animation
				if (dt >= 1) {
					node.setLayoutX(x1);
					node.setLayoutY(y1);
					post.run();
					stop();
					return;
				}
				
				// Updates the card's new position
				final double x = x0 + (x1-x0) * dt;
				final double y = y0 + (y1-y0) * dt;
				node.setLayoutX(x);
				node.setLayoutY(y);
			}
		}.start();
	}
	
	private static void showUnoButton(final byte clientID) {
		UNO_BUTTON.setVisible(true);

		// Places the button randomly on the screen
		final int x = 280 + (int) (700 * Math.random());
		final int y = 260 + (int) (140 * Math.random());
		UNO_BUTTON.setLayoutX(x);
		UNO_BUTTON.setLayoutY(y);
		
		// Sets up the uno call
		UNO_BUTTON.setOnMouseClicked(m -> { try {
			final long time = System.currentTimeMillis();
			OUTPUT.write(new byte[] {
				(byte) ClientRequest.CALLED_UNO.ordinal(),
				clientID,
				(byte) time,
				(byte) (time >> 8),
				(byte) (time >> 16),
			});
			UNO_BUTTON.setVisible(false);
		} catch (Exception e) {e.printStackTrace();}});
	}

	public void start(Stage stage) throws Exception {
		
		// Sets up the background
		final Canvas back = new Canvas(WIDTH, HEIGHT);
		final PixelWriter pixels = back.getGraphicsContext2D().getPixelWriter();
		for (int x = 0; x < WIDTH; x++) {
			for (int y = 0; y < HEIGHT; y++) {
				int noise = (int) (15*Noise.noise(new Vector2D(x/40.0, y/40.0), WIDTH));
				pixels.setColor(x, y, Color.rgb(16+noise, 20+noise, 30+noise));
			}
		}
		PANE.getChildren().add(back);
		
		// Displays the center card
		CENTER_CARD.setLayoutX((WIDTH - 3*Card.WIDTH)/2);
		CENTER_CARD.setLayoutY(100);
		PANE.getChildren().add(CENTER_CARD);
		
		// Displays the deck card
		DECK_CARD.setLayoutX(CENTER_CARD.getLayoutX() + 2*Card.WIDTH);
		DECK_CARD.setLayoutY(CENTER_CARD.getLayoutY());
		DECK_CARD.flip();
		DECK_CARD.setOnMouseClicked(m -> { try {
			
			// Checks if an action is already being done
			if (acting)
				return;
			
			// Checks if it is the client's turn
			if (clientTurn != clientID)
				return;
			
			// Requests to draw a card from the deck
			OUTPUT.write(ClientRequest.DRAW_CARD.ordinal());
			acting = true;
			
		} catch (Exception e) {e.printStackTrace();}});
		DECK_CARD.setOnMouseEntered(m -> DECK_CARD.setCursor(Cursor.HAND));
		DECK_CARD.setOnMouseExited(m -> DECK_CARD.setCursor(Cursor.DEFAULT));
		PANE.getChildren().add(DECK_CARD);
		
		// Displays the hand
		HAND.setLayoutX(WIDTH/2);
		HAND.setLayoutY(450);
		PANE.getChildren().add(HAND);
		
		// Displays the special card color choice options
		COLOR_CHOICES.setLayoutX(CENTER_CARD.getLayoutX()-50);
		COLOR_CHOICES.setLayoutY(100);
		COLOR_CHOICES.setPrefHeight(Card.HEIGHT);
		COLOR_CHOICES.setSpacing(10);
		COLOR_CHOICES.setAlignment(Pos.CENTER);
		for (final CardColor color : CardColor.REGULAR_COLORS) {
			final Rectangle colorSquare = new Rectangle(30, 30, color.color);
			colorSquare.setOpacity(Card.OPACITY*2);
			colorSquare.setOnMouseEntered(m -> colorSquare.setCursor(Cursor.HAND));
			colorSquare.setOnMouseExited(m -> colorSquare.setCursor(Cursor.DEFAULT));
			
			// Requests the server to use the special card with the given color
			colorSquare.setOnMouseClicked(m -> { try {
				OUTPUT.write(new byte[] {
					(byte) ClientRequest.USE_CARD.ordinal(),
					(byte) lastCardClicked,
					(byte) color.ordinal(),
					(byte) specialCardUsed.getValue().ordinal()
				});
				COLOR_CHOICES.setVisible(false);
			} catch (Exception e) {e.printStackTrace();} });
			COLOR_CHOICES.getChildren().add(colorSquare);
		}
		COLOR_CHOICES.setVisible(false);
		PANE.getChildren().add(COLOR_CHOICES);
		
		// Displays the turn indicator
		TURN_TEXT.setLayoutY(HAND.getLayoutY()+Card.HEIGHT+20);
		TURN_TEXT.setPrefWidth(WIDTH);
		TURN_TEXT.setTextFill(Color.WHITE);
		TURN_TEXT.setAlignment(Pos.CENTER);
		TURN_TEXT.setTextAlignment(TextAlignment.CENTER);
		TURN_TEXT.setFont(new Font("System Bold", 24));
		PANE.getChildren().add(TURN_TEXT);

		// Sets up the opponent hands
		OPPONENTS[0].setLayoutX(50);
		OPPONENTS[0].setLayoutY(50);
		OPPONENTS[1].setLayoutX(50);
		OPPONENTS[1].setLayoutY(50 + Card.HEIGHT + 50);
		OPPONENTS[2].setLayoutX(WIDTH - OpponentHand.WIDTH - 50);
		OPPONENTS[2].setLayoutY(50);
		OPPONENTS[3].setLayoutX(WIDTH - OpponentHand.WIDTH - 50);
		OPPONENTS[3].setLayoutY(50 + Card.HEIGHT + 50);
		for (OpponentHand hand : OPPONENTS) {
			hand.setVisible(false);
			PANE.getChildren().add(hand);
		}
		
		// Sets up the Uno button
		final Rectangle buttonBack = new Rectangle(40, 40, Color.gray(0.3));
		UNO_BUTTON.getChildren().add(buttonBack);
		final Label buttonText = new Label("U");
		buttonText.setPrefSize(40, 40);
		buttonText.setTextFill(Color.WHITE);
		buttonText.setAlignment(Pos.CENTER);
		buttonText.setTextAlignment(TextAlignment.CENTER);
		buttonText.setFont(new Font("System Bold", 24));
		UNO_BUTTON.getChildren().add(buttonText);
		UNO_BUTTON.setOnMouseEntered(m -> UNO_BUTTON.setCursor(Cursor.HAND));
		UNO_BUTTON.setOnMouseExited(m -> UNO_BUTTON.setCursor(Cursor.DEFAULT));
		UNO_BUTTON.setVisible(false);
		PANE.getChildren().add(UNO_BUTTON);
		
//		// Sets up the loose card
//		final Card card = new Card();
//		card.flip();
//		final LooseBox box = new LooseBox(Card.WIDTH, Card.HEIGHT, card);
//		PANE.getChildren().add(box);
		
		// Finalizes the stage setup
		stage.setScene(new Scene(PANE));
		stage.setOnCloseRequest(e -> System.exit(0));
		stage.setTitle("RaUno Client");
		stage.show();
	}
}
