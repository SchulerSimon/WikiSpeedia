

import java.util.Date;
import java.util.LinkedList;

/**
 * this class is the main executor for the Wikispeedia algorythms.
 * 
 * @author Simon Schuler
 *
 */
public class WikiSpeedia {

	public static final boolean DEBUG = false;
	public static final boolean INFO = true;
	public static final boolean ERROR = true;

	private long start, end;

	private final PageSet from, to;

	/**
	 * constructor.
	 * @param args
	 */
	public WikiSpeedia(String[] args) {
		start = new Date().getTime();
		if (DEBUG) {
			System.out.println("input: From: " + args[0] + " To: " + args[1] + " recursionLimit: " + args[2]);
		}

		boolean found = false;
		// parse recursion limit
		int recursionLimit = 0;
		try {
			recursionLimit = Integer.parseInt(args[2]);
		} catch (NumberFormatException nfe) {
			if (ERROR) {
				System.out.println("nfe while parsing" + args[2]);
			}
			recursionLimit = 1;
		} catch (ArrayIndexOutOfBoundsException e) {
			recursionLimit = 1;
		}

		// create starting points
		from = new PageSet(args[0], null, Lang.EN, Direction.FROM);
		to = new PageSet(args[1], null, Lang.EN, Direction.TO);

		LinkedList<PageSet> fromList = getIntersectingObjects(from);
		LinkedList<PageSet> toList = getIntersectingObjects(to);
		LinkedList<PageSet> intersection;
		int recursionDepth = 0;
		do {
			recursionDepth++;
			if (INFO) {
				System.out.println("started recursion " + recursionDepth);
			}
			// wich recursion is smarter? from or to?
			double lambda = 0.5;
			if ((float)toList.size() * (lambda) < (float)fromList.size() * (1 - lambda)) {
				for (PageSet set : toList) {
					LinkListener.listenToLinks(set);
				}
				toList = getIntersectingObjects(to);
			} else {
				for (PageSet set : fromList) {
					LinkListener.listenToLinks(set);
				}
				fromList = getIntersectingObjects(from);
			}
			// calculate if there are intersections
			intersection = cloneList(fromList);
			intersection.retainAll(toList);
			if (intersection.size() > 0) {
				found = true;
				if (INFO) {
					System.out
							.println("found " + intersection.size() + " intersections in recursion " + recursionDepth);
				}
			}
		} while (!found && recursionDepth < recursionLimit);

		if (intersection.size() == 0) {
			System.out.println("no result with recursionLimit " + recursionLimit);
		}
		for (PageSet set : intersection) {
			System.out.println(getPath(set, toList.get(toList.indexOf(set))));
		}
		end = new Date().getTime();
		if (INFO) {
			System.out.println("\nsome additional information:" + collectAllInfo());
		}
	}

	/**
	 * private method to collect some information after the program is finished.
	 * @return
	 */
	private String collectAllInfo() {
		StringBuilder str = new StringBuilder();
		str.append("\nsites searched:\t\t" + LinkListener.siteCount);
		str.append("\nlinks found:\t\t" + (from.size() + to.size()));
		str.append("\nbytes downloaded:\t" + LinkListener.byteCount);
		str.append("\ntime to complete:\t" + (end - start) + "ms");

		return str.toString();
	}

	/**
	 * constructs the path from set1+set2
	 * @param set1
	 * @param set2
	 * @return
	 */
	private String getPath(PageSet set1, PageSet set2) {
		return set1.getPath() + set2.getPath();
	}

	/**
	 * clones a linkedList
	 * @param set
	 * @return
	 */
	private LinkedList<PageSet> cloneList(LinkedList<PageSet> set) {
		LinkedList<PageSet> list = new LinkedList<PageSet>();
		for (PageSet page : set) {
			list.add(page);
		}
		return list;
	}

	/**
	 * searches for all intersection partners
	 * @param set
	 * @return
	 */
	private LinkedList<PageSet> getIntersectingObjects(PageSet set) {
		LinkedList<PageSet> list = new LinkedList<PageSet>();
		for (PageSet page : set) {
			list.add(page);
		}
		return list;
	}

	/**
	 * non-javaDoc
	 * @param args
	 */
	public static void main(String[] args) {
		new WikiSpeedia(args);
	}
}
