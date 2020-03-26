package chess;
/*
 * Interface of the State
 */
public interface IState
{
	Piece getPiece(int col, int row);
	boolean isLegal(int col, int row);
	boolean isSelected(int col, int row);
	Piece getPassingPawn(boolean bPlayer, int col, int row);
	boolean beingAttacked(boolean side, int col, int row);
	boolean isChecked(boolean side);
}