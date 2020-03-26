package chess;
/*
 * to implement the color setting in CLib class
 */
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Grid extends JButton
{
	private static final long serialVersionUID = 1L;
	
	private int mRow;
	private int mCol;
	
	public Grid(int mRow, int mCol)
	{
		this.mRow = mRow;
		this.mCol = mCol;
		/*
		 * set up the chess board color
		 */
		setBackground((mRow + mCol) % 2 == 0 ? CLib.Black : CLib.White);
	}
	
	public void update(State mBoard)
	{
		Piece piece = mBoard.getPiece(mCol, mRow);
		
		if (piece != null)
		{
			ImageIcon icon = piece.getIcon();
			icon.setImage(icon.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_DEFAULT ));
			setIcon(icon);
		}
		else
		{
			setIcon(null);
		}
		/*
		 * make the legal (available move based on the rule) square green and other squares remains the 
		 * same color as initial
		 */
		if (mBoard.isLegal(mCol,  mRow))
		{
			setBackground(CLib.Green);
		}
		else
		{
			setBackground((mRow + mCol) % 2 == 0 ? CLib.Black : CLib.White);
		}
		/*
		 * make the clicked piece color blue
		 */
		if (mBoard.isSelected(mCol,  mRow))
		{
			setBackground(CLib.Blue);
		}
	}
	
	int getRow()
	{
		return mRow;
	}
	
	int getCol()
	{
		return mCol;
	}
}