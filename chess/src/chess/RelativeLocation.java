package chess;

public class RelativeLocation {
	private final int ROW, COL;
	
	public RelativeLocation(int row, int col) {
		ROW = row;
		COL = col;
	}
	
	public int getRow() {
		return ROW;
	}
	
	public int getCol() {
		return COL;
	}
	
	@Override
	public String toString() {
		return "[" + ROW + ", " + COL + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		boolean equals = false;
		if (o instanceof RelativeLocation) {
			RelativeLocation other = (RelativeLocation) o;
			equals = other.COL == COL && other.ROW == ROW;
		}
		return equals;
	}
	
	@Override
	public int hashCode() {
		return ROW * 10 + COL;
	}
}
