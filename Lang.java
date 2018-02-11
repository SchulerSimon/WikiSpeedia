

public enum Lang {
	DE, EN;

	/**
	 * converts enum into url
	 * default is EN
	 * @param l
	 * @return
	 */
	public static String getUrl(Lang l) {
		switch (l) {
		case DE:
			return "de.";
		case EN:
			return "en.";
		default:
			return "en.";
		}
	}
}
