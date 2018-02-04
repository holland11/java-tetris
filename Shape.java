enum Tetrominoes { None, S, Z, Line, Block, L, J, T }

public class Shape {
	
	private int[][] coords;
	private Tetrominoes pieceShape;
	private int minX = 0;
	private int minY = 0;
	private int maxX = 0;
	private int maxY = 0;
	
	public Shape() {
		setShape(Tetrominoes.None);
	}
	
	public Tetrominoes getShape() {
		return pieceShape;
	}
	
	public void setShape(Tetrominoes shape) {
		switch (shape) {
		case None:
			coords = new int[][]{ {0,0}, {0,0}, {0,0}, {0,0} };
			break;
		case S:
			coords = new int[][]{ {0,1}, {0,0}, {-1,0}, {-1,-1} };
			break;
		case Z:
			coords = new int[][]{ {-1,1}, {-1,0}, {0,0}, {0,-1} };
			break;
		case Line:
			coords = new int[][]{ {0,1}, {0,0}, {0,-1}, {0,-2} };
			break;
		case Block:
			coords = new int[][]{ {0,0}, {0,1}, {1,1}, {1,0} };
			break;
		case L:
			coords = new int[][]{ {1,1}, {0,1}, {0,0}, {0,-1} };
			break;
		case J:
			coords = new int[][]{ {-1,1}, {0,1}, {0,0}, {0,-1} };
			break;
		case T:
			coords = new int[][]{ {-1,0}, {0,0}, {0,-1}, {1,0} };
			break;
		}
		pieceShape = shape;
		setMinMax();
	}
	
	public void setMinMax() {
		// set minX, maxX and minY, maxY
		for (int i = 0; i < 4; i++) {
			if (coords[i][0] < minX)
				minX = coords[i][0];
			if (coords[i][1] < minY)
				minY = coords[i][1];
			
			if (coords[i][0] > maxX)
				maxX = coords[i][0];
			if (coords[i][1] > maxY)
				maxY = coords[i][1];
		}
	}
	
	public int getMaxX() {
		return maxX;
	}
	public int getMaxY() {
		return maxY;
	}
	public int getMinX() {
		return minX;
	}
	public int getMinY() {
		return minY;
	}
	
	public Tetrominoes setRandomShape() {
		int index = ((int)(Math.random() * 100) % 7) + 1;
		Tetrominoes[] tmp = Tetrominoes.values();
		setShape(tmp[index]);
		return pieceShape;
	}
	
	public void setX(int index, int val) {
		coords[index][0] = val;
	}
	public void setY(int index, int val) {
		coords[index][1] = val;
	}
	public int getX(int index) {
		return coords[index][0];
	}
	public int getY(int index) {
		return coords[index][1];
	}
	
	public Shape rotateLeft() {
		if (pieceShape == Tetrominoes.Block)
			return this;
		
		Shape result = new Shape();
		result.pieceShape = pieceShape;
		
		for (int i = 0; i < 4; i++) {
			int temp = coords[i][0];
			result.setX(i, -getY(i));
			result.setY(i, temp);
		}
		result.setMinMax();
		return result;
	}
	
	public Shape rotateRight() {
		if (pieceShape == Tetrominoes.Block)
			return this;
		
		Shape result = new Shape();
		result.pieceShape = pieceShape;
		
		for (int i = 0; i < 4; i++) {
			int temp = coords[i][0];
			result.setX(i, getY(i));
			result.setY(i, -temp);
		}
		result.setMinMax();
		return result;
	}
}
