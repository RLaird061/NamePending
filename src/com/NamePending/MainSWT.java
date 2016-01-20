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
	public static GameSWT gameFrame = null;
	private static Display display = null;
	private static Shell shell = null;
	private static GameOptions gameOpt = null;
	
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
		AlphaEffect.setAlpha(animationRunner, shell, 255, 1900, new ExpoOut(), null, null);
		
		Button btnExit = new Button(shell, SWT.NONE);
		btnExit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (gameFrame != null)
					gameFrame.dispose();
				shell.close();
			}
		});
		btnExit.setBounds(398, 458, 92, 32);
		btnExit.setText("Exit");
		
		Button btnOpt = new Button(shell, SWT.NONE);	
		btnOpt.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// opens GameOptions window
				
				// TODO: make good-looking options window
				gameOpt = new GameOptions();
				gameOpt.setVisible(true);
			}
		});	
		btnOpt.setText("Options");
		btnOpt.setBounds(398, 420, 92, 32);
		
		Button btnPlay = new Button(shell, SWT.NONE);
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				// open GameSWT window and maybe close this window
				
				// TODO: move all this to a seperate process???
				Shell gameShell = new Shell(display, SWT.NONE);
				gameFrame = new GameSWT(gameShell, SWT.NONE); // TODO: put in center? params useless?
				gameShell.setLocation(shell.getBounds().x, shell.getBounds().y);
				gameShell.pack();
				gameShell.open();
			}
		});
		btnPlay.setText("Play");
		btnPlay.setBounds(398, 382, 92, 32);
		
		//UnitTests ut = new UnitTests(gb);
		//ut.UnitTest1();
		
		while (!shell.isDisposed()) {
				try {
					if (!display.readAndDispatch()) 
						display.sleep();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
		}
		display.dispose();		
	}

	public static Display getDisplay() {
		return display;
	}

	public static void setDisplay(Display display) {
		MainSWT.display = display;
	}

	public static Shell getShell() {
		return shell;
	}

	public static void setShell(Shell shell) {
		MainSWT.shell = shell;
	}

	public static GameSWT getGameFrame() {
		return gameFrame;
	}

	public static void setGameFrame(GameSWT gameFrame) {
		MainSWT.gameFrame = gameFrame;
	}
}
