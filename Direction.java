

public enum Direction {
	FROM, TO;
	
	/**
	 * converts enum into url
	 * default is FROM
	 * @param d
	 * @return
	 */
	public static String getUrl(Direction d) {
		switch (d) {
		case TO:
			return "wikipedia.org/w/index.php?title=Special:WhatLinksHere/";
		case FROM:
			return "wikipedia.org/wiki/";
		default:
			return "wikipedia.org/wiki/";
		}
	}
	
	/**
	 * returns a string wich indicates direction
	 * @param d
	 * @return
	 */
	public static String getString(Direction d) {
		switch (d) {
		case TO:
			return "over";
		case FROM:
			return "under";
		default:
			return " ";
		}
	}
	
	public static String getArrows(Direction d) {
		switch (d) {
		case TO:
			return "<-";
		case FROM:
			return "->";
		default:
			return "-";
		}
	}
}
