package com.NamePending.piece;

import java.awt.*;
import java.util.ArrayList;

public abstract class StandardPiece extends Piece
{
    public static ArrayList<StandardPiece> pieces = populatePieces();

    public StandardPiece(Color centerColor)
    {
        super(centerColor);
    }

    protected static ArrayList<StandardPiece> populatePieces()
    {
        ArrayList<StandardPiece> pieces = new ArrayList<>();
        // TODO: add all standard pieces here
        pieces.add(new RedBrickPiece());
        return pieces;
    }
}
