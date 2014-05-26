package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import model.Pieces.EmptyPiece;
import model.Pieces.King;
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
	
	private Board(Board toCopy) {
		for(int row = 0; row < SIZE; row++) {
			for(int col = 0; col < SIZE; col++) {
				board[row][col] = toCopy.board[row][col];
			}
		}
	}
	
	public Piece pieceAt(Location loc) {
		return board[loc.getRow()][loc.getCol()];
	}
	
	public List<Location> getLocations(Color color) {
		List<Location> locations = new ArrayList<Location>();
		for(int row = 0; row < SIZE; row++) {
			for(int col = 0; col < SIZE; col++) {
				Piece p = board[row][col];
				if(color == null || p.getColor() == color) {
					locations.add(new Location(row, col));
				}
			}
		}
		return locations;
	}
	
	public List<Piece> getPieces(Color color) {
		List<Piece> pieces = new ArrayList<Piece>();
		List<Location> locations = getLocations(color);
		
		for(Location l : locations) {
			Piece p = pieceAt(l);
			if(p.getColor() == color) {
				pieces.add(p);
			}
		}
		return pieces;
	}
	
	public Board getCopy() {
		return new Board(this);
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
	
	public Location getKingLocation(Color color) {
		Location king = null;
		for(int row = 0; row < SIZE && king == null; row++) {
			for(int col = 0; col < SIZE && king == null; col++) {
				Piece piece = board[row][col];
				if(piece.getColor() == color && piece instanceof King) {
					king = new Location(row, col);
				}
			}
		}
		return king;
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
