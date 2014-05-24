package inputOutput;

import java.util.List;
import java.util.Scanner;

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
			input = SCAN.next();
		}
		while(input.length() != 2 && LocationTools.isLocationOnBoard(new Location(input)));
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
	
	
}
