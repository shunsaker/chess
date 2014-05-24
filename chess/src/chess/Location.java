package chess;

import model.Board;

public class Location {
	private final int ROW, COL;
	private final String LOC_STRING;
	
	public Location(String locString) {
		LOC_STRING = locString;
		ROW = Board.SIZE - (locString.charAt(1) - '0');
		COL = locString.charAt(0) - 'a';
		
	}
	
	public Location(Location origin, RelativeLocation offset) {
		this(origin.getRow() + offset.getRow(), origin.getCol() + offset.getCol());
	}
	
	public Location(int row, int col) {
		ROW = row;
		COL = col;
		LOC_STRING = (char)('a' + COL) + "" + (char)('0' + (Board.SIZE - ROW));
	}
	
	public int getRow() {
		return ROW;
	}
	
	public int getCol() {
		return COL;
	}
	
	@Override
	public String toString() {
		return LOC_STRING;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean equals = false;
		if(o instanceof Location) {
			Location other = (Location) o;
			equals = ROW == other.getRow() && COL == other.getCol();
		}
		return equals;
	}
}
