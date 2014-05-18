package commands;

import model.Pieces.Piece;
import chess.Location;

public class Place extends Command{
	private final Location L;
	private final Piece P;
	
	public Place(Piece p, Location l) {
		P = p;
		L = l;	
		log = "Place " + P.getColor() + " " + P + " at " + L;
	}
	
	public Location getLocation() {
		return L;
	}
	
	public Piece getPiece() {
		return P;
	}
	
}
