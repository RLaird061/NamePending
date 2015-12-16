package com.NamePending;

import java.awt.Point;
import java.util.ArrayList;

public class GameBoard {
	// Member Variable
	private ArrayList<Piece> board = new ArrayList<Piece>();
	private final int WIDTH  = 3;
	private final int HEIGHT = 3;
	
	// Member Functions
	public GameBoard() {
		for (int i = 0; i < WIDTH * HEIGHT; i++)
			board.add(new Piece(0));
	}
	
	public int XYtoIDX(int x, int y) {
		return y * WIDTH + x;
	}
	
	public Point IDXtoXY(int idx) {
		Point p = new Point();
		p.x = idx % WIDTH;
		p.y = idx / WIDTH;
		return p;
	}
	
	public void PrintBoard() {
		for (int y = 0; y < HEIGHT; y++) {
			for (int x = 0; x < WIDTH; x++) {
				// for every game cell
				System.out.printf("%d ", board.get(XYtoIDX(x,y)).getPiece_color());
			}
			System.out.printf("\n");
		}
	}
	
	public void UnitTest1() {
		int x = 0;
		int y = 0;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		x = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		x = 2;
		board.get(XYtoIDX(x,y)).setPiece_color(0);

		x = 0;
		y = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		x = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		x = 2;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		
		x = 0;
		y = 2;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		x = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		x = 2;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		
		//PrintBoard();
		// TODO: test board solver
		x = 0;
		y = 0;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		x = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		x = 2;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		
		x = 0;
		y = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		x = 1;
		board.get(XYtoIDX(x,y)).setPiece_color(0);
		x = 2;
		board.get(XYtoIDX(x,y)).setPiece_color(1);
		// END board solver
		//PrintBoard();
		
		// confirm top 2 lines are 010, 101
		x = 0;
		y = 0;
		if (board.get(XYtoIDX(x,y)).getPiece_color() != 0)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 1;
		if (board.get(XYtoIDX(x,y)).getPiece_color() != 1)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 2;
		if (board.get(XYtoIDX(x,y)).getPiece_color() != 0)
			System.out.printf("UnitTest1 FAIL!\n");

		x = 0;
		y = 1;
		if (board.get(XYtoIDX(x,y)).getPiece_color() != 1)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 1;
		if (board.get(XYtoIDX(x,y)).getPiece_color() != 0)
			System.out.printf("UnitTest1 FAIL!\n");
		x = 2;
		if (board.get(XYtoIDX(x,y)).getPiece_color() != 1)
			System.out.printf("UnitTest1 FAIL!\n");
	}
}
