

public final class Excludables {
	public static String[] exclude = { "Main_Page", "[z]", "[c]", "f=" };

	public static boolean contains(String url) {
		if(isYear(url)) {
			return true;
		}
		for (String ex : exclude) {
			if (ex.equals(url)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isYear(String url) {
		if(url.length() == 4 && url.replaceAll("[0-9]", "").length() == 0) {
			return true;
		}
		return false;
	}

}
