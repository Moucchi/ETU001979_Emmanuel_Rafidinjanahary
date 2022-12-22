package core;

import java.awt.Dimension;
import java.awt.Font;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import javax.swing.JFrame;

import elements.Painter;
import functions.Inc;

public class Game extends Inc implements Runnable {
    private String ip ;
	private int port ;
	private Scanner scanner;
	private JFrame frame;
	private final int WIDTH = 520;
	private final int HEIGHT = 520;
	private Thread thread;

	private Painter painter;
	private Socket socket;
	private DataOutputStream dos;
	private DataInputStream dis;

	private ServerSocket serverSocket;

	private String[] cases ;

	private boolean yourTurn ;
	private boolean circle ;
	private boolean accepted ;
	private boolean linkedToOpponent ;
	private boolean won ;
	private boolean enemyWon ;
	private boolean tie ;

	private int lengthOfSpace ;
	private int errors ;
	private int beginning ;
	private int ending ;

	private String fontFamily ;
	private Font font ;

	private String waitingString ;
	private String wonString ;
	private String enemyWonString ;
	private String tieString ;

	private int[][] wins ;

	public Game() {
		this.setScanner(new Scanner(System.in));
		System.out.println("Enter an IP adress: ");
		this.setIp(scanner.nextLine());
		System.out.println("Enter an avalable port: ");
		this.setPort(scanner.nextInt());
		while (port < 1 || port > 65535) {
			System.out.println("The port you entered is invalid , please input another port: ");
			this.setPort(scanner.nextInt());
		}

		this.setCases(new String[9]);
		this.setYourTurn(false);
		this.setCircle(true);
		this.setAccepted(false);
		this.setLinkedToOpponent(false);
		this.setWon(false);
		this.setEnemyWon(false);
		this.setTie(false);

		this.setLengthOfSpace(160);
		this.setErrors(0);
		this.setBeginning(0);
		this.setEnding(0);

		this.setFontFamily("Verdana");
		this.setFont(new Font(this.getFontFamily(), Font.BOLD, 32));

		this.setWaitingString("Waiting for opponent");
		this.setWonString("Victory");
		this.setEnemyWonString("Defeat");
		this.setTieString("Dead end");

		this.setWins(new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } });

		painter = new Painter(this);
		
		painter.loadImages();

		painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		if (!connectClient(this)) createServer(this);

		frame = new JFrame();
		frame.setTitle("Morpion");
		frame.setContentPane(painter);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setVisible(true);

		this.setThread(new Thread(this, "ProjetSokety"));
		this.getThread().start();
	}

	public void run() {
		while (!won || !enemyWon) {
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

	public String getWaitingString() {
		return waitingString;
	}

	public void setWaitingString(String waitingString) {
		this.waitingString = waitingString;
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

	public boolean isLinkedToOpponent() {
		return linkedToOpponent;
	}

	public void setLinkedToOpponent(boolean linkedToOpponent) {
		this.linkedToOpponent = linkedToOpponent;
	}

	public String[] getCases() {
		return cases;
	}

	public void setCases(String[] cases) {
		this.cases = cases;
	}

	public int getBeginning() {
		return beginning;
	}

	public void setBeginning(int beginning) {
		this.beginning = beginning;
	}

	public int getEnding() {
		return ending;
	}

	public void setEnding(int ending) {
		this.ending = ending;
	}

	
}
