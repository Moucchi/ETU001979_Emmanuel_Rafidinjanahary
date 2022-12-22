package functions;

import java.awt.BasicStroke;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import core.Game;

public class Inc {
    public void render(Graphics g , Game game) {
		g.drawImage(game.getPainter().getBoard(), 0, 0, null);

		if (game.isAccepted()) {
			for (int i = 0; i < game.getCases().length; i++) {
				if (game.getCases()[i] != null) {
					if (game.getCases()[i].equals("X")) {
						if (game.isCircle()) {
							g.drawImage(game.getPainter().getRedX(), (i % 3) * game.getLengthOfSpace() + 10 * (i % 3), (int) (i / 3) * game.getLengthOfSpace() + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(game.getPainter().getBlueX(), (i % 3) * game.getLengthOfSpace() + 10 * (i % 3), (int) (i / 3) * game.getLengthOfSpace() + 10 * (int) (i / 3), null);
						}
					} else if (game.getCases()[i].equals("O")) {
						if (game.isCircle()) {
							g.drawImage(game.getPainter().getBlueCircle(), (i % 3) * game.getLengthOfSpace() + 10 * (i % 3), (int) (i / 3) * game.getLengthOfSpace() + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(game.getPainter().getRedCircle(), (i % 3) * game.getLengthOfSpace() + 10 * (i % 3), (int) (i / 3) * game.getLengthOfSpace() + 10 * (int) (i / 3), null);
						}
					}
				}
			}
			if (game.isWon() || game.isEnemyWon()) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(10));
				g.setColor(Color.WHITE);
				g.drawLine(game.getBeginning() % 3 * game.getLengthOfSpace() + 10 * game.getBeginning() % 3 + game.getLengthOfSpace() / 2, (int) (game.getBeginning() / 3) * game.getLengthOfSpace() + 10 * (int) (game.getBeginning() / 3) + game.getLengthOfSpace() / 2, game.getEnding() % 3 * game.getLengthOfSpace() + 10 * game.getEnding() % 3 + game.getLengthOfSpace() / 2, (int) (game.getEnding() / 3) * game.getLengthOfSpace() + 10 * (int) (game.getEnding() / 3) + game.getLengthOfSpace() / 2);

				g.setColor(Color.WHITE);
				g.setFont(game.getFont());
				
				if (game.isWon()) {
					int stringWidth = g2.getFontMetrics().stringWidth(game.getWonString());
					g.drawString(game.getWonString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
				} else if (game.isEnemyWon()) {
					int stringWidth = g2.getFontMetrics().stringWidth(game.getEnemyWonString());
					g.drawString(game.getEnemyWonString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
				}
			}
			if (game.isTie() && !game.isEnemyWon() && !game.isWon()) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.WHITE);
				g.setFont(game.getFont());
				int stringWidth = g2.getFontMetrics().stringWidth(game.getTieString());
				g.drawString(game.getTieString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
			}
		} else {
			g.setColor(Color.WHITE);
			g.setFont(game.getFont());
			Graphics2D g2 = (Graphics2D) g;
			int stringWidth = g2.getFontMetrics().stringWidth(game.getWaitingString());
			g.drawString(game.getWaitingString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
		}

	}

	public void tick(Game game) {
		if (game.getErrors() >= 10) game.setLinkedToOpponent(false);

		if (!game.isYourTurn() && game.isLinkedToOpponent()) {
			try {
				int space = game.getDis().readInt();
				if (game.isCircle()) game.getCases()[space] = "X";
				else game.getCases()[space] = "O";
				checkForEnemyWin(game);
				checkForTie(game);
				game.setYourTurn(true);
			} catch (IOException e) {
				e.printStackTrace();
				game.setErrors(game.getErrors() + 1);
			}
		}
	}

	public void checkForWin(Game game) {
		for (int i = 0; i < game.getWins().length; i++) {
			if (game.isCircle()) {
				if (game.getCases()[game.getWins()[i][0]] == "O" && game.getCases()[game.getWins()[i][1]] == "O" && game.getCases()[game.getWins()[i][2]] == "O") {
					game.setBeginning(game.getWins()[i][0]);
					game.setEnding(game.getWins()[i][2]);
					game.setWon(true);
				}
			} else {
				if (game.getCases()[game.getWins()[i][0]] == "X" && game.getCases()[game.getWins()[i][1]] == "X" && game.getCases()[game.getWins()[i][2]] == "X") {
					game.setBeginning(game.getWins()[i][0]);
					game.setEnding(game.getWins()[i][2]);
					game.setWon(true);
				}
			}
		}
	}

	public void checkForEnemyWin(Game game) {
		for (int i = 0; i < game.getWins().length; i++) {
			if (game.isCircle()) {
				if (game.getCases()[game.getWins()[i][0]] == "X" && game.getCases()[game.getWins()[i][1]] == "X" && game.getCases()[game.getWins()[i][2]] == "X") {
					game.setBeginning(game.getWins()[i][0]); 
					game.setEnding(game.getWins()[i][2]); 
					game.setEnemyWon(true);
				}
			} else {
				if (game.getCases()[game.getWins()[i][0]] == "O" && game.getCases()[game.getWins()[i][1]] == "O" && game.getCases()[game.getWins()[i][2]] == "O") {
					game.setBeginning(game.getWins()[i][0]); 
					game.setEnding(game.getWins()[i][2]); 
					game.setEnemyWon(true);
				}
			}
		}
	}

	public void checkForTie(Game game) {
		for (int i = 0; i < game.getCases().length; i++) {
			if (game.getCases()[i] == null) {
				return;
			}
		}
		game.setTie(true);
	}

	public void listenForServerRequest(Game game) {
		Socket socket = null;
		try {
			socket = game.getServerSocket().accept();
			game.setDos(new DataOutputStream(socket.getOutputStream()));
			game.setDis(new DataInputStream(socket.getInputStream()));
			game.setAccepted(true);
			System.out.println("Client successfully connected ... ");
		} catch (IOException e) {
			System.err.println( "Client failed to connect to the designed server  ... " );
			game = new Game();
		}
	}

	public boolean connectClient(Game game) {
		try {
			game.setSocket(new Socket(game.getIp(), game.getPort()));
			game.setDos(new DataOutputStream(game.getSocket().getOutputStream()));
			game.setDis(new DataInputStream(game.getSocket().getInputStream()));
			game.setAccepted(true);
			game.setLinkedToOpponent(true);
		} catch (IOException e) {
			System.out.println("Starting a server : '" + game.getIp() + "' on port : " + game.getPort() );
			return false;
		}
		System.out.println("Successfully connected to the server.");
		return true;
	}

	public void createServer(Game game) {
		try {
			game.setServerSocket(new ServerSocket(game.getPort(), 8, InetAddress.getByName(game.getIp())));
			game.setLinkedToOpponent(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
		game.setYourTurn(true); 
		game.setCircle(false); 
	}

}
