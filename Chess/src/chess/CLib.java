package chess;

import java.awt.Color;
import java.util.ArrayList;

public class CLib
/*
 * evaluate each piece in different location and get the score of the piece at that location
 */
{
	static int nDepth = 1;
	static double dMax = Double.MIN_VALUE;
	static double dMin = -Double.MAX_VALUE;

	static void resetMinMax()
	{
		dMin = Double.MAX_VALUE;
		dMax = -Double.MAX_VALUE;
	}

	static void postScore(double min, double max)
	{
		dMin = Math.min(dMin, min);
		dMax = Math.max(dMax, max);
	}

	static boolean hasMinMax()
	{
		return dMin != Double.MAX_VALUE || dMax != -Double.MAX_VALUE;
	}
	/*
	 * sorting 
	 * sort by ascending order on distance, if the distance is equal then sort by ascending order on
	 * coins, if it still equal, sort by ascending order on power
	 */

	static void sort(ArrayList<State> states)
	{
		for(int i = 0; i < states.size(); i++)
		{
			for(int j = 0; j < states.size() - 1 - i; j++)
			{
				double scoreJ = states.get(j).getMax();
				double scoreJ1 = states.get(j + 1).getMax();

				/*
				 * try different method of sorting
				 */
				if (scoreJ > scoreJ1)
				{
					State temp = states.get(j);
					states.set(j, states.get(j + 1));
					states.set(j + 1, temp);
				}
			}
		}
	}

	final static Color White = new Color(255,255,255);// the white part of the board
	final static Color Black = new Color(0,0,0);// the black part of the board
	final static Color Green = new Color(107, 194, 90);// the available moves 
	final static Color Blue = new Color(13, 113, 137);// the current location color when click onto it
	/*
	 * the evaluation function for different pieces in the chess game
	 */
	final static double[][] KingTable = new double[][] {
		{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
		{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
		{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
		{-3.0, -4.0, -4.0, -5.0, -5.0, -4.0, -4.0, -3.0},
		{-2.0, -3.0, -3.0, -4.0, -4.0, -3.0, -3.0, -2.0},
		{-1.0, -2.0, -2.0, -2.0, -2.0, -2.0, -2.0, -1.0},
		{ 2.0,  2.0,  0.0,  0.0,  0.0,  0.0,  2.0,  2.0},
		{ 2.0,  3.0,  1.0,  0.0,  0.0,  1.0,  3.0,  2.0}
	};
	
	final static double[][] QueenTable = new double[][]{
		{-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0},
		{-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
		{-1.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
		{-0.5,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
		{ 0.0,  0.0,  0.5,  0.5,  0.5,  0.5,  0.0, -0.5},
		{-1.0,  0.5,  0.5,  0.5,  0.5,  0.5,  0.0, -1.0},
		{-1.0,  0.0,  0.5,  0.0,  0.0,  0.0,  0.0, -1.0},
		{-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0}
	};
	
	final static double[][] RookTable = new double[][] {
		{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
		{ 0.5,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  0.5},
		{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
		{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
		{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
		{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
		{-0.5,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -0.5},
		{ 0.0,  0.0,  0.0,  0.5,  0.5,  0.0,  0.0,  0.0}
	};
	
	final static double[][] BishopTable = new double[][] {
		{-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0},
		{-1.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0, -1.0},
		{-1.0,  0.0,  0.5,  1.0,  1.0,  0.5,  0.0, -1.0},
		{-1.0,  0.5,  0.5,  1.0,  1.0,  0.5,  0.5, -1.0},
		{-1.0,  0.0,  1.0,  1.0,  1.0,  1.0,  0.0, -1.0},
		{-1.0,  1.0,  1.0,  1.0,  1.0,  1.0,  1.0, -1.0},
		{-1.0,  0.5,  0.0,  0.0,  0.0,  0.0,  0.5, -1.0},
		{-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0}
	};
	
	final static double[][] KnightTable = new double[][] {
		{-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
		{-4.0, -2.0,  0.0,  0.0,  0.0,  0.0, -2.0, -4.0},
		{-3.0,  0.0,  1.0,  1.5,  1.5,  1.0,  0.0, -3.0},
		{-3.0,  0.5,  1.5,  2.0,  2.0,  1.5,  0.5, -3.0},
		{-3.0,  0.0,  1.5,  2.0,  2.0,  1.5,  0.0, -3.0},
		{-3.0,  0.5,  1.0,  1.5,  1.5,  1.0,  0.5, -3.0},
		{-4.0, -2.0,  0.0,  0.5,  0.5,  0.0, -2.0, -4.0},
		{-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0},
	};
	
	final static double[][] PawnTable = new double[][] {
		{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0},
		{ 5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0,  5.0},
		{ 1.0,  1.0,  2.0,  3.0,  3.0,  2.0,  1.0,  1.0},
		{ 0.5,  0.5,  1.0,  2.5,  2.5,  1.0,  0.5,  0.5},
		{ 0.0,  0.0,  0.0,  2.0,  2.0,  0.0,  0.0,  0.0},
		{ 0.5, -0.5, -1.0,  0.0,  0.0, -1.0, -0.5,  0.5},
		{ 0.5,  1.0,  1.0, -2.0, -2.0,  1.0,  1.0,  0.5},
		{ 0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0,  0.0}
	};
}