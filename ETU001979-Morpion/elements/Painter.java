package elements;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import core.Game;
import functions.Inc;

public class Painter extends JPanel implements MouseListener {
    private BufferedImage board;
	private BufferedImage redX;
	private BufferedImage blueX;
	private BufferedImage redCircle;
	private BufferedImage blueCircle;

    Inc inc;
    Game game;

    public Painter(Game game) {
        setFocusable(true);
        requestFocus();
        setBackground(Color.DARK_GRAY);
        addMouseListener(this);
        setInc(new Inc());
        this.setGame(game);
    }

    public void loadImages() {
		try {
			board = ImageIO.read(getClass().getResourceAsStream("../images/board.png"));
			redX = ImageIO.read(getClass().getResourceAsStream("../images/redX.png"));
			redCircle = ImageIO.read(getClass().getResourceAsStream("../images/redCircle.png"));
			blueX = ImageIO.read(getClass().getResourceAsStream("../images/blueX.png"));
			blueCircle = ImageIO.read(getClass().getResourceAsStream("../images/blueCircle.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        inc.render(g,game);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (game.isAccepted()) {
            if (game.isYourTurn() && game.isLinkedToOpponent() && !game.isWon() && !game.isEnemyWon()) {
                int x = e.getX() / game.getLengthOfSpace();
                int y = e.getY() / game.getLengthOfSpace();
                y *= 3;
                int position = x + y;

                if (game.getCases()[position] == null) {
                    if (!game.isCircle()) game.getCases()[position] = "X";
                    else game.getCases()[position] = "O";
                    game.setYourTurn(false);
                    repaint();

                    try {
                        game.getDos().writeInt(position);
                        game.getDos().flush();
                    } catch (IOException e1) {
                        game.setErrors(game.getErrors() + 1);
                        e1.printStackTrace();
                    }

                    inc.checkForWin(game);
                    inc.checkForTie(game);

                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public BufferedImage getBoard() {
        return board;
    }

    public void setBoard(BufferedImage board) {
        this.board = board;
    }

    public BufferedImage getRedX() {
        return redX;
    }

    public void setRedX(BufferedImage redX) {
        this.redX = redX;
    }

    public BufferedImage getBlueX() {
        return blueX;
    }

    public void setBlueX(BufferedImage blueX) {
        this.blueX = blueX;
    }

    public BufferedImage getRedCircle() {
        return redCircle;
    }

    public void setRedCircle(BufferedImage redCircle) {
        this.redCircle = redCircle;
    }

    public BufferedImage getBlueCircle() {
        return blueCircle;
    }

    public void setBlueCircle(BufferedImage blueCircle) {
        this.blueCircle = blueCircle;
    }

    public Inc getInc() {
        return inc;
    }

    public void setInc(Inc inc) {
        this.inc = inc;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

}
