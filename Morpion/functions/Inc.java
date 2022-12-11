package functions;

import java.awt.BasicStroke;
import java.awt.Color;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

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
		if (game.isUnableToCommunicateWithOpponent()) {
			g.setColor(Color.RED);
			g.setFont(game.getSmallerFont());
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(game.getUnableToCommunicateWithOpponentString());
			g.drawString(game.getUnableToCommunicateWithOpponentString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
			return;
		}

		if (game.isAccepted()) {
			for (int i = 0; i < game.getSpaces().length; i++) {
				if (game.getSpaces()[i] != null) {
					if (game.getSpaces()[i].equals("X")) {
						if (game.isCircle()) {
							g.drawImage(game.getPainter().getRedX(), (i % 3) * game.getLengthOfSpace() + 10 * (i % 3), (int) (i / 3) * game.getLengthOfSpace() + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(game.getPainter().getBlueX(), (i % 3) * game.getLengthOfSpace() + 10 * (i % 3), (int) (i / 3) * game.getLengthOfSpace() + 10 * (int) (i / 3), null);
						}
					} else if (game.getSpaces()[i].equals("O")) {
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
				g.setColor(Color.BLACK);
				g.drawLine(game.getFirstSpot() % 3 * game.getLengthOfSpace() + 10 * game.getFirstSpot() % 3 + game.getLengthOfSpace() / 2, (int) (game.getFirstSpot() / 3) * game.getLengthOfSpace() + 10 * (int) (game.getFirstSpot() / 3) + game.getLengthOfSpace() / 2, game.getSecondSpot() % 3 * game.getLengthOfSpace() + 10 * game.getSecondSpot() % 3 + game.getLengthOfSpace() / 2, (int) (game.getSecondSpot() / 3) * game.getLengthOfSpace() + 10 * (int) (game.getSecondSpot() / 3) + game.getLengthOfSpace() / 2);

				g.setColor(Color.RED);
				g.setFont(game.getLargerFont());
				if (game.isWon()) {
					int stringWidth = g2.getFontMetrics().stringWidth(game.getWonString());
					g.drawString(game.getWonString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
				} else if (game.isEnemyWon()) {
					int stringWidth = g2.getFontMetrics().stringWidth(game.getEnemyWonString());
					g.drawString(game.getEnemyWonString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
				}
			}
			if (game.isTie()) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.BLACK);
				g.setFont(game.getLargerFont());
				int stringWidth = g2.getFontMetrics().stringWidth(game.getTieString());
				g.drawString(game.getTieString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
			}
		} else {
			g.setColor(Color.RED);
			g.setFont(game.getFont());
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(game.getWaitingString());
			g.drawString(game.getWaitingString(), game.getWIDTH() / 2 - stringWidth / 2, game.getHEIGHT() / 2);
		}

	}

	public void tick(Game game) {
		if (game.getErrors() >= 10) game.setUnableToCommunicateWithOpponent(true);

		if (!game.isYourTurn() && !game.isUnableToCommunicateWithOpponent()) {
			try {
				int space = game.getDis().readInt();
				if (game.isCircle()) game.getSpaces()[space] = "X";
				else game.getSpaces()[space] = "O";
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
				if (game.getSpaces()[game.getWins()[i][0]] == "O" && game.getSpaces()[game.getWins()[i][1]] == "O" && game.getSpaces()[game.getWins()[i][2]] == "O") {
					game.setFirstSpot(game.getWins()[i][0]);
					game.setSecondSpot(game.getWins()[i][2]);
					game.setWon(true);
				}
			} else {
				if (game.getSpaces()[game.getWins()[i][0]] == "X" && game.getSpaces()[game.getWins()[i][1]] == "X" && game.getSpaces()[game.getWins()[i][2]] == "X") {
					game.setFirstSpot(game.getWins()[i][0]);
					game.setSecondSpot(game.getWins()[i][2]);
					game.setWon(true);
				}
			}
		}
	}

	public void checkForEnemyWin(Game game) {
		for (int i = 0; i < game.getWins().length; i++) {
			if (game.isCircle()) {
				if (game.getSpaces()[game.getWins()[i][0]] == "X" && game.getSpaces()[game.getWins()[i][1]] == "X" && game.getSpaces()[game.getWins()[i][2]] == "X") {
					game.setFirstSpot(game.getWins()[i][0]); 
					game.setSecondSpot(game.getWins()[i][2]); 
					game.setEnemyWon(true);
				}
			} else {
				if (game.getSpaces()[game.getWins()[i][0]] == "O" && game.getSpaces()[game.getWins()[i][1]] == "O" && game.getSpaces()[game.getWins()[i][2]] == "O") {
					game.setFirstSpot(game.getWins()[i][0]); 
					game.setSecondSpot(game.getWins()[i][2]); 
					game.setEnemyWon(true);
				}
			}
		}
	}

	public void checkForTie(Game game) {
		for (int i = 0; i < game.getSpaces().length; i++) {
			if (game.getSpaces()[i] == null) {
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
			System.out.println("CLIENT IS CONNECTED");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean connect(Game game) {
		try {
			game.setSocket(new Socket(game.getIp(), game.getPort()));
			game.setDos(new DataOutputStream(game.getSocket().getOutputStream()));
			game.setDis(new DataInputStream(game.getSocket().getInputStream()));
			game.setAccepted(true);
		} catch (IOException e) {
			System.out.println("Trying to connect to the address: " + game.getIp() + ":" + game.getPort() + " | Starting a server");
			return false;
		}
		System.out.println("Successfully connected to the server.");
		return true;
	}

	public void initializeServer(Game game) {
		try {
			game.setServerSocket(new ServerSocket(game.getPort(), 8, InetAddress.getByName(game.getIp())));
		} catch (Exception e) {
			e.printStackTrace();
		}
		game.setYourTurn(true); 
		game.setCircle(false); 
	}

}
