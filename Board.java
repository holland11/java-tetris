import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;


public class Board extends JPanel implements Runnable {
	
	/*
	 * n.next = p.next;
	 * n.next.prev = n;
	 * n.prev = p;
	 * p.next = n;
	 */
	
	private int blocksWide = 10;
	private int blocksHigh = 22;
	private int blockSize = 20;
	private int boardX = blocksWide * blockSize;
	private int boardY = blocksHigh * blockSize;
	private int boardStartY = 20;
	private int boardStartX = 70;
	private int windowX = boardX + boardStartX;
	private int windowY = boardY + boardStartY;
	private Tetrominoes[] board;
	private Thread thread;
	private Shape curShape;
	private Tetrominoes pieceShape;
	private int curX;
	private int curY;
	private int score;
	private boolean gameOn = false;
	private long lastTime;
	private long tickTime = 400; // drop piece every tickTime ms
	private Shape nextShape;
	private boolean pause;
	
	
	public Board() {
		initBoard();
	}
	
	public void initBoard() {
		setPreferredSize(new Dimension(windowX, windowY));
		board = new Tetrominoes[blocksWide * blocksHigh];
		clearBoard();
		
		newShape();
		gameOn = true;
		lastTime = System.currentTimeMillis();
		
		thread = new Thread(this);
		thread.start();
	}
	
	private void newShape() {
		if (nextShape == null) {
			nextShape = new Shape();
			nextShape.setRandomShape();
		}
		curShape = nextShape;
		pieceShape = curShape.getShape();
		curX = (blocksWide / 2 - 1);
		curY =  - curShape.getMinY();
		if (!tryMove(curShape, curX, curY)) {
			gameOn = false;
			curShape = null;
		}
		nextShape = new Shape();
		nextShape.setRandomShape();
	}

	
	public void clearBoard() {
		for (int i = 0, n = board.length; i < n; i++) {
			board[i] = Tetrominoes.None;
		}
	}
	
	public void drawBoard(Graphics g) {
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(0, 0, windowX, windowY);
		g.setColor(Color.white);
		g.fillRect(boardStartX, boardStartY, boardX, boardY);
		
		for (int i = 0, n = board.length; i < n; i++) {
			drawBlock(g, board[i], (i % blocksWide) * blockSize + boardStartX, (i / blocksWide) * blockSize + boardStartY);
		}
		for (int i = 0; i < 4; i++) {
			if (curShape != null) {
			drawBlock(g, pieceShape, curX * blockSize + (curShape.getX(i) * blockSize) + boardStartX, curY * blockSize + (curShape.getY(i) * blockSize) + boardStartY);
			}
			drawBlock(g, nextShape.getShape(), boardStartX / 3 + (nextShape.getX(i) * blockSize), boardStartY + (nextShape.getMinY() * -blockSize) + (nextShape.getY(i) * blockSize));
		}
	}
	
	public void drawBlock(Graphics g, Tetrominoes shape, int x, int y) {
		Color[] colors = {Color.black, Color.cyan, Color.red, Color.MAGENTA, Color.pink, Color.BLUE, Color.GRAY, Color.yellow };
		
		g.setColor(Color.white);
		g.drawRect(x, y, blockSize, blockSize);
		
		g.setColor(colors[shape.ordinal()]);
		g.fillRect(x+1, y+1, blockSize-1, blockSize-1);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (gameOn) {
		drawBoard(g);
		}
		else {
			drawBoard(g);
			drawGameOver(g);
		}
		drawMenu(g);
	}
	
	public void drawMenu(Graphics g) {
		g.setColor(Color.black);
		g.drawString("Score: "+score, 3, windowY-50);
	}
	
	public void drawGameOver(Graphics g) {
		String text = "Spacebar for New Game";
		int width = g.getFontMetrics().stringWidth(text);
		g.setColor(Color.white);
		g.fillRect((blocksWide * blockSize)/2 - (width/2) - 3 + boardStartX, (blocksHigh * blockSize)/2 - 10 + boardStartY, width+6, 20);
		g.setColor(Color.black);
		g.drawString(text, (blocksWide * blockSize)/2 - (width/2) + boardStartX, (blocksHigh * blockSize)/2 + 4 + boardStartY);
	}
	
	public void updateGame() {
		dropShape();
		clearFullLines();
		
	}
	
	public void dropShape() {
		if (tryMove(curShape, curX, curY + 1)) {
		}
		else {
			dockPiece();
			newShape();
		}
	}
	
	public boolean tryMove(Shape newShape, int newX, int newY) {
		// make sure new position doesn't go past the "floor"
		if ((newY + newShape.getMaxY()) >= blocksHigh)
			return false;
		// make sure new position doesn't try to breach one of the "walls"
		if ((newX + newShape.getMinX()) < 0 || (newX + newShape.getMaxX()) >= blocksWide)
			return false;
		
		// make sure new position doesn't conflict with any blocks already docked in the board[]
		for (int i = 0; i < 4; i++) {
			if (board[((newY + newShape.getY(i)) * blocksWide) + (newX + newShape.getX(i))] != Tetrominoes.None)
				return false;
		}
		
		// new position is valid. accept the move
		curShape = newShape;
		curX = newX;
		curY = newY;
		repaint();
		return true;
	}
	
	public void dockPiece() {
		for (int i = 0; i < 4; i++) {
			board[((curY + curShape.getY(i)) * blocksWide) + (curX + curShape.getX(i))] = pieceShape; 
		}
	}
	
	public void clearFullLines() {
		for (int i = blocksHigh-1; i >= 0; i--) {
			boolean full = true;
			for (int j = blocksWide-1; j >= 0; j--) {
				if (board[(i * blocksWide) + j] == Tetrominoes.None) {
					full = false;
					break;
				}
			}
			if (full) {
				score++;
				tickTime -= 5;
				clearLine(i);
			}
		}
	}
	
	public void clearLine(int index) {
		// drop all blocks down 1 row that are above the index'th line
		for (int i = (index*blocksWide)+(blocksWide-1); i > blocksWide-1; i--) {
			board[i] = board[i-blocksWide];
		}
		// make sure top row has been emptied
		for (int i = 0; i < blocksWide; i++) {
			board[i] = Tetrominoes.None;
		}
	}

	
	public void newGame() {
		gameOn = true;
		newShape();
		clearBoard();
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		gameLoop();
	}
	
	public void gameLoop() {
		while (gameOn) {
			long tempTime = System.currentTimeMillis();
			long tick = tempTime - lastTime;
			long wait = 0;
			if (tick >= 0 && tick < tickTime)
				wait = tickTime - tick;
			lastTime = tempTime;
			try {
				Thread.sleep(wait);
			} catch (InterruptedException e) {
			}
			if (!pause) {
				updateGame();
			}
			repaint();
		}
	}

	public void keyPressed(KeyEvent e) {	
		switch (e.getKeyCode()) {
			case (KeyEvent.VK_P):
				pause = !pause;
				break;
			case (KeyEvent.VK_LEFT):
			case (KeyEvent.VK_A):
				if (gameOn) {
					tryMove(curShape, curX-1, curY);
				}
				break;
			case (KeyEvent.VK_RIGHT):
			case (KeyEvent.VK_D):
				if (gameOn) {
					tryMove(curShape, curX+1, curY);
				}
				break;
			case (KeyEvent.VK_UP):
			case (KeyEvent.VK_W):
				if (gameOn) {
					tryMove(curShape.rotateLeft(), curX, curY);
				}
				break;
			case (KeyEvent.VK_DOWN):
			case (KeyEvent.VK_S):
				if (gameOn) {
					tryMove(curShape.rotateRight(), curX, curY);
				}
				break;
			case (KeyEvent.VK_SPACE):
				if (!gameOn) {
					gameOn = true;
					newGame();
				}
				else {
					tryMove(curShape, curX, curY+1);
				}
		}
	}
	
	public void keyReleased(KeyEvent e) {
		
	}
}




