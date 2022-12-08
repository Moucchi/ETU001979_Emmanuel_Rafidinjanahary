import java.awt.Dimension;

import java.awt.Font;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;

import java.util.Scanner;

import javax.swing.JFrame;

public class Game extends Inc implements Runnable {
    private String ip ;
	private int port ;
	private Scanner scanner = new Scanner(System.in);
	private JFrame frame;
	private final int WIDTH = 500;
	private final int HEIGHT = 500;
	private Thread thread;

	private Painter painter;
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;

	private ServerSocket serverSocket;

	private String[] spaces = new String[9];

	private boolean yourTurn = false;
	private boolean circle = true;
	private boolean accepted = false;
	private boolean unableToCommunicateWithOpponent = false;
	private boolean won = false;
	private boolean enemyWon = false;
	private boolean tie = false;

	private int lengthOfSpace;
	private int errors ;
	private int firstSpot ;
	private int secondSpot ;

	private String fontFamily ;
	private Font font ;
	private Font smallerFont ;
	private Font largerFont ;

	private String waitingString = "Waiting for your opponent to come";
	private String unableToCommunicateWithOpponentString = "Trying to communicate with the opponent.";
	private String wonString = "You won!";
	private String enemyWonString = "Opponent won!";
	private String tieString = "Game ended in a tie.";

	//Liste des alignements pour gagne
	private int[][] wins = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } }; 

	public Game() {
		System.out.println("\tInput the IP: ");
		ip = scanner.nextLine();
		System.out.println("\tInput the port: ");
		port = scanner.nextInt();

		this.setLengthOfSpace(160);
		this.setErrors(0);
		this.setFirstSpot(-1);
		this.setSecondSpot(-1);

		this.setFontFamily("Verdana");
		this.setFont( new Font( getFontFamily() , Font.BOLD , 32 ) );
		this.setSmallerFont(new Font( getFontFamily() , Font.BOLD , 20 ));
		this.setLargerFont(new Font( getFontFamily() , Font.BOLD , 50 ));

		while (port < 1 || port > 65535) {
			System.out.println("The port you entered was invalid, try another port: ");
			port = scanner.nextInt();
		}

		painter = new Painter(this);
		
		painter.loadImages();

		painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		if (!connect(this)) initializeServer(this);

		frame = new JFrame();
		frame.setTitle("Socket-Game");
		frame.setContentPane(painter);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		thread = new Thread(this, "sockety");
		thread.start();
	}

	public void run() {
		while (true) {
			tick(this);
			painter.repaint();

			if (!circle && !accepted) {
				listenForServerRequest(this);
			}

		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Scanner getScanner() {
		return scanner;
	}

	public void setScanner(Scanner scanner) {
		this.scanner = scanner;
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public int getWIDTH() {
		return WIDTH;
	}

	public int getHEIGHT() {
		return HEIGHT;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public Painter getPainter() {
		return painter;
	}

	public void setPainter(Painter painter) {
		this.painter = painter;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public DataOutputStream getDos() {
		return dos;
	}

	public void setDos(DataOutputStream dos) {
		this.dos = dos;
	}

	public DataInputStream getDis() {
		return dis;
	}

	public void setDis(DataInputStream dis) {
		this.dis = dis;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public String[] getSpaces() {
		return spaces;
	}

	public void setSpaces(String[] spaces) {
		this.spaces = spaces;
	}

	public boolean isYourTurn() {
		return yourTurn;
	}

	public void setYourTurn(boolean yourTurn) {
		this.yourTurn = yourTurn;
	}

	public boolean isCircle() {
		return circle;
	}

	public void setCircle(boolean circle) {
		this.circle = circle;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}

	public boolean isUnableToCommunicateWithOpponent() {
		return unableToCommunicateWithOpponent;
	}

	public void setUnableToCommunicateWithOpponent(boolean unableToCommunicateWithOpponent) {
		this.unableToCommunicateWithOpponent = unableToCommunicateWithOpponent;
	}

	public boolean isWon() {
		return won;
	}

	public void setWon(boolean won) {
		this.won = won;
	}

	public boolean isEnemyWon() {
		return enemyWon;
	}

	public void setEnemyWon(boolean enemyWon) {
		this.enemyWon = enemyWon;
	}

	public boolean isTie() {
		return tie;
	}

	public void setTie(boolean tie) {
		this.tie = tie;
	}

	public int getLengthOfSpace() {
		return lengthOfSpace;
	}

	public void setLengthOfSpace(int lengthOfSpace) {
		this.lengthOfSpace = lengthOfSpace;
	}

	public int getErrors() {
		return errors;
	}

	public void setErrors(int errors) {
		this.errors = errors;
	}

	public int getFirstSpot() {
		return firstSpot;
	}

	public void setFirstSpot(int firstSpot) {
		this.firstSpot = firstSpot;
	}

	public int getSecondSpot() {
		return secondSpot;
	}

	public void setSecondSpot(int secondSpot) {
		this.secondSpot = secondSpot;
	}

	public String getFontFamily() {
		return fontFamily;
	}

	public void setFontFamily(String fontFamily) {
		this.fontFamily = fontFamily;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Font getSmallerFont() {
		return smallerFont;
	}

	public void setSmallerFont(Font smallerFont) {
		this.smallerFont = smallerFont;
	}

	public Font getLargerFont() {
		return largerFont;
	}

	public void setLargerFont(Font largerFont) {
		this.largerFont = largerFont;
	}

	public String getWaitingString() {
		return waitingString;
	}

	public void setWaitingString(String waitingString) {
		this.waitingString = waitingString;
	}

	public String getUnableToCommunicateWithOpponentString() {
		return unableToCommunicateWithOpponentString;
	}

	public void setUnableToCommunicateWithOpponentString(String unableToCommunicateWithOpponentString) {
		this.unableToCommunicateWithOpponentString = unableToCommunicateWithOpponentString;
	}

	public String getWonString() {
		return wonString;
	}

	public void setWonString(String wonString) {
		this.wonString = wonString;
	}

	public String getEnemyWonString() {
		return enemyWonString;
	}

	public void setEnemyWonString(String enemyWonString) {
		this.enemyWonString = enemyWonString;
	}

	public String getTieString() {
		return tieString;
	}

	public void setTieString(String tieString) {
		this.tieString = tieString;
	}

	public int[][] getWins() {
		return wins;
	}

	public void setWins(int[][] wins) {
		this.wins = wins;
	}
}
