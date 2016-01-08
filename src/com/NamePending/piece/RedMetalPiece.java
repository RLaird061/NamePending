package com.NamePending.piece;

import java.awt.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.MainSWT;

public class RedMetalPiece extends StandardPiece
{
    public RedMetalPiece()
    {
        super(new Color(255, 42, 42), 
				new Image(MainSWT.getDisplay(), SWTResourceManager.getImage(
						MainSWT.class, "/com/NamePending/res/RedMetalPiece.png")
						.getImageData()));
    }
}
