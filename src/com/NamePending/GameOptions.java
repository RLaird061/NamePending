package com.NamePending;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;

public class GameOptions extends JFrame {
	public GameOptions() {
		
		int optFullScreen = -1;
		
		GameDatabase db = new GameDatabase();
		try {
			optFullScreen = db.readOptions("FullScreen", 0);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		this.setSize(350, 305);
		this.setResizable(false);
		getContentPane().setLayout(null);

		// Game Window size stuff
		JLabel lblGameWindow = new JLabel("Game Window:");
		lblGameWindow.setBounds(22, 11, 72, 23);
		getContentPane().add(lblGameWindow);

		JRadioButton rdbtnFullscreen = new JRadioButton("Fullscreen");
		rdbtnFullscreen.setBounds(104, 11, 78, 23);
		getContentPane().add(rdbtnFullscreen);

		JRadioButton rdbtnWindowed = new JRadioButton("Windowed");
		rdbtnWindowed.setBounds(203, 11, 83, 23);
		getContentPane().add(rdbtnWindowed);

		// Don't forget to group the radio buttons!
		ButtonGroup winSize = new ButtonGroup();
		winSize.add(rdbtnFullscreen);
		winSize.add(rdbtnWindowed);
		
		if (optFullScreen == 0)
			rdbtnWindowed.setSelected(true);
		else if (optFullScreen == 1)
			rdbtnFullscreen.setSelected(true);

		// TODO: get some music all up in here
		JLabel lblSounds = new JLabel("Sounds:");
		lblSounds.setBounds(22, 54, 46, 14);
		getContentPane().add(lblSounds);

		JLabel lblBackgroundMusic = new JLabel("Background Music:");
		lblBackgroundMusic.setBounds(63, 79, 200, 14);
		getContentPane().add(lblBackgroundMusic);

		JSlider BGM = new JSlider();
		BGM.setBounds(63, 104, 200, 26);
		getContentPane().add(BGM);

		JLabel lblSoundEffects = new JLabel("Sound Effects:");
		lblSoundEffects.setBounds(63, 152, 200, 14);
		getContentPane().add(lblSoundEffects);

		JSlider SFX = new JSlider();
		SFX.setBounds(63, 177, 200, 26);
		getContentPane().add(SFX);		

		//CONFIRMATION A GO-GO		
		JButton btnOk = new JButton("OK");
		btnOk.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
//				if ( rdbtnFullscreen.isSelected())
//					// only works AFTER game is open
//					// TODO: make options work BEFORE game is open
//					MainSWT.gameFrame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
//				MainSWT.gameFrame.setVisible(true);
//				MainSWT.gameFrame.setUndecorated(true);

				GameDatabase db = new GameDatabase();

				try {
					if (rdbtnWindowed.isSelected())							
						db.writeOptions("FullScreen", 0);
					else 
						db.writeOptions("FullScreen", 1);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				dispose();
			}
		});
		btnOk.setBounds(45, 224, 89, 23);
		getContentPane().add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				dispose();
			}
		});
		btnCancel.setBounds(184, 224, 89, 23);
		getContentPane().add(btnCancel);
	}
}