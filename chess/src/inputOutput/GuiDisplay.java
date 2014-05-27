package inputOutput;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import commands.MoveCMD;
import model.Board;
import model.PieceMap;
import model.Pieces.Piece;
import chess.Location;

@SuppressWarnings("serial")
public class GuiDisplay extends Display{
	private static final String[] promotionOptions = {"Queen", "Knight", "Bishop", "Rook"};
	private BoardPanel boardPanel = new BoardPanel();

	@Override
	public void displayBoard(Board board, Location selectedLoc, List<Location> locsWithMoves) {
		boardPanel.update(board, selectedLoc, locsWithMoves);	
	}

	@Override
	public void notifyCheck() {
		JOptionPane.showMessageDialog(null, "Check!");
	}

	@Override
	public void notifyEndofGame(String message) {
		JOptionPane.showMessageDialog(null, message);
	}

	@Override
	public Piece getPawnPromotion(model.Color color) {
		int n = JOptionPane.showOptionDialog(null, "Select promotion",
				"Pawn Promotion", JOptionPane.DEFAULT_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, promotionOptions, 
				promotionOptions[0]);
		String pieceInitial = n == 1 ? "n" : promotionOptions[n].toLowerCase().substring(0, 1);
		
		return PieceMap.getInstance(pieceInitial, color);
	}

	public BoardPanel getBoardPanel() {
		return boardPanel;
	}
	
	public void notifyWithMove(MoveCMD move) {
		setChanged();
		notifyObservers(move);
	}
	
	private class BoardPanel extends JPanel {
		final private int PIXELS = 70;
		private BoardSquare[] panels = new BoardSquare[Board.SIZE * Board.SIZE];
		private Board board;
		private Piece selectedPiece;
		private BoardSquare selectedPanel;
		private Location selectedLoc;
		private List<Location> validLocations = new ArrayList<Location>(), locsWithMoves;
		private int mouseX, mouseY;
		
		private BoardPanel() {
			setLayout(new GridLayout(Board.SIZE, Board.SIZE));
			MouseListener listener = new MouseHandler();
			addMouseMotionListener(new MouseHandler());
			for(int row = 0; row < Board.SIZE; row++) {
				for(int col = 0; col < Board.SIZE; col++) {
					Location panelLocation = new Location(row, col);
					Color background = (row + col) % 2 == 1 ? Color.LIGHT_GRAY : Color.WHITE;
					BoardSquare panel = new BoardSquare(panelLocation, background);
					panel.addMouseListener(listener);
					panel.addMouseMotionListener((MouseMotionListener) listener);
					panel.setPreferredSize(new Dimension(PIXELS, PIXELS));
					panels[row * Board.SIZE + col] = panel;
					add(panel);
				}
			}
		}
		
		private void update(Board board, Location selectedLoc, List<Location> validMoves) {
			this.board = board;
			if(selectedLoc != null) {
				//this.selectedLoc = selectedLoc;
				selectedPiece = board.pieceAt(selectedLoc);
			}
			locsWithMoves = validMoves;
			for(BoardSquare panel : panels) {
				if(locsWithMoves.contains(panel.getLoc())){
					panel.setHasMoves(true);
				}
				else {
					panel.setHasMoves(false);
				}
			}
			repaint();
		}
		
		public void paint(Graphics canvas) {
			this.paintChildren(canvas);

			if(selectedPiece != null) {
				ImageIcon image  = selectedPiece.getImage();
				image.paintIcon(this, canvas, mouseX - image.getIconWidth() / 2, mouseY - image.getIconHeight() / 2);
			}

		}
		
		private class BoardSquare extends JPanel{
			private final Color BACKGROUND;
			private final Location LOCATION;
			private boolean hasMoves = false;
			private boolean validMove = false;
			private boolean display = true;
			
			private BoardSquare(Location location, Color background) {
				BACKGROUND = background;
				LOCATION = location;
				setBackground(background);
			}
			
			@Override
			public void paintComponent (Graphics canvas) {
				Color background = validMove ? Color.GRAY : ( hasMoves ? Color.CYAN: BACKGROUND);
				if(hasMoves || validMove) {
					background = (BACKGROUND == Color.LIGHT_GRAY) ? background.darker() : background;
				}
				setBackground(background);
				super.paintComponent(canvas);
				if(display){
					paint(canvas, board.pieceAt(LOCATION).getImage());
				}
			}
			
			private void paint(Graphics canvas, ImageIcon image) {
				int width = image.getIconWidth();
				int height = image.getIconHeight();
				int x = (getSize().width - width) / 2;
				int y = (getSize().height - height) / 2;
				image.paintIcon(this, canvas, x, y);
			}
			
			private void setValid(boolean valid) {
				this.validMove = valid;
			}
			
			private void setHasMoves(boolean hasMoves) {
				this.hasMoves = hasMoves;
			}
			
			private Location getLoc() {
				return LOCATION;
			}
			private void setDisplay(boolean display) {
				this.display = display;
			}
		}
		
		private class MouseHandler implements MouseListener, MouseMotionListener {
			BoardSquare lastClicked;

			@Override
			public void mouseClicked(MouseEvent event) {
				BoardSquare source = (BoardSquare) event.getSource();
				Location loc = source.getLoc();
				Piece piece = board.pieceAt(loc);
				if(selectedPiece == null) {
					if(piece != null && validLocations.size() > 0) {
						selectedLoc = loc;
						selectedPanel = source;
						selectedPanel.setDisplay(false);
						selectedPiece = piece;
					}
				}
				else {
					if(validLocations.contains(loc)) {
						MoveCMD move = new MoveCMD(selectedLoc, loc);
						notifyWithMove(move);
						selectedPiece = null;
						selectedPanel.setDisplay(true);
						mouseExited(event);
					}
				}
				repaint();
			}

			@Override
			public void mouseEntered(MouseEvent event) {
				if(selectedPiece == null) {
					BoardSquare source = (BoardSquare) event.getSource();
					Location loc = source.getLoc();
					Piece piece = board.pieceAt(loc);
					validLocations = piece.getValidMoves();
					for(BoardSquare panel : panels) {
						for(Location move : validLocations) {
							if(panel.getLoc().equals(move)) {
								panel.setValid(true);
							}
						}
					}
				}
				repaint();
			}

			@Override
			public void mouseExited(MouseEvent event) {
				if(selectedPiece == null) {
					validLocations = new ArrayList<Location>();
					for(BoardSquare panel : panels) {
						panel.setValid(false);
					}
				}
				repaint();
			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				lastClicked = (BoardSquare) event.getSource();
			}

			@Override
			public void mouseReleased(MouseEvent event) {
				BoardSquare source = (BoardSquare) event.getSource();
				if(source == lastClicked) {
					mouseClicked(event);
				}
				lastClicked = null;
			}
			
			@Override
			public void mouseDragged(MouseEvent event) {
				
			}

			@Override
			public void mouseMoved(MouseEvent event) {
				if(event.getSource() instanceof BoardSquare) {
					BoardSquare source = (BoardSquare) event.getSource();
					mouseX = event.getX() + source.getLoc().getCol() * source.getWidth();
					mouseY = event.getY() + source.getLoc().getRow() * source.getHeight();
					if(selectedPiece != null) {
						repaint();
					}
				}
			}
			
		}
	}
	
}
