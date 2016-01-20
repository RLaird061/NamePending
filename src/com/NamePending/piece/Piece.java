package com.NamePending.piece;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public abstract class Piece
{
    private Color centerColor;
    private Image image;
    private Composite composite = null;

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
    
    public Image getImage()
    {
    	return image;
    }

    public void setComposite(Composite composite) {
		this.composite = composite;
	}
    public Composite getComposite() {
    	return composite;
    }
}
