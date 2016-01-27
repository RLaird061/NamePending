package com.NamePending.piece;

import java.awt.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.MainSWT;

public class BlueMetalPiece extends StandardPiece
{
    public BlueMetalPiece()
    {
        super(new Color(42, 42, 255), 
				new Image(MainSWT.getDisplay(), SWTResourceManager.getImage(
						MainSWT.class, "/com/NamePending/res/PurpleBalloon.png")
						.getImageData()));
    }
}
