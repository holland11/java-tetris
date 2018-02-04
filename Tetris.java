import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;


public class Tetris extends JFrame implements KeyListener {

	private Board board;
	
	public Tetris() {
		initFrame();
	}
	
	public void initFrame() {
		board = new Board();
		add(board);
		
		setTitle("Tetris - Patrick Holland");
		setResizable(false);
		setFocusable(true);
		addKeyListener(this);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Tetris t = new Tetris();
				t.setVisible(true);
			}
		});
	}

	public void keyTyped(KeyEvent e) {

	}

	public void keyPressed(KeyEvent e) {
		board.keyPressed(e);
	}

	public void keyReleased(KeyEvent e) {
		board.keyReleased(e);
	}
}
