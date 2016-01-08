package com.NamePending;

import java.awt.Point;
import java.util.ArrayList;

import com.NamePending.piece.NullPiece;
import com.NamePending.piece.Piece;
import com.NamePending.piece.StandardPiece;

public class GameBoard {
	// Member Variable
	private ArrayList<Piece> board = new ArrayList<Piece>();
	private int boardWidth = 1;
	private int boardHeight = 1;
	
	// Member Functions
	public GameBoard(int w, int h) {
		boardWidth = w;
		boardHeight = h;
		for (int i = 0; i < w * h; i++)
			board.add(new NullPiece());
		
		// temp
		randomBoard();
	}
	
	public Piece get(int i) {
		return board.get(i);
	}
	
	public void set(int i, Piece p) {
		board.set(i, p);
	}
	
	// whatever the left piece is inside yellow selector; swap
	public void swap(int i)
	{
		// TODO
	}
	
	// Temp function to make random pieces! TODO: remove me
	public void randomBoard() {
		for (int i = 0; i < board.size(); i++) {
			int rnd = (int) (Math.random() * StandardPiece.pieces.size());
			
			board.set(i, StandardPiece.pieces.get(rnd));
		}
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
