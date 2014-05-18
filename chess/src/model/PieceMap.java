package model;

import java.util.HashMap;
import java.util.Map;

import model.Pieces.Piece;
import model.factories.*;

public class PieceMap {
	private static final Map<String, String> PIECE_NAME_MAP = new HashMap<String, String>();
	static {
		PIECE_NAME_MAP.put("r", "Rook");
		PIECE_NAME_MAP.put("n", "Knight");
		PIECE_NAME_MAP.put("q", "Queen");
		PIECE_NAME_MAP.put("b", "Bishop");
		PIECE_NAME_MAP.put("p", "Pawn");
		PIECE_NAME_MAP.put("k", "King");
	}
	
	public static final Map<String, PieceFactory> PIECE_FACOTRY_MAP = new HashMap<String, PieceFactory>();
	static {
		PIECE_FACOTRY_MAP.put("r", new RookFactory());
		PIECE_FACOTRY_MAP.put("n", new KnightFactory());
		PIECE_FACOTRY_MAP.put("q", new QueenFactory());
		PIECE_FACOTRY_MAP.put("b", new BishopFactory());
		PIECE_FACOTRY_MAP.put("p", new PawnFactory());
		PIECE_FACOTRY_MAP.put("k", new KingFactory());
	}
	
	public static String get(String letter) {
		return PIECE_NAME_MAP.get(letter);
	}
	
	public static Piece getInstance(String letter, Color color) {
		PieceFactory p = PIECE_FACOTRY_MAP.get(letter);
		if(p == null) {
			System.out.println(letter);
		}
		return p.getInstance(color);
	}
	
}
