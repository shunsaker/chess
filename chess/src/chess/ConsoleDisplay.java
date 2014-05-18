package chess;

import model.Board;
import model.Pieces.Piece;

public class ConsoleDisplay implements Display{

	@Override
	public void displayBoard(Board board) {
		int i = 0;
		System.out.println("\n   A B C D E F G H\n   _______________");
		for(Piece p : board) {
			int mod = i % Board.SIZE;
			if(mod == 0) {
				if(i != 0) {
					System.out.println();
				}
				System.out.print(Board.SIZE-(i / Board.SIZE) + "| ");
			}
			System.out.print(p.toChar() + " ");
			i++;
		}
	}
}
