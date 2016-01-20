package com.NamePending.piece;

import java.awt.Color;
import java.util.ArrayList;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

public abstract class StandardPiece extends Piece
{
    public static ArrayList<StandardPiece> pieces = populatePieces();

    public StandardPiece(Color centerColor, Image image)
    {
        super(centerColor, image);
    }

    protected static ArrayList<StandardPiece> populatePieces()
    {
        ArrayList<StandardPiece> pieces = new ArrayList<>();
        // TODO: add all standard pieces here
        pieces.add(new RedBrickPiece());
        pieces.add(new RedMetalPiece());
        pieces.add(new GreenBrickPiece());
        pieces.add(new GreenMetalPiece());
        pieces.add(new BlueBrickPiece());
        pieces.add(new BlueMetalPiece());
        return pieces;
    }
}
