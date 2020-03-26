package chess;

import java.awt.Point;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class Piece extends JButton
{
	private static final long serialVersionUID = 1L;
	
	protected IState mState;
	private boolean bSide;
	private Kind mKind;
	private int mCol;
	private int mRow;
	
	private ImageIcon mIcon;
	
	private ArrayList<Point> mMoves = new ArrayList<>();
	private boolean bConsecutive;
	
	private boolean bMoved;
	boolean bAlive;
	
	private int nMove = 0;
	private double dValue;

	protected String toText()
	{
		return String.format("%s$%d$%d$%d$%s$%s$%d", bSide, mKind.ordinal(), mCol, mRow, bMoved, bAlive, nMove);
	}
/*
 * shows the points of each chess piece in the chess program: 
 * the king worth 900, the queen worth 90, bishop worth 30, knight worth 30, rook worth 50 and pawn worth 10.
 */
	public Piece(IState mState, String text)
	{
		/*
		 * it same as "public Piece(IState mBoard, boolean bSide, Kind mKind, int mCol, int mRow)"
		 * it is for generate the history move "Step.txt"file by using a list of string that include 7 informations
		 * the white or black side, the type of the piece, position column and position row, have it been moved before or not
		 * is it still on the board or already be captured, the number of move it have
		 * 
		 */
		String arguments[] = text.split("\\$");

		this.mState = mState;
		bSide = Boolean.parseBoolean(arguments[0]);
		mKind = Kind.values()[Integer.parseInt(arguments[1])];
		mCol = Integer.parseInt(arguments[2]);
		mRow = Integer.parseInt(arguments[3]);
		bMoved = Boolean.parseBoolean(arguments[4]);
		bAlive = Boolean.parseBoolean(arguments[5]);
		nMove = Integer.parseInt(arguments[6]);


		switch (mKind)
		{
			case Pawn:
			{
				mIcon = new ImageIcon(bSide ? "resources/pawn0.png" : "resources/pawn1.png");
				/*
				 * because the pawn can only move forward. thus, it can just make the y add one at each time
				 * the score of the pawn is 10
				 */
				mMoves.add(new Point(0, 1));

				bConsecutive = true;
				dValue = 10 * (bSide ? 1 : -1);

				break;
			}

			case Rook:
			{
				mIcon = new ImageIcon(bSide ? "resources/rook0.png" : "resources/rook1.png");
				/*
				 * rook can move horizontally leftward or rightward or vertically forward or backward
				 * thus, it can add one or minus one on x or y. 
				 * The number of square can be times by the other parameter
				 * the score of a rook is 50
				 */
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				bConsecutive = true;
				dValue = 50 * (bSide ? 1 : -1);

				break;
			}

			case Knight:
			{
				mIcon = new ImageIcon(bSide ? "resources/knight0.png" : "resources/knight1.png");
				/*
				 * knight can move to a "L" shaped squares, 
				 * which is vertically add 2 or minus two with a horizontally minus or add 1, or
				 * horizontally add or minus two with a vertically add or minus 1
				 * the score of a knight is 30
				 */
				mMoves.add(new Point(-2, 1));
				mMoves.add(new Point(2, 1));
				mMoves.add(new Point(-1, 2));
				mMoves.add(new Point(1, 2));

				mMoves.add(new Point(-2, -1));
				mMoves.add(new Point(2, -1));
				mMoves.add(new Point(-1, -2));
				mMoves.add(new Point(1, -2));

				bConsecutive = false;
				dValue = 30 * (bSide ? 1 : -1);

				break;
			}

			case Bishop:
			{
				mIcon = new ImageIcon(bSide ? "resources/bishop0.png" : "resources/bishop1.png");
				/*
				 * bishop can move to a diagonal position, 
				 * which is add one or minus one horizontally and add one vertically, or
				 * add one or minus one horizontally and minus one vertically.
				 * the score of the bishop is 30
				 */
				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = true;
				dValue = 30 * (bSide ? 1 : -1);

				break;
			}

			case Queen:
			{
				mIcon = new ImageIcon(bSide ? "resources/queen0.png" : "resources/queen1.png");
				/*
				 * Queen can move with both horizontally, vertically and diagonally with any number of squares. 
				 * it just need to list the rule when move to one square and the other parameter can times it 
				 * to get the final movement
				 * the score of the queen is 90
				 */

				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = true;
				dValue = 90 * (bSide ? 1 : -1);

				break;
			}

			case King:
			{
				mIcon = new ImageIcon(bSide ? "resources/king0.png" : "resources/king1.png");
				/*
				 * King can move same to queen, but without the parameter that can times ,
				 * which means king can only move one square at a time
				 * the king is the most important piece in the game.
				 * it has a score of 900.
				 */

				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = false;
				dValue = 900 * (bSide ? 1 : -1);

				break;
			}

			default:
				break;
		}
	}
	
	public Piece(IState mState, Piece p)
	{
		/*
		 * put the piece onto the board
		 */
		this.mState = mState;
		bSide = p.bSide;
		mKind = p.mKind;
		mCol = p.mCol;
		mRow = p.mRow;
		
		switch (mKind)
		{
			case Pawn:
			{
				mIcon = new ImageIcon(bSide ? "resources/pawn0.png" : "resources/pawn1.png");
				break;
			}

			case Rook:
			{
				mIcon = new ImageIcon(bSide ? "resources/rook0.png" : "resources/rook1.png");
				break;
			}

			case Knight:
			{
				mIcon = new ImageIcon(bSide ? "resources/knight0.png" : "resources/knight1.png");
				break;
			}

			case Bishop:
			{
				mIcon = new ImageIcon(bSide ? "resources/bishop0.png" : "resources/bishop1.png");
				break;
			}

			case Queen:
			{
				mIcon = new ImageIcon(bSide ? "resources/queen0.png" : "resources/queen1.png");
				break;
			}

			case King:
			{
				mIcon = new ImageIcon(bSide ? "resources/king0.png" : "resources/king1.png");
				break;
			}

			default:
				break;
		}
		
		mMoves.addAll(p.mMoves);
		bConsecutive = p.bConsecutive;
		bMoved = p.bMoved;
		bAlive = p.bAlive;
		
		nMove = p.nMove;
		dValue = p.dValue;
	}

	public Piece(IState mBoard, boolean bSide, Kind mKind, int mCol, int mRow)
	{
		/*
		 * put pieces onto the chess board, make the chess can move based on the rule and evaluate the chess score.
		 * for player player is a positive value and the enemy is a negative value
		 * boolean value to state if it has been moved or not and it is still alive(being captured) or not
		 */
		this.mState = mBoard;
		this.bSide = bSide;
		this.mKind = mKind;
		this.mCol = mCol;
		this.mRow = mRow;

		switch (mKind)
		{
			case Pawn:
			{
				mIcon = new ImageIcon(bSide ? "resources/pawn0.png" : "resources/pawn1.png");
				
				mMoves.add(new Point(0, 1));
				
				bConsecutive = true;
				dValue = 10 * (bSide ? 1 : -1);
				
				break;
			}

			case Rook:
			{
				mIcon = new ImageIcon(bSide ? "resources/rook0.png" : "resources/rook1.png");
				
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));
				
				bConsecutive = true;
				dValue = 50 * (bSide ? 1 : -1);
				
				break;
			}

			case Knight:
			{
				mIcon = new ImageIcon(bSide ? "resources/knight0.png" : "resources/knight1.png");
				
				mMoves.add(new Point(-2, 1));
				mMoves.add(new Point(2, 1));
				mMoves.add(new Point(-1, 2));
				mMoves.add(new Point(1, 2));

				mMoves.add(new Point(-2, -1));
				mMoves.add(new Point(2, -1));
				mMoves.add(new Point(-1, -2));
				mMoves.add(new Point(1, -2));

				bConsecutive = false;
				dValue = 30 * (bSide ? 1 : -1);
				
				break;
			}

			case Bishop:
			{
				mIcon = new ImageIcon(bSide ? "resources/bishop0.png" : "resources/bishop1.png");
				
				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = true;
				dValue = 30 * (bSide ? 1 : -1);
				
				break;
			}

			case Queen:
			{
				mIcon = new ImageIcon(bSide ? "resources/queen0.png" : "resources/queen1.png");
				
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = true;
				dValue = 50 * (bSide ? 1 : -1);
				
				break;
			}

			case King:
			{
				mIcon = new ImageIcon(bSide ? "resources/king0.png" : "resources/king1.png");
				
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = false;
				dValue = 900 * (bSide ? 1 : -1);
				
				break;
			}

			default:
				break;
		}

		bMoved = false;
		bAlive = true;
	}
	
	public void moveTo(int mCol, int mRow)
	{
		Piece target = mState.getPiece(mCol, mRow);
		
		if (target != null && target.bSide != bSide)
			target.setCaptured();
		//车王易位: castling
		/*
		 *to check if the king is not move and not attack by other pieces 
		 *the rook is not move and they are at the same row
		 *then the casting condition satisfy 
		 *2 type of castling.  
		 */
		if (mKind == Kind.King && !mState.beingAttacked(bSide, mCol, mRow) && !bMoved &&
				target != null && target.bSide == bSide && target.mKind == Kind.Rook && !target.bMoved && mRow == target.getRow())
		{
			ArrayList<Point> points = new ArrayList<>();

			for (int i = Math.min(mCol, target.mCol) + 1; i < Math.max(mCol, target.mCol); i++)
			{
				points.add(new Point(i, mRow));
			}

			boolean swapProhibited = false;

			for (Point point : points)
			{
				if (mState.beingAttacked(bSide, point.x, point.y) || mState.getPiece(point.x, point.y) != null)
				{
					swapProhibited = true;
					break;
				}
			}

			if (!swapProhibited)
			{
				if (target.getCol() == 0)	//	长易位
				{
					this.mCol = 2;
					target.mCol = 3;
				}
				else
				{
					this.mCol = 6;
					target.mCol = 5;
				}

				bMoved = true;
				nMove++;

				target.bMoved = true;
				target.nMove++;

				return;
			}
		}
		
		  //过路吃兵： En passant capture the piece by using en passant
		/*
		 * 
		 */
		 

		if (mKind == Kind.Pawn)
		{
			if (mCol != getCol() && mRow != getRow())
			{
				Piece PassingPawn = mState.getPassingPawn(getSide(), mCol, getRow());

				if (PassingPawn != null)
					PassingPawn.setCaptured();
			}
			/*
			 * promotion:
			 * if the pawn reach the end then it automatically changed to a Queen
			 */

			if (getKind() == Kind.Pawn && getRow() == (getSide() ? 1 : 6) && mRow == (getSide() ? 0 : 7))
			{
				setKind(Kind.Queen);
				System.out.println("Changed.");
			}
		}

		this.mCol = mCol;
		this.mRow = mRow;
		bMoved = true;
		nMove++;
	}
	
	public int getMove()
	{
		return nMove;
	}
	
	public boolean hasMoved()
	{
		return bMoved;
	}
	
	public int getCol()
	{
		return mCol;
	}
	
	public int getRow()
	{
		return mRow;
	}
	
	public Kind getKind()
	{
		return mKind;
	}
	
	public boolean getSide()
	{
		return bSide;
	}
	
	/*
	 *get the calculated score based on the square table and the value of the piece type
	 */
	
	public double getHeuristicValue()
	{
		if (!bAlive)
			return 0;
		
		switch (mKind)
		{
			case King:
			{
				return dValue + CLib.KingTable[bSide ? mRow : 7 - mRow][mCol] * (bSide ? 1 : -1);
			}
			
			case Queen:
			{
				return dValue + CLib.QueenTable[bSide ? mRow : 7 - mRow][mCol] * (bSide ? 1 : -1);
			}
			
			case Rook:
			{
				return dValue + CLib.RookTable[bSide ? mRow : 7 - mRow][mCol] * (bSide ? 1 : -1);
			}
			
			case Knight:
			{
				return dValue + CLib.KnightTable[bSide ? mRow : 7 - mRow][mCol] * (bSide ? 1 : -1);
			}
			
			case Bishop:
			{
				return dValue + CLib.BishopTable[bSide ? mRow : 7 - mRow][mCol] * (bSide ? 1 : -1);
			}
			
			case Pawn:
			{
				return dValue + CLib.PawnTable[bSide ? mRow : 7 - mRow][mCol] * (bSide ? 1 : -1);
			}
			
			default:
				return 0;
		}
	}
	/*
	 * get the array of possible moving list
	 */
	public ArrayList<Point> getMoves()
	{
		ArrayList<Point> Moves = new ArrayList<>();

		if (!bAlive)
			return Moves;
		
		if (mKind != Kind.Pawn)
		{
			for (Point mMove : mMoves)
			{
				for (int j = 1; j <= (bConsecutive ? 8 : 1); j++)
				{
					int DestCol = mCol + mMove.x * j;
					int DestRow = mRow + (bSide ? -mMove.y * j : mMove.y * j);

					if (DestCol >= 8 || DestCol < 0 || DestRow >= 8 || DestRow < 0)
					{
						break;
					}

					Piece Occupant = mState.getPiece(DestCol, DestRow);

					if (Occupant == null)
					{
						Moves.add(new Point(DestCol, DestRow));
					}
					else
					{
						if (Occupant.bSide != bSide)
						{
							Moves.add(new Point(DestCol, DestRow));
						}

						break;
					}
				}
			}
		}
		else
		{
			int DestRow = getRow() + (getSide() ? -1 : 1);

			if (DestRow >= 0 && DestRow < 8)
			{
				Piece AliveOccupant = mState.getPiece(getCol(), DestRow);

				if (AliveOccupant == null)
				{
					Moves.add(new Point(getCol(), DestRow));
				}
			}

			if (!hasMoved() && Moves.size() != 0)
			{
				DestRow = getRow() + 2 * (getSide() ? -1 : 1);

				if (DestRow < 8 && DestRow >= 0)
				{
					Piece AliveOccupant = mState.getPiece(getCol(), DestRow);

					if (AliveOccupant == null)
					{
						Moves.add(new Point(getCol(), DestRow));
					}
				}
			}

			int DestCol = getCol() + 1;
			DestRow = getRow() + (getSide() ? -1 : 1);

			if (DestCol <8 && DestCol >= 0 && DestRow < 8 && DestRow >= 0)
			{
				Piece AliveOccupant = mState.getPiece(DestCol, DestRow);
				Piece PassingPawn = mState.getPassingPawn(getSide(), getCol() + 1, getRow());

				if ((AliveOccupant != null && AliveOccupant.getSide() != getSide()) || PassingPawn != null)
				{
					Moves.add(new Point(DestCol, DestRow));
				}
			}

			DestCol = getCol() - 1;
			DestRow = getRow() + (getSide() ? -1 : 1);

			if (DestCol <8 && DestCol >= 0 && DestRow < 8 && DestRow >= 0)
			{
				Piece AliveOccupant = mState.getPiece(DestCol, DestRow);
				Piece PassingPawn = mState.getPassingPawn(getSide(), getCol() - 1, getRow());

				if ((AliveOccupant != null && AliveOccupant.getSide() != getSide()) || PassingPawn != null)
				{
					Moves.add(new Point(DestCol, DestRow));
				}
			}
		}

		return Moves;
	}
	/*
	 * initialized capture as false
	 */
	public void setCaptured()
	{
		bAlive = false;
	}
	
	public boolean at(int col, int row)
	{
		return mCol == col && mRow == row && bAlive;
	}
	
	public ImageIcon getIcon()
	{
		return mIcon;
	}
	/*
	 * set type of the piece at each postion and can be updated
	 */
	public void setKind(Kind eKind)
	{
		mKind = eKind;
		mMoves.clear();
		
		switch (mKind)
		{
			case Pawn:
			{
				mIcon = new ImageIcon(bSide ? "resources/pawn0.png" : "resources/pawn1.png");
				mMoves.add(new Point(0, 1));
				
				bConsecutive = true;
				dValue = 10 * (bSide ? 1 : -1);
				
				break;
			}

			case Rook:
			{
				mIcon = new ImageIcon(bSide ? "resources/rook0.png" : "resources/rook1.png");
				
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));
				
				bConsecutive = true;
				dValue = 50 * (bSide ? 1 : -1);
				
				break;
			}

			case Knight:
			{
				mIcon = new ImageIcon(bSide ? "resources/knight0.png" : "resources/knight1.png");
				
				mMoves.add(new Point(-2, 1));
				mMoves.add(new Point(2, 1));
				mMoves.add(new Point(-1, 2));
				mMoves.add(new Point(1, 2));

				mMoves.add(new Point(-2, -1));
				mMoves.add(new Point(2, -1));
				mMoves.add(new Point(-1, -2));
				mMoves.add(new Point(1, -2));

				bConsecutive = false;
				dValue = 30 * (bSide ? 1 : -1);
				
				break;
			}

			case Bishop:
			{
				mIcon = new ImageIcon(bSide ? "resources/bishop0.png" : "resources/bishop1.png");

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = true;
				dValue = 30 * (bSide ? 1 : -1);
				
				break;
			}

			case Queen:
			{
				mIcon = new ImageIcon(bSide ? "resources/queen0.png" : "resources/queen1.png");
				
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = true;
				dValue = 50 * (bSide ? 1 : -1);
				
				break;
			}

			case King:
			{
				mIcon = new ImageIcon(bSide ? "resources/king0.png" : "resources/king1.png");
				
				mMoves.add(new Point(1, 0));
				mMoves.add(new Point(-1, 0));
				mMoves.add(new Point(0, -1));
				mMoves.add(new Point(0, 1));

				mMoves.add(new Point(1, 1));
				mMoves.add(new Point(-1, 1));
				mMoves.add(new Point(1, -1));
				mMoves.add(new Point(-1, -1));

				bConsecutive = false;
				dValue = 900 * (bSide ? 1 : -1);
				
				break;
			}

			default:
				break;
		}
	}
}