package chess;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Setup {
	// board must be 8 x 8 for this regex to work
	private static final String pattern = "(?i)([bkqnrp][ld][a-h][1-8])|([a-h][1-8] [a-h][1-8]\\*?( [a-h][1-8] [a-h][1-8])?)";
	public static final Scanner SCAN = new Scanner(System.in);
	
	public static void parseLine(String line) {
		if(line != null && line.matches(pattern)) {
			if(line.length() == 4) {	
				System.out.println("Place " + line.charAt(0) + " at " + line.substring(2, 4));
			}
			else {
				System.out.print("Move whatever is at " + line.substring(0, 2) + " to " + line.substring(3,5));
				System.out.print((line.length() == 6) ? " and capture the piece there" : "");
				System.out.println((line.length() == 11) ? " and move whatever is at " + line.substring(6, 8) + " to " + line.substring(9, 11) : "");
			}
		}
		else 
			System.out.println("invalid: " + line);
		
	}
	
	public static void driver(String fileAddress) {
		try(BufferedReader br = new BufferedReader(new FileReader(fileAddress))) {
			String line =  null; 
			do {
				line = br.readLine();
				parseLine(line);
			}
			while (line != null);
		}
		catch(IOException e) {
			System.out.println("Invlid file, exiting program.");
			e.printStackTrace();
		}
	}
	
	
}
