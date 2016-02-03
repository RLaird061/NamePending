package com.NamePending;

import java.awt.Point;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Rectangle;

import com.NamePending.piece.Piece;
import com.NamePending.piece.StandardPiece;

public class GameBoard {
	// Constants
	private static final int MARK_NONE = 0;
	private static final int MARK_TOREMOVE = 1;
	private static final int MARK_GETNEWPIECEFROMBELOW = 2;
	private static final int MARK_GETNEWPIECEIMAGE = 3;
	
	private static final int MULTIPLER_NONE = 0;
	private static final int MULTIPLER_3 = 1;
	private static final int MULTIPLER_3x3 = 2;
	private static final int MULTIPLER_3x3x3 = 3;
	private static final int MULTIPLER_3x3x3x3 = 4;
	
	// Member Variable
	private ArrayList<Piece> board = new ArrayList<Piece>();
	private ArrayList<Integer> marker = new ArrayList<Integer>();
	private int boardWidth = 1;
	private int boardHeight = 1;
	private GameComposite gameComposite = null;
	
	private int multipler = MULTIPLER_NONE;
	
	// Member Functions
	public GameBoard(int w, int h) {
		boardWidth = w;
		boardHeight = h;
		for (int i = 0; i < w * h; i++) {
			board.add(Piece.pieces.get(0) /* NullPiece */);
			marker.add(new Integer(MARK_NONE));
		}
		// temp
		randomBoard();
	}
	
	public int getBoardSize() { return board.size(); }
	
	public void setComposite(GameComposite comp)
	{
		gameComposite = comp;
		for (int i = 0; i < board.size(); i++)
		{
			Piece p = get(i);
			TransitionableCanvas t = (TransitionableCanvas) gameComposite.lbls.get(i);
			t.setPieceImage(p.getImage());
		}		
	}
	
	public Piece get(int i) {
		return board.get(i);
	}
	
	public void set(int i, Piece p) {
		board.set(i, p);
	}
	
	// i (index) is whatever the left piece is inside yellow selector; swapping like
	// this makes it so we don't have to do any bounds checking
	public void swap(int swap_point)
	{
		MainSWT.getGameFrame().gameOverDirty = true; // don't touch timer
		
		Piece tmp = get(swap_point);
		set(swap_point, get(swap_point+1));
		set(swap_point+1, tmp);
		
		Piece p1 = get(swap_point);
		TransitionableCanvas t1 = (TransitionableCanvas) gameComposite.lbls.get(swap_point);
		Piece p2 = get(swap_point+1);
		TransitionableCanvas t2 = (TransitionableCanvas) gameComposite.lbls.get(swap_point+1);

		t1.doSwapAnim();
		t1.setPieceImage(p1.getImage());
		t2.setPieceImage(p2.getImage());
		
		// track "chained" moves somehow
		int score = 0;
		int acc_score = 0;
		boolean first_solve = true;
		do {
			score = solver(swap_point, first_solve, false);
			
			if (first_solve) {
				String[] outstr = {"NONE", "3", "3x3", "3x3x3", "3x3x3x3"};
				System.out.printf("Multiplier: %s\n", outstr[multipler]);
				
				if (multipler > MULTIPLER_3) {
					if (multipler == MULTIPLER_3x3)
						score += 500;
					else if (multipler == MULTIPLER_3x3x3)
						score += 2000;
					else if (multipler == MULTIPLER_3x3x3x3)
						score += 10000;
				}
				first_solve = false;
			}
			
			acc_score += score;
		} while(score > 0);
		
		if (acc_score > 0) {
			MainSWT.getGameFrame().addToScore(acc_score);
			MainSWT.getGameFrame().gameOverSelection = 100;
		}
		MainSWT.getGameFrame().gameOverDirty = false;
	}
	
	// solver is the meat of the game; any 3 or more pieces in a row will score/disappear
	// hard part is making sure we get the "correct" score for a swap
	// returns: score
	//     if no pieces are scored/removed return 0
	public int solver(int swap_point, boolean first_solve, boolean no_anim)
	{
		// detect swap multiplier (ie: 3x3x5 stuff)
		if (first_solve)
			multipler = solverFindMultiplier(swap_point, false);
		
		int ret = solverMarkToRemove();
		solverRemovePieces(no_anim);
		solverMovePiecesFromBelow(no_anim);
		if (!no_anim)
			solverDoSlideUp();
		else {
			for (int i = 0; i < board.size(); i++)
				marker.set(i, MARK_NONE);
		}
		return ret;
	}
	
	private int solverFindMultiplier(int swap_point, boolean isRight)
	{
		int mult = MULTIPLER_NONE;
		int x, y;
		int tmpx, tmpy;
		int horz, vert; // number of matching in a direction
		boolean goesHorz = false;
		boolean goesVert = false;

		x = swap_point % GameSWT.PIECES_PER_ROW;
		y = swap_point / GameSWT.PIECES_PER_ROW;
		tmpx = x;
		tmpy = y;
		Piece p1 = get(tmpy * GameSWT.PIECES_PER_ROW + tmpx);
		Piece p2 = null;
		horz = 0; vert = 0;
		// go UP
		while ((tmpy > 0) && (p2 == null || p1.getCenterColor() == p2.getCenterColor())) {
			p2 = get(--tmpy * GameSWT.PIECES_PER_ROW + tmpx);
			if (p1.getCenterColor() != p2.getCenterColor())
				tmpy++;			
		}
		vert = y - tmpy;
		// then go DOWN
		p2 = null;
		tmpx = swap_point % GameSWT.PIECES_PER_ROW;
		tmpy = swap_point / GameSWT.PIECES_PER_ROW;
		while ((tmpy < GameSWT.PIECES_PER_COL-1) && (p2 == null || p1.getCenterColor() == p2.getCenterColor())) {
			p2 = get(++tmpy * GameSWT.PIECES_PER_ROW + tmpx);
			if (p1.getCenterColor() != p2.getCenterColor())
				tmpy--;
		}
		vert += tmpy - y;
		if (vert >= 2)
			goesVert = true;

		// go LEFT
		p2 = null;
		tmpx = swap_point % GameSWT.PIECES_PER_ROW;
		tmpy = swap_point / GameSWT.PIECES_PER_ROW;
		while ((tmpx > 0) && (p2 == null || p1.getCenterColor() == p2.getCenterColor())) {
			p2 = get(tmpy * GameSWT.PIECES_PER_ROW + (--tmpx));
			if (p1.getCenterColor() != p2.getCenterColor())
				tmpx++;
		}
		horz = x - tmpx;
		// then go RIGHT
		p2 = null;
		tmpx = swap_point % GameSWT.PIECES_PER_ROW;
		tmpy = swap_point / GameSWT.PIECES_PER_ROW;
		while ((tmpx < GameSWT.PIECES_PER_ROW-1) && (p2 == null || p1.getCenterColor() == p2.getCenterColor())) {
			p2 = get(tmpy * GameSWT.PIECES_PER_ROW + (++tmpx));
			if (p1.getCenterColor() != p2.getCenterColor())
				tmpx--;
		}
		horz += tmpx - x;
		if (horz >= 2)
			goesHorz = true;

		int right_side = 0;
		if (!isRight)
			right_side = solverFindMultiplier(swap_point+1, true);
		
		if (right_side == MULTIPLER_3x3 && (goesVert && goesHorz))
			mult = MULTIPLER_3x3x3x3;
		else if (right_side == MULTIPLER_3x3 && (goesVert || goesHorz))
			mult = MULTIPLER_3x3x3;
		else if (right_side == MULTIPLER_3 && (goesVert && goesHorz))
			mult = MULTIPLER_3x3x3;
		else if (right_side == MULTIPLER_3 && (goesVert || goesHorz))
			mult = MULTIPLER_3x3;
		else if (goesVert && goesHorz)
			mult = MULTIPLER_3x3;
		else if (right_side == MULTIPLER_3x3)
			mult = MULTIPLER_3x3;
		else if (goesVert || goesHorz)
			mult = MULTIPLER_3;
		else if (right_side == MULTIPLER_3)
			mult = MULTIPLER_3;
		
		System.out.printf("%b %b| %d %d\n", goesHorz, goesVert, horz, vert);
		
		return mult;
	}
	
	private void solverDoSlideUp()
	{
		TransitionableCanvas t0 = (TransitionableCanvas) gameComposite.lbls.get(0);
		ArrayList<Rectangle> rects = new ArrayList<Rectangle>();
		ArrayList<Integer> heights = new ArrayList<Integer>();
		int x, y, tmpy;
		for (x = 0; x < GameSWT.PIECES_PER_ROW; x++) {
			tmpy = -1;
			int h = 0;
			for (y = 0; y < GameSWT.PIECES_PER_COL; y++) {
				int idx = y * GameSWT.PIECES_PER_ROW + x;
				int mark = marker.get(idx);
				if (tmpy == -1 && mark == MARK_TOREMOVE) {
					tmpy = y;
					h++;
				} else if (mark == MARK_TOREMOVE) {
					h++;
				}
			}
			if (tmpy != -1 && tmpy < GameSWT.PIECES_PER_COL) {
				// found a column to move up.. so make a rect
				Rectangle r = new Rectangle(
						x * GameSWT.PIECE_LENGTH,
						tmpy * GameSWT.PIECE_LENGTH,
						GameSWT.PIECE_LENGTH,
						(GameSWT.PIECES_PER_COL-tmpy) * GameSWT.PIECE_LENGTH);
				rects.add(r);
				heights.add(h);
			}
		}
		
		t0.doSlideUpAnimStep1(rects, heights);
		for (int i = 0; i < board.size(); i++) {
			marker.set(i, MARK_NONE);
			((TransitionableCanvas) gameComposite.lbls.get(i)).setPieceImage(get(i).getImage());		
		}
		t0.doSlideUpAnimStep2();		
	}
	
	private void solverMovePiecesFromBelow(boolean no_anim)
	{
		for (int x = 0; x < GameSWT.PIECES_PER_ROW; x++)
		{
			for (int y = 0; y < GameSWT.PIECES_PER_COL; y++)
			{   // go col by col
				int i = y * GameSWT.PIECES_PER_ROW + x;

				int tmpx = x;
				int tmpy = y;
				boolean found = false;
				int mark = marker.get(i);
				if (mark == MARK_GETNEWPIECEFROMBELOW || mark == MARK_TOREMOVE) {
					// go down looking for a piece below if found move it up here
					for (; tmpy < GameSWT.PIECES_PER_COL && !found; tmpy++) {
						int tmpi = tmpy * GameSWT.PIECES_PER_ROW + tmpx;
						if (marker.get(tmpi) != MARK_GETNEWPIECEFROMBELOW && marker.get(tmpi) != MARK_TOREMOVE) {
							set(i, get(tmpi)); // move piece up
							set(tmpi, Piece.pieces.get(0) /* NullPiece */);
							
							marker.set(tmpi, MARK_GETNEWPIECEFROMBELOW);
							found = true;
						}
					}
					if (!found) {
						// if not found make a new random piece
						int rnd = (int) (Math.random() * StandardPiece.pieces.size());
						board.set(i, StandardPiece.pieces.get(rnd));						
						
						if (marker.get(i) != MARK_TOREMOVE)
							marker.set(i, MARK_GETNEWPIECEIMAGE);
					}
				}
			}
		}
	}
	
	private void solverRemovePieces(boolean no_anim)
	{
		TransitionableCanvas t = null;
		for (int i = 0; i < board.size(); i++)
		{
			if (!no_anim)
				t = (TransitionableCanvas) gameComposite.lbls.get(i);
			
			if (marker.get(i) == MARK_TOREMOVE) {
				set(i, Piece.pieces.get(0) /* NullPiece */);
				if (!no_anim)
					t.setPieceImage(get(i).getImage());
			}
		}
	}
	
	private int solverMarkToRemove()
	{
		int ret = 0;
		int tmpx;
		int tmpy;
		int idx;
		for (int y = 0; y < boardHeight; y++)
		{
			for (int x = 0; x < boardWidth; x++)
			{
				tmpx = x; tmpy = y;
				Piece p1 = get(tmpy * GameSWT.PIECES_PER_ROW + tmpx);
				if (tmpy > 1) { // going UP
					Piece p2 = get(--tmpy * GameSWT.PIECES_PER_ROW + tmpx);
					Piece p3 = get(--tmpy * GameSWT.PIECES_PER_ROW + tmpx);
					if (p1.getCenterColor() == p2.getCenterColor() &&  // atleast 3 in a row 
							p2.getCenterColor() == p3.getCenterColor())
					{
						while(tmpy>0 && p1.getCenterColor() == p2.getCenterColor()) {
							p2 = get(--tmpy * GameSWT.PIECES_PER_ROW + tmpx);
							if (p1.getCenterColor() != p2.getCenterColor())
								tmpy++;
						}
						while(tmpy <= y) {
							idx = tmpy++ * GameSWT.PIECES_PER_ROW + tmpx;
							if (marker.get(idx) == MARK_NONE) {
								marker.set(idx, MARK_TOREMOVE);
								ret++; // TODO: this is pieces removed, use for scoring?
							}
						}
					}
				}
				tmpx = x; tmpy = y;
				if (tmpx > 1) { // going LEFT
					Piece p2 = get(tmpy * GameSWT.PIECES_PER_ROW + (--tmpx));
					Piece p3 = get(tmpy * GameSWT.PIECES_PER_ROW + (--tmpx));
					if (p1.getCenterColor() == p2.getCenterColor() &&  // atleast 3 in a row 
							p2.getCenterColor() == p3.getCenterColor())
					{
						while(tmpx>0 && p1.getCenterColor() == p2.getCenterColor()) {
							p2 = get(tmpy * GameSWT.PIECES_PER_ROW + (--tmpx));
							if (p1.getCenterColor() != p2.getCenterColor())
								tmpx++;
						}
						while(tmpx <= x) {
							idx = tmpy * GameSWT.PIECES_PER_ROW + tmpx++;
							if (marker.get(idx) == MARK_NONE) {
								marker.set(idx, MARK_TOREMOVE);
								ret++;
							}
						}
					}
				}
				tmpx = x; tmpy = y;
				if (tmpy < GameSWT.PIECES_PER_COL-2) { // going DOWN
					Piece p2 = get(++tmpy * GameSWT.PIECES_PER_ROW + tmpx);
					Piece p3 = get(++tmpy * GameSWT.PIECES_PER_ROW + tmpx);
					if (p1.getCenterColor() == p2.getCenterColor() &&  // atleast 3 in a row 
							p2.getCenterColor() == p3.getCenterColor())
					{
						while(tmpy<GameSWT.PIECES_PER_ROW-1 && p1.getCenterColor() == p2.getCenterColor()) {
							p2 = get(++tmpy * GameSWT.PIECES_PER_ROW + tmpx);
							if (p1.getCenterColor() != p2.getCenterColor())
								tmpy--;
						}
						while(tmpy >= y) {
							idx = tmpy-- * GameSWT.PIECES_PER_ROW + tmpx;
							if (marker.get(idx) == MARK_NONE) {
								marker.set(idx, MARK_TOREMOVE);
								ret++;
							}
						}
					}
				}
				tmpx = x; tmpy = y;
				if (tmpx < GameSWT.PIECES_PER_ROW-2) { // going RIGHT
					Piece p2 = get(tmpy * GameSWT.PIECES_PER_ROW + (++tmpx));
					Piece p3 = get(tmpy * GameSWT.PIECES_PER_ROW + (++tmpx));
					if (p1.getCenterColor() == p2.getCenterColor() &&  // atleast 3 in a row 
							p2.getCenterColor() == p3.getCenterColor())
					{
						while(tmpx<GameSWT.PIECES_PER_ROW-1 && p1.getCenterColor() == p2.getCenterColor()) {
							p2 = get(tmpy * GameSWT.PIECES_PER_ROW + (++tmpx));
							if (p1.getCenterColor() != p2.getCenterColor())
								tmpx--;
						}
						while(tmpx >= x) {
							idx = tmpy * GameSWT.PIECES_PER_ROW + tmpx--;
							if (marker.get(idx) == MARK_NONE) {
								marker.set(idx, MARK_TOREMOVE);
								ret++;
							}
						}
					}
				}
			}
		}
		return ret;		
	}
	
	// Temp function to make random pieces! TODO: remove me
	public void randomBoard() {
		for (int i = 0; i < board.size(); i++) {
			int rnd = (int) (Math.random() * StandardPiece.pieces.size());
			
			board.set(i, StandardPiece.pieces.get(rnd));
		}
		
		int score = 0;
		do {
			score = solver(0, false, true);
		} while(score > 0);		
	}
	
	// XYtoIDX: input the array coords x and y
	// return: and index into board array
	public int XYtoIDX(int x, int y) {
		return y * boardWidth + x;
	}
	
	public Point IDXtoXY(int idx) {
		Point p = new Point();
		p.x = idx % boardWidth;
		p.y = idx / boardWidth;
		return p;
	}
	
	public void PrintBoard() {
		for (int y = 0; y < boardHeight; y++) {
			for (int x = 0; x < boardWidth; x++) {
				// for every game cell
				System.out.printf("%d ", board.get(XYtoIDX(x,y)).getCenterColor());
			}
			System.out.printf("\n");
		}
	}
}
