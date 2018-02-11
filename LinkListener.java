

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinkListener {

	public static long byteCount = 0;
	public static long siteCount = 0;

	/**
	 * constructs all linked pages for one page. It excludes duplicates because
	 * it uses a HashSet and it avoids loops via the father.
	 * 
	 * how to avoid all loops ? -> u just dont.
	 * 
	 * @param page
	 * @param l
	 * @return
	 */
	public static void listenToLinks(PageSet page) {
		String htmlCode = "";
		String finalUrl = page.getUrl();
		if (WikiSpeedia.INFO) {
			System.out.println("search in: " + finalUrl);
		}
		try {
			htmlCode = download(finalUrl);
		} catch (FileNotFoundException fnfe) {
			if (WikiSpeedia.ERROR) {
				System.out.println("not found:" + finalUrl);
			}
			return;
		} catch (Exception e) {
			if (WikiSpeedia.ERROR) {
				System.out.println(e.getClass() + "-url:" + finalUrl);
				e.printStackTrace();
			}
			return;
		}

		// replace all linebreaks
		htmlCode.replaceAll("\\r\\n|\\r|\\n", " ");
		htmlCode.replaceAll("\\<br( )*\\/\\>", " ");
		// replace redundent spaces
		htmlCode.replaceAll("( ){2,}", " ");
		// find all hrefs as desired
		// \<a href=\"\/wiki\/[a-zA-Z\-_%()0-9#]*".*?\<\/a\>
		// is the regex pattern
		Pattern p = Pattern.compile("\\<a href=\"\\/wiki\\/[a-zA-Z\\-_%()0-9#]*\".*?\\<\\/a\\>");
		Matcher m = p.matcher(htmlCode);
		while (m.find()) {
			// get the line wich was matched
			String line = htmlCode.substring(m.start(), m.end());
			// extract title
			String title = getTitle(line);
			// extract url
			String url = getUrl(line);
			if (WikiSpeedia.DEBUG) {
				System.out.println("url:\t" + url + "\ntitle:\t" + title);
			}
			if (Excludables.contains(url) || Excludables.contains(title)) {
				if (WikiSpeedia.DEBUG) {
					System.out.println("excluded " + url);
				}
				continue;
			}
			// see if there is a Link with different url than title
			// this is just for the looks to resolve things like
			// Url:Peter_Rochegune_Munch
			// Title: View the content page [c]
			// <a href="/wiki/Peter_Rochegune_Munch" title="View the content
			// page [c]">Article</a>
			if (!title.toLowerCase().startsWith(url.substring(0, 1).toLowerCase())) {
				if (WikiSpeedia.DEBUG) {
					System.out.println("Title and Url differ too much, just forget the title");
				}
				// resolve this Problem
				title = url;
			}

			if (WikiSpeedia.DEBUG) {
				System.out.println("htmlLink:\t" + line);
			}

			// create new Kid
			PageSet child = new PageSet(url, page, page.lang, page.dir);
			page.addChild(child);
		}
		if (WikiSpeedia.INFO) {
			System.out.println("found links " + page.size());
		}
	}

	/**
	 * extracts the title
	 * 
	 * @param line
	 * @return
	 */
	private static String getTitle(String line) {
		String title = line.substring(jumpTo(line, "title=\"") + 7);
		title = title.substring(0, jumpTo(title, "\""));
		return title;
	}

	/**
	 * extracts the url
	 * 
	 * @param line
	 * @return
	 */
	private static String getUrl(String line) {
		String url = line.substring(15);
		url = url.substring(0, jumpTo(url, "\""));
		return trimUrl(url);
	}

	/**
	 * this method turns links with paragraphs into normal links
	 * some_article#some_paragraph -> some_article
	 * 
	 * @param url
	 * @return
	 */
	private static String trimUrl(String url) {
		if (!url.contains("#")) {
			return url;
		} else {
			if (WikiSpeedia.DEBUG) {
				System.out.println("trimming url: " + url);
			}
			return url.substring(0, jumpTo(url, "#"));
		}
	}

	/**
	 * finds the next toFind and returns the index of the start.
	 * 
	 * @param line
	 * @param toFind
	 * @return
	 */
	private static int jumpTo(String line, String toFind) {
		return line.indexOf(toFind);
	}

	/**
	 * opens an url and downloads the site.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	private static String download(String url) throws Exception {
		// download the html-code of the site.
		URL site = new URL(url);
		BufferedReader reader = new BufferedReader(new InputStreamReader(site.openStream()));

		// stuff all of that into a String char by char
		StringBuilder stringbuilder = new StringBuilder();
		int c;
		while ((c = reader.read()) != -1) {
			stringbuilder.append((char) c);
			byteCount++;
		}

		siteCount++;
		return stringbuilder.toString();
	}
}
