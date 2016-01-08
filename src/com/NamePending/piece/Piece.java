package com.NamePending.piece;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;

import com.NamePending.Utils;

public abstract class Piece
{
    private Color centerColor;
    private Image image;

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
    	this.image = p.image;
    }
    
    public Piece(Color centerColor, Image image)
    {
        this.centerColor = centerColor;
        this.image = image;
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
    
    public BufferedImage getImage()
    {
    	return Utils.convertToAWT(image.getImageData());
    }

    @Override
    public boolean equals(Object o)
    {
        return o.getClass().equals(this.getClass());
    }
}
