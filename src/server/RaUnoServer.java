package server;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Handles a server for the RaUno game.
 * @author Raul Hernandez, 12/25/2023
 */
public class RaUnoServer {
	
	/** The port on which the server is hosted. */
	private static final int PORT = 29175;
	
	/** The number of players to include in a game. */
	private static final int PLAYERS = 1;
	
	/** The server socket. */
	private static ServerSocket SERVER;
	
	public static void main(String[] args) throws Exception {

		// Initializes the server
		System.out.println("Setting up the server...");
		SERVER = new ServerSocket(PORT, 100);
		
		new Thread() {
			public void run() { try { while (true) {
				System.out.println("Waiting for players to connect...");
				final Socket[] players = new Socket[PLAYERS];
				for (int i = 0; i < PLAYERS; i++)
					players[i] = SERVER.accept();
				final RaUnoSession session = new RaUnoSession(players);
				while (!session.isClosed()) Thread.sleep(5_000);
			}} catch (Exception e) {e.printStackTrace();}}
		}.start();
	}
}
