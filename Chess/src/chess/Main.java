package chess;

import java.awt.EventQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
 * to make the ndepth can change during the game 
 * the main method of the program
 */

public class Main
{
	public static void main(String[] args)
	{
		if(args.length ==1){
			Matcher mer = Pattern.compile("^[0-9]+$").matcher(args[0]);
			if(mer.find()){
				CLib.nDepth = Integer.parseInt(args[0]);
			}
		}
		EventQueue.invokeLater(() -> {
			try
			{
				Game mBoard = new Game();
				mBoard.setVisible(true);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		});
	}
}
