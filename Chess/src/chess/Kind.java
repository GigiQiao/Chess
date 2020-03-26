package chess;
/*
 * there are six types of pieces in the chess game
 */
public enum Kind
{
	Pawn("Pawn"),
	Rook("Rook"),
	Knight("Knight"),
	Bishop("Bishop"),
	Queen("Queen"),
	King("King");

	private String mText;

	Kind(String mText)
	{
		this.mText = mText;
	}

	String toText()
	{
		return mText;
	}
}