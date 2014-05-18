package model;

import java.util.Iterator;

import model.Pieces.EmptyPiece;
import model.Pieces.Piece;
import chess.Location;


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
	
	public void place(Piece piece, Location to) {
		board[to.getRow()][to.getCol()] = piece;
	}
	
	public void move(Location from, Location to) {
		Piece temp = board[to.getRow()][to.getCol()];
		board[to.getRow()][to.getCol()] = board[from.getRow()][from.getCol()];
		board[from.getRow()][from.getCol()] = temp;
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
