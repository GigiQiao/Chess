package chess;

import javax.swing.*;
import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
/*
 * get the state and evaluate the score based on the evaluation function and table
 * doing the alpha beta pruning to get the next move based on the score for each location
 * it first generate a list of possible position and based on the score to make a alpha beta tree
 */
public class State implements IState
{
	private State mParent;
	private ArrayList<Piece> mPieces;
	private boolean bSide;
	private Piece mPending;
	private Piece mLastMoved;
	private ArrayList<Point> mMoves;
	private int nDepth;
	private int nPiece;
	private int nMove;
	private double dMin;
	private double dMax;

	private int mSrcCol;
	private int mSrcRow;

	private int mDstCol;
	private int mDstRow;

	private boolean bStalemate;

	private boolean bEnded;
	private boolean bWin;

	private String mSteps;

	public State()
	{
		mPieces = new ArrayList<>();
		bSide = true;
		mPending = null;
		mLastMoved = null;
		mMoves = new ArrayList<>();
		nDepth = 0;

		nPiece = 0;
		nMove = 0;

		dMin = Double.MAX_VALUE;
		dMax = Double.MIN_VALUE;

		bStalemate = false;
		bEnded = false;
		bWin = false;

		mSteps = "";
	}
	
	/*
	 * alpha-beta pruning method 
	 */

	private void AlphaBetaPruning()
	{
		nDepth = 0;
		dMin = Double.MAX_VALUE;
		dMax = Double.MIN_VALUE;
		CLib.resetMinMax();

		//	The list of all successors must be produced at first
		ArrayList<State> prunedSuccessors = new ArrayList<>();
		ArrayList<State> successors = allSuccessors();

		if (isStalemate())
		{
			bStalemate = true;
			JOptionPane.showMessageDialog(null, "Stalemate.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}

		
		for (State state : successors)
		{
			state.getValue();

			if (state.dMax != -Double.MAX_VALUE)
				prunedSuccessors.add(state);
		}

		CLib.sort(prunedSuccessors);

		actionPerformed(prunedSuccessors.get(0).mSrcCol, prunedSuccessors.get(0).mSrcRow);
		actionPerformed(prunedSuccessors.get(0).mDstCol, prunedSuccessors.get(0).mDstRow);
	}
	/*
	 * save the file that can be use for reload to continue next time
	 * and it also generate the step.txt file at the same time.
	 * which is the history that the move performed by each side
	 */

	protected void save()
	{
		try
		{
			StringBuilder pieces = new StringBuilder();

			for (int i = 0; i < mPieces.size(); i++)
			{
				pieces.append(mPieces.get(i).toText());

				if (i != mPieces.size() - 1)
				{
					pieces.append("&");
				}
			}

			String state = String.format("%s#%s#%s", pieces.toString(), bSide, mSteps);

			FileOutputStream os = new FileOutputStream(new File("Chess.txt"), false);
			os.write(state.getBytes());
			os.close();

			os = new FileOutputStream(new File("Steps.txt"), false);
			os.write(mSteps.getBytes());
			os.close();
		}
		catch (Exception ignored)
		{

		}
	}
	/*
	 * load the previous saved game based on the history in the "chess.txt" file
	 * by using the list of argument that in the piece class to get the needed information
	 */

	public void load()
	{
		try
		{
			File file = new File("Chess.txt");

			if (file.exists())
			{
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[is.available()];
				is.read(bytes);
				is.close();

				String content = new String(bytes);

				String[] arguments = content.split("#");
				bSide = Boolean.parseBoolean(arguments[1]);
				mSteps = arguments[2];

				String[] pieces = arguments[0].split("&");

				mPieces.clear();

				for (String piece : pieces)
				{
					mPieces.add(new Piece(this, piece));
				}
			}
		}
		catch (Exception e)
		{
			System.err.println(e.getMessage());
		}
	}

	private void getValue()
	{
		if (nDepth != CLib.nDepth)
		{
			while (true)
			{
				State successor = singleSuccessor();

				if (successor != null)
				{
					successor.getValue();

				// if the next one is larger than the current maximum then do the beta pruning

					if (CLib.hasMinMax() && bSide)
					{
						if (successor.dMax > CLib.dMax)
						{
							System.out.println("Beta Pruned");
							break;
						}
					}

					// if the next one is smaller than the current minimum then do the alpha pruning

					if (CLib.hasMinMax() && !bSide)
					{
						if (successor.dMin < CLib.dMin)
						{
							System.out.println("Alpha pruned");
							break;
						}
					}

					
				}
				else	// minimax get the score
				{
					mParent.receiveScore(dMin, dMax);
					CLib.postScore(dMin, dMax);
					break;
				}
			}
		}
		else	
		{
			evaluate();
			mParent.receiveScore(dMin, dMax);
		}
	}

	private void receiveScore(double dMin, double dMax)
	{
		this.dMax = Math.max(this.dMax, dMax);
		this.dMin = Math.min(this.dMin, dMin);
	}

	/////////////////////////////////////////////
	/*
	 * evaluate to get the score for each pieces in current location 
	 * based on the evaluation table and the piece score
	 */

	private void evaluate()
	{
		double score = 0;

		for (Piece mPiece : mPieces)
		{
			double d = mPiece.getHeuristicValue();
			score += d;
		}

		dMin = Math.min(dMin, score);
		dMax = Math.max(dMax, score);
	}

	/* 
	 * the performance of each successor, then use it as individual in the all successor list
	 *  check every pieces on the board, and get the  available move for each piece
	 *  if there is no available move for current piece then move to the next piece
	 */
	private State singleSuccessor()
	{
		for (int i = nPiece; i < mPieces.size(); i++)
		{
			if (mPieces.get(i).getSide() == bSide)
			{
				ArrayList<Point> moves = mPieces.get(i).getMoves();

				if (nMove < moves.size())
				{
					State state = copy();
					state.nDepth++;
					state.bSide = !bSide;
					Piece pPiece = state.getPiece(mPieces.get(i).getCol(), mPieces.get(i).getRow());
					pPiece.moveTo(moves.get(nMove).x, moves.get(nMove).y);

					nMove++;
					return state;
				}
				else
				{
					nMove = 0;
					nPiece++;
				}
			}
		}

		return null;
	}
	/*
	 * make all the available successor to a successor list 
	 * it's use for getting the evaluation score and for alpha beta pruning
	 */

	private ArrayList<State> allSuccessors()
	{
		ArrayList<State> nodes = new ArrayList<>();

		for (Piece mPiece : mPieces)
		{
			if (mPiece.getSide() == bSide)
			{
				ArrayList<Point> moves = mPiece.getMoves();

				for (Point move : moves)
				{
					State nBoard = copy();
					nBoard.nDepth++;
					nBoard.bSide = !bSide;

					nBoard.mSrcCol = mPiece.getCol();
					nBoard.mSrcRow = mPiece.getRow();

					nBoard.mDstCol = move.x;
					nBoard.mDstRow = move.y;

					Piece pPiece = nBoard.getPiece(nBoard.mSrcCol, nBoard.mSrcRow);
					pPiece.moveTo(nBoard.mDstCol, nBoard.mDstRow);

					nodes.add(nBoard);
				}
			}
		}

		return nodes;
	}

	double getMax()
	{
		return dMax;
	}

	/*
	 * make a copy of all the piece at current location and 
	 * use for perform the minimax / alpha-beta pruning
	 */
	State copy()
	{
		State state = new State();

		state.mParent = this;

		for (int i = 0; i < mPieces.size(); i++)
		{
			Piece p = new Piece(state, mPieces.get(i));
			state.mPieces.add(p);
		}
		
		state.bSide = bSide;
		
		if (mPending != null)
		{
			state.mPending = state.getPiece(mPending.getCol(), mPending.getRow());
		}
		
		if (mLastMoved != null)
		{
			state.mLastMoved = state.getPiece(mLastMoved.getCol(), mLastMoved.getRow());
		}
		
		state.mMoves.addAll(mMoves);
		state.nDepth = nDepth;
		state.nPiece = 0;
		state.nMove = 0;
		state.dMin = Double.MAX_VALUE;
		state.dMax = -Double.MAX_VALUE;
		state.bStalemate = false;
		state.bEnded = false;
		state.bWin = false;

		return state;
	}
	/*
	 * initialize the piece position, which is use in the "Game" class use for the new game in the menu bar
	 * put all the piece at the initial position to start the game
	 */
	
	public void init()
	{
		mPieces = new ArrayList<>();
		bSide = true;
		mPending = null;
		mLastMoved = null;
		mMoves = new ArrayList<>();
		bStalemate = false;
		bEnded = false;
		
		nDepth = 0;
		
		for (int i = 0; i < 8; i++)
		{
			mPieces.add(new Piece(this, false, Kind.Pawn, i, 1));
			mPieces.add(new Piece(this, true, Kind.Pawn, i, 6));
		}

		mPieces.add(new Piece(this, false, Kind.Rook, 0, 0));
		mPieces.add(new Piece(this, true, Kind.Rook, 0, 7));

		mPieces.add(new Piece(this, false, Kind.Knight, 1, 0));
		mPieces.add(new Piece(this, true, Kind.Knight, 1, 7));

		mPieces.add(new Piece(this, false, Kind.Bishop, 2, 0));
		mPieces.add(new Piece(this, true, Kind.Bishop, 2, 7));

		mPieces.add(new Piece(this, false, Kind.Queen, 3, 0));
		mPieces.add(new Piece(this, true, Kind.Queen, 3, 7));

		mPieces.add(new Piece(this, false, Kind.King, 4, 0));
		mPieces.add(new Piece(this, true, Kind.King, 4, 7));

		mPieces.add(new Piece(this, false, Kind.Bishop, 5, 0));
		mPieces.add(new Piece(this, true, Kind.Bishop, 5, 7));

		mPieces.add(new Piece(this, false, Kind.Knight, 6, 0));
		mPieces.add(new Piece(this, true, Kind.Knight, 6, 7));

		mPieces.add(new Piece(this, false, Kind.Rook, 7, 0));
		mPieces.add(new Piece(this, true, Kind.Rook, 7, 7));
	}
	/*
	 * when there is a stalemate, or win/loss happened return a message by Jpanel and 
	 * use for generate the "step.txt"
	 * for each step it getting the position at initial and final and the piece name to generate the final report
	 */
	public void actionPerformed(int col, int row)
	{
		if (bStalemate)
		{
			JOptionPane.showMessageDialog(null, "Stalemate.", "", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!bEnded)
		{
			Piece p = getPiece(col, row);

			if (mPending == null)
			{
				if (p != null && p.getSide() == bSide)
				{
					mMoves = p.getMoves();
					mPending = p;
				}
			}
			else
			{
				Piece possibleRook = getPiece(col, row);

				if (isLegal(col, row) || (mPending.getKind() == Kind.King && !mPending.hasMoved() && possibleRook != null && possibleRook.getKind() == Kind.Rook && mPending.getSide() == possibleRook.getSide()))
				{
					if (mSteps.length() != 0)
					{
						mSteps += "\n";
						mSteps += String.format("%s - %s from (%d, %d) to (%d, %d)", mPending.getSide() ? "Player" : "AI", mPending.getKind().toText(), mPending.getCol(), mPending.getRow(), col, row);
					}
					else
					{
						mSteps = String.format("%s - %s from (%d, %d) to (%d, %d)", mPending.getSide() ? "Player" : "AI", mPending.getKind().toText(), mPending.getCol(), mPending.getRow(), col, row);
					}
					mPending.moveTo(col, row);
					mLastMoved = mPending;
					turn();
				}
				else
				{
					mPending = null;
					mMoves.clear();
					actionPerformed(col, row);
				}
			}
		}
		else
		{
			JOptionPane.showMessageDialog(null, String.format("%s Win. Game ended.", bWin ? "AI" : "Player"), "", JOptionPane.ERROR_MESSAGE);
		}
	}
	
/*
 * if is stalemate, then  return true otherwise return false
 */
	private boolean isStalemate()
	{
		ArrayList<State> successors = allSuccessors();

		if (!isChecked(bSide))
		{
			boolean notAllSuccessorsChecked = false;

			for (State state : successors)
			{
				if (!state.isChecked(true))
				{
					notAllSuccessorsChecked = true;
					break;
				}
			}

			return !notAllSuccessorsChecked;
		}
		
/*
 * check if there is only one king in the game for any side.
 */
		boolean onlyKingAlive = true;

		for (Piece piece : mPieces)
		{
			if (piece.bAlive && piece.getKind() != Kind.King)
				return false;
		}

		return false;
	}
	/*
	 * implement the pruning and judge the win/ loose and stalemate
	 */
	private void turn()
	{
		mPending = null;
		mMoves.clear();
		bSide = !bSide;
		/*
		 * if there is no stalemate, then starting alpha beta pruning
		 */
		if (!bSide)
		{
			AlphaBetaPruning();

			if (isStalemate())
			{
				bStalemate = true;
				JOptionPane.showMessageDialog(null, "Stalemate.", "", JOptionPane.ERROR_MESSAGE);
				return;
			}
			/*
			 * if the king is being attacked, then the game end, return the jpanel message
			 */
			for (Piece mPiece : mPieces)
			{
				if (mPiece.getKind() == Kind.King && !mPiece.bAlive)
				{
					bWin = mPiece.getSide();

					JOptionPane.showMessageDialog(null, String.format("%s Win. Game ended.", bWin ? "AI" : "Player"), "", JOptionPane.ERROR_MESSAGE);
					bEnded = true;
					break;
				}
			}
		}
	}
	
	@Override
	/*
	 * (non-Javadoc)
	 * @see chess.IState#getPiece(int, int)
	 * get the piece at the location 
	 */
	public Piece getPiece(int col, int row)
	{
		for (Piece mPiece : mPieces)
		{
			if (mPiece.at(col, row))
			{
				return mPiece;
			}
		}
		
		return null;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see chess.IState#isLegal(int, int)
	 * check if the move can be make or nots
	 */
	public boolean isLegal(int col, int row)
	{
		for (Point mMove : mMoves)
		{
			if (mMove.x == col && mMove.y == row)
				return true;
		}
		
		return false;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see chess.IState#isSelected(int, int)
	 * check if the chess piece is being selected or not
	 */
	public boolean isSelected(int col, int row)
	{
		return mPending != null && mPending.getCol() == col && mPending.getRow() == row;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see chess.IState#getPassingPawn(boolean, int, int)
	 * check if the pawn can perform the en passant
	 */
	public Piece getPassingPawn(boolean bSide, int col, int row)
	{
		Piece p = getPiece(col, row);

		if (row == (bSide ? 3 : 4) && p != null && p == mLastMoved && p.getMove() == 1 && p.getSide() != bSide)
			return p;
		
		return null;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see chess.IState#isChecked(boolean)
	 * check if the king is in threat or not
	 */
	public boolean isChecked(boolean side)
	{
		int kingCol = 0;
		int kingRow = 0;

		for (Piece mPiece : mPieces)
		{
			if (mPiece.getSide() == side && mPiece.getKind() == Kind.King)
			{
				kingCol = mPiece.getCol();
				kingRow = mPiece.getRow();
				break;
			}
		}

		return beingAttacked(side, kingCol, kingRow);
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see chess.IState#beingAttacked(boolean, int, int)
	 * check if any piece can be attacked or not
	 */
	public boolean beingAttacked(boolean side, int col, int row)
	{
		ArrayList<Point> opponentMoves = new ArrayList<>();

		for (Piece p : mPieces)
		{
			if (p.getSide() != side)
			{
				opponentMoves.addAll(p.getMoves());
			}
		}

		for (Point point : opponentMoves)
		{
			if (point.x == col && point.y == row)
				return true;
		}

		return false;
	}
}