package model;

import java.util.Iterator;

import model.Pieces.EmptyPiece;
import model.Pieces.Piece;
import chess.Location;
import chess.RelativeLocation;


public class Board implements Iterable<Piece>{
	public static final int SIZE = 8;
	public static final Piece EMPTY = new EmptyPiece();
	private Piece[][] board = new Piece[SIZE][SIZE];
	
	public Board() {
		for(int row = 0; row < SIZE; row++) {
			for(int col = 0; col < SIZE; col++) {
				board[row][col] = EMPTY;
			}
		}
	}
	
	public Piece pieceAt(Location loc) {
		return board[loc.getRow()][loc.getCol()];
	}
	
	public void place(Piece piece, Location to) {
		board[to.getRow()][to.getCol()] = piece;
	}
	
	public void move(Location from, Location to) {
		Piece temp = board[to.getRow()][to.getCol()];
		board[to.getRow()][to.getCol()] = board[from.getRow()][from.getCol()];
		board[from.getRow()][from.getCol()] = temp;
	}
	
	public void capture(Location from, Location to) {
		board[to.getRow()][to.getCol()] = board[from.getRow()][from.getCol()];
		board[from.getRow()][from.getCol()] = EMPTY;
	}
	
	public boolean isPathClear(Location from, Location to) {
		boolean clear = false;
		if(isConnected(from, to)){
			clear = true;
			RelativeLocation increment = getIncrementer(from, to);
			Location loc = to;
			do {
				loc = new Location(loc, increment);
				if(pieceAt(loc) != EMPTY && !loc.equals(from)) {
					clear = false;
				}
			}
			while(!loc.equals(from));
		}
		
		return clear;
	}
	
	private RelativeLocation getIncrementer(Location from, Location to) {
		int rowDist = from.getRow() - to.getRow();
		int colDist = from.getCol() - to.getCol();
		
		int rowIncrement = 0;
		if(rowDist != 0) {
			rowIncrement = rowDist / Math.abs(rowDist);
		}
		
		int colIncrement = 0;
		if(colDist != 0) {
			colIncrement = colDist / Math.abs(colDist);
		}
		
		return new RelativeLocation(rowIncrement, colIncrement);
		
	}
	
	private boolean isConnected(Location from, Location to) {
		int rowDist = from.getRow() - to.getRow();
		int colDist = from.getCol() - to.getCol();
		boolean diagonal = Math.abs(rowDist) == Math.abs(colDist);
		boolean straight = rowDist == 0 || colDist == 0;
		return diagonal || straight;
	}

	@Override
	public Iterator<Piece> iterator() {
		return new BoardIterator();
	}
	
	private class BoardIterator implements Iterator<Piece> {
		private int index;
		
		@Override
		public boolean hasNext() {
			return index < SIZE * SIZE;
		}

		@Override
		public Piece next() {
			return board[index / SIZE][index++ % SIZE];
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}		
	}
}
