package com.NamePending;

import org.eclipse.nebula.animation.AnimationRunner;
import org.eclipse.nebula.animation.effects.AlphaEffect;
import org.eclipse.nebula.animation.movement.ExpoOut;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

import com.NamePending.unittest.UnitTests;

public class MainSWT {
	private static Display display;
	private static Shell shell;
	private static GameSWT gameFrame = null;
	
	public static void main(String[] args) {
		display = new Display();
		shell = new Shell(display, SWT.NO_TRIM);
		shell.setAlpha(0);
		shell.setSize(500, 500);

		Image img = new Image(display, SWTResourceManager.getImage(
				MainSWT.class, "/com/NamePending/res/LauncherScreen.png")
				.getImageData());
		shell.setBackgroundImage(img);

		AnimationRunner animationRunner = new AnimationRunner();
		// fade out                    2.9 secs
		AlphaEffect.fadeOnClose(shell, 2900, new ExpoOut(), animationRunner);

		// fade in                             160 alpha, 1.9 secs
		shell.open();
		AlphaEffect.setAlpha(animationRunner, shell, 160, 1900, new ExpoOut(), null, null);
		
		Button btnExit = new Button(shell, SWT.NONE);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				shell.close();
			}
		});
		btnExit.setBounds(398, 458, 92, 32);
		btnExit.setText("Exit");
		
		Button btnOpt = new Button(shell, SWT.NONE);
		btnOpt.setText("Options");
		btnOpt.setBounds(398, 420, 92, 32);
		
		Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// open GameSWT window and maybe close this window
				
				// TODO: move all this to a seperate process???
				gameFrame = new GameSWT(0, 0); // TODO: put in center? params useless?
				gameFrame.setLocation(shell.getBounds().x, shell.getBounds().y);
				gameFrame.setVisible(true);
			}
		});
		btnPlay.setText("Play");
		btnPlay.setBounds(398, 382, 92, 32);
		
		GameBoard gb = new GameBoard();
		UnitTests ut = new UnitTests(gb);
		ut.UnitTest1();
		
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) 
				display.sleep();
		}
		display.dispose();		
	}
}
