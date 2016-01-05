package com.NamePending;

import java.awt.Point;
import java.util.ArrayList;

import com.NamePending.piece.NullPiece;
import com.NamePending.piece.Piece;

public class GameBoard {
	// Member Variable
	private ArrayList<Piece> board = new ArrayList<Piece>();
	private final static int WIDTH  = 3;
	private final static int HEIGHT = 3;
	
	// Member Functions
	public GameBoard() {
		for (int i = 0; i < WIDTH * HEIGHT; i++)
			board.add(new NullPiece());
	}
	
	public Piece get(int i) {
		return board.get(i);
	}
	
	public void set(int i, Piece p) {
		board.set(i, p);
	}
	
	// XYtoIDX: input the array coords x and y
	// return: and index into board array
	public static int XYtoIDX(int x, int y) {
		return y * WIDTH + x;
	}
	
	public static Point IDXtoXY(int idx) {
		Point p = new Point();
		p.x = idx % WIDTH;
		p.y = idx / WIDTH;
		return p;
	}
	
	public void PrintBoard() {
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				// for every game cell
				System.out.printf("%d ", board.get(XYtoIDX(x,y)).getCenterColor());
			}
			System.out.printf("\n");
		}
	}
}
