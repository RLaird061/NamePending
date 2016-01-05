package com.NamePending.piece;

import java.awt.*;
import java.util.ArrayList;

public abstract class Piece
{
    private Color centerColor;

    public static ArrayList<Piece> pieces = populatePieces();

    private static ArrayList<Piece> populatePieces()
    {
        ArrayList<Piece> pieces = new ArrayList<>();
        pieces.add(0, new NullPiece());
        pieces.addAll(StandardPiece.populatePieces());
        return pieces;
    }

    public Piece(Piece p)
    {
    	this.centerColor = p.centerColor;
    }
    
    public Piece(Color centerColor)
    {
        this.centerColor = centerColor;
    }

    public static byte getPieceIndex(Piece piece)
    {
        if (piece == null)
        {
            return 0;
        }

        return (byte) pieces.indexOf(piece);
    }

    public boolean isColorPiece(Color color)
    {
        return color.equals(centerColor);
    }

    public Color getCenterColor()
    {
        return centerColor;
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass().equals(this.getClass());
    }
}
