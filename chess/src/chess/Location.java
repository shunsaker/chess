package chess;

import model.Board;

public class Location {
	private final int row, col;
	private final String locString;
	
	public Location(String locString) {
		this.locString = locString;
		row = Board.SIZE - (locString.charAt(1) - '0');
		col = locString.charAt(0) - 'a';
		
	}
	
	public Location(int row, int col) {
		this.row = row;
		this.col = col;
		locString = ('A' + row) + "" + ('0' + col);
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	@Override
	public String toString() {
		return locString;
	}
}
