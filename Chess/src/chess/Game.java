package chess;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Dimension;
/*
 * UI and menu bar
 */
public class Game extends JFrame implements ActionListener
{
	private static final long serialVersionUID = 1L;
	
	private State mState = new State();
	private CLib clib = new CLib();
	private ArrayList<Grid> mGrids;

	public Game()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnGame = new JMenu("Game");
		menuBar.add(mnGame);
		
		int width = 800;
		int height = 800 + menuBar.getHeight();
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((screenSize.width - width) / 2, (screenSize.height - height) / 2, width, height);
		/*
		 * new button is to start a new game, all pieces are at initial position
		 */
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(e ->
		{
			mState.init();
			update(mState);
		});
		mnGame.add(mntmNew);
		/*
		 * The depth is initial as 1 this button increase the depth by 1
		 */
		JMenuItem mntmnDepthI = new JMenuItem("Depth Increase");
		mntmnDepthI.addActionListener(e ->{
			CLib.nDepth = CLib.nDepth+1;
			System.out.println("Current Depth:" + CLib.nDepth);
			
		});
		mnGame.add(mntmnDepthI);
		/*
		 * The depth is initial as 1 and this button is decrease the depth by 1
		 */
		JMenuItem mntmnDepthD = new JMenuItem("Depth Decrease");
		mntmnDepthD.addActionListener(e ->{
			CLib.nDepth = CLib.nDepth-1;
			System.out.println("Current Depth:"+ CLib.nDepth);
		});
		mnGame.add(mntmnDepthD);
/*
 * load the previous saved position
 */
		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(e ->
		{
			mState.load();
			update(mState);
		});
		mnGame.add(mntmLoad);
/*
 * save the position as well as generate the report of history steps
 */
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(e -> mState.save());
		mnGame.add(mntmSave);
	
		getContentPane().setLayout(new GridLayout(8, 8, 1, 1));
		
		initGrids();
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Grid g = (Grid)e.getSource();
		mState.actionPerformed(g.getCol(), g.getRow());
		update(mState);
	}
	
	public void update(State mBoard)
	{
		for (Grid mGrid : mGrids)
		{
			mGrid.update(mBoard);
		}
	}
	
	private void initGrids()
	{
		mGrids = new ArrayList<>();
		
		for (int row = 0; row < 8; row++)
		{
			for (int col = 0; col < 8; col++)
			{
				Grid grid = new Grid(row, col);
				grid.addActionListener(this);
				getContentPane().add(grid);
				mGrids.add(grid);
			}
		}
	}
}