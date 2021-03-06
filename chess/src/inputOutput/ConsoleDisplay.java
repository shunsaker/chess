package inputOutput;

import java.util.List;

import chess.Location;
import model.Board;
import model.Pieces.Piece;

public class ConsoleDisplay extends Display{

	@Override
	public void displayBoard(Board board, Location selectedPiece, List<Location> validMoves) {
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
		System.out.println("\n");
	}
	@Override
	public void notifyCheck() {
		System.out.println("Check!");
		
	}

	@Override
	public void notifyEndofGame(String message) {
		System.out.println(message);
		
	}

	@Override
	public Piece getPawnPromotion(model.Color color) {
		return ConsoleInput.getPawnPromotion(color);
	}
}
