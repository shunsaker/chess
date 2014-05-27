package inputOutput;

import java.util.List;
import java.util.Scanner;

import model.Color;
import model.PieceMap;
import model.Pieces.Piece;
import chess.Location;
import chess.LocationTools;

public class ConsoleInput {
	private static final Scanner SCAN = new Scanner(System.in);

	public static Location promptForLocation(String message) {
		String input = null;
		do {
			if(input != null) {
				System.err.println("Invalid Input!");
				if(input.equals("debug")) {
					System.out.println("entering debug");
				}
			}
			System.out.println(message);
			input = SCAN.nextLine();
		}
		while(!(input.length() == 2 && LocationTools.isLocationOnBoard(new Location(input))));
		return new Location(input);
	}
	
	public static Location getLocation(List<Location> validLocations, String prompt, String error) {
		Location loc = null;
		do {
			if(loc != null) {
				System.err.println(error);
			}
			loc = promptForLocation(prompt);
		}
		while(!validLocations.contains(loc));
		return loc;
	}
	
	public static Piece getPawnPromotion(Color color) {
		Piece piece = null;
		do {
			System.out.println("Pawn Promotion! What piece do you want? [Q, B, N, R]");
			String input = SCAN.nextLine().toLowerCase();
			if(input.equalsIgnoreCase("q") ||
					input.equalsIgnoreCase("b") ||
					input.equalsIgnoreCase("n") ||
					input.equalsIgnoreCase("r")) {
				piece = PieceMap.getInstance(input, color);
			}
		}
		while(piece == null);
		return piece;
	}
	
}
