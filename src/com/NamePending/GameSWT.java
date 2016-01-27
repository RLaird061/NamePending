package com.NamePending;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

public class GameSWT extends Composite
{
	// Constants
	public final static int OUTLINE_WIDTH = 2;
	public final static int BORDER_WIDTH = 7;
	public final static int PIECES_PER_ROW = 6;
	public final static int PIECES_PER_COL = 12;
	public final static int PIECE_LENGTH = 45;
	public final static int SELECTOR_WIDTH = 90;
	public final static int SELECTOR_HEIGHT = 45;
	
	// Member Variables
	private GameBoard gameboard;    // holds game logic
	private GameComposite gamecomp; // game drawing
	private Label lblScore;
	private Text txtName;

	// Member Functions
	public GameSWT(Composite parent, int style) {
		super(parent, style);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
			}
			public void keyPressed(KeyEvent e) {
				// Handle ARROW keys and SWAP(space)
				//System.out.println(e.toString());
				Point p = gamecomp.getSelector();
				int selX = p.x;
				int selY = p.y;
				if (e.keyCode == 0x1000001) { // Up
					selY = (selY > 0) ? selY-1 : selY;
				} else if (e.keyCode == 0x1000004) { // Down
					selX = (selX < PIECES_PER_ROW-2) ? selX+1 : selX;
				} else if (e.keyCode == 0x1000002) { // Right
					selY = (selY < PIECES_PER_COL-1) ? selY+1 : selY;
				} else if (e.keyCode == 0x1000003) { // Left
					selX = (selX > 0) ? selX-1 : selX;					
				} else if (e.keyCode == 0x20) { // Space
					gameboard.swap(selY * PIECES_PER_ROW + selX);
				}
				gamecomp.setSelector(new Point(selX, selY));
				redraw(0, 0, parent.getBounds().width, parent.getBounds().height, true);
			}
		});

		setSize(BORDER_WIDTH*2 + PIECES_PER_ROW*PIECE_LENGTH + 300, 
				BORDER_WIDTH*2 + PIECES_PER_COL*PIECE_LENGTH);
		
		gamecomp = new GameComposite(this, SWT.DOUBLE_BUFFERED);
		gamecomp.setSelector(new Point(0,0)); // start selector at top left
		gameboard = new GameBoard(PIECES_PER_ROW, PIECES_PER_COL);
		gameboard.setComposite(gamecomp);
		
		txtName = new Text(gamecomp, SWT.BORDER);
		txtName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				if (txtName.getText().length() > 3)
					txtName.setText(txtName.getText().substring(0, 3));
			}
		});
		txtName.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		txtName.setBounds(380, 10, 125, 30);
		
		lblScore = new Label(gamecomp, SWT.CENTER);
		lblScore.setFont(SWTResourceManager.getFont("Arial", 16, SWT.BOLD));
		lblScore.setBounds(380, 50, 125, 30);
		lblScore.setText("0");
		
		// TODO: remove me!
		Button btnGameover = new Button(gamecomp, SWT.NONE);
		btnGameover.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				GameDatabase db = new GameDatabase();
				try {
					int score = Integer.parseInt(lblScore.getText());
					db.writeDataBase(txtName.getText(), score);
				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
		});
		btnGameover.setBounds(430, 381, 75, 25);
		btnGameover.setText("GameOver");
		
		setFocus();
	}
	
	public void changeScore(int score) {
		String str = String.format("%d", score);
		lblScore.setText(str);
	}
	
	public void addToScore(int score) {
		int prev = Integer.parseInt(lblScore.getText());
		String str = String.format("%d", score+prev);
		lblScore.setText(str);
	}
}