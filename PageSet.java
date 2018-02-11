

import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * this class represents a page + the set of its linked pages
 * 
 * @author Simon Schuler
 *
 */
public class PageSet implements Iterable<PageSet> {
	private final String url;
	protected final PageSet father;
	protected final HashSet<PageSet> children;
	public final Lang lang;
	public final Direction dir;
	protected boolean done = false;
	private final Iterator<PageSet> myIterator;

	/**
	 * non java-Doc
	 * 
	 * @param title
	 * @param url
	 * @param father
	 * @param l
	 * @param d
	 */
	public PageSet(String url, PageSet father, Lang l, Direction d) {
		this.url = url;
		this.father = father;
		this.lang = l;
		this.dir = d;
		children = new HashSet<PageSet>();
		this.myIterator = new PageSetIterator(this);
	}

	/**
	 * returns the Iterator associated with this PageSet
	 */
	@Override
	public Iterator<PageSet> iterator() {
		return myIterator;
	}

	/**
	 * This Method makes sure every site is represented just once, and then adds
	 * the correspondent child
	 * 
	 * @param child
	 *            the Child to be added
	 */
	public void addChild(PageSet child) {
		done = true;
		if (isFather(child)) {
			if (WikiSpeedia.DEBUG) {
				System.out.println("Prevented loop for this: " + getPath() + " and: " + child.url);
			}
			return;
		}
		this.children.add(child);
	}

	/**
	 * calculates the size of this set
	 * 
	 * @return
	 */
	public int size() {
		AtomicInteger ret = new AtomicInteger(1);
		children.forEach(child -> ret.addAndGet(child.size()));
		if (this.father == null) {
			if (WikiSpeedia.DEBUG) {
				System.out.println(
						"Tree " + Direction.getString(dir) + " " + url + " has now " + ret.get() + " elements");
			}
		}
		return ret.get();
	}

	/**
	 * this Method is for testing if there is a loop
	 * 
	 * @param child
	 * @return true if (this or any of the fathers).equals(child) else false
	 */
	protected boolean isFather(PageSet child) {
		// test if there is a loop
		if (this.equals(child)) {
			return true;
		} else if (father == null) {
			return false;
		} else {
			return father.isFather(child);
		}
	}

	/**
	 * non-javaDoc
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof PageSet)) {
			return false;
		}
		PageSet s = (PageSet) o;
		return s.url.equals(this.url);
	}

	/**
	 * non-javaDoc
	 */
	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(Direction.getArrows(dir) + url);
		// using lambda-expressions like a boss
		children.forEach(child -> ret.append("\n  " + child.toString()));
		return ret.toString();
	}

	/**
	 * returns the fully qualified url for this Object.
	 * 
	 * @return
	 */
	public String getUrl() {
		String ret = "https://" + Lang.getUrl(this.lang) + Direction.getUrl(this.dir) + this.url;
		if (this.dir == Direction.TO) {
			ret += "&limit=5000";
		}
		return ret;
	}

	/**
	 * @return returns the Path that leads from from to to
	 */
	public String getPath() {
		if (father == null)
			return url;
		if (dir == Direction.FROM) {
			return father.getPath() + Direction.getArrows(Direction.FROM) + url;
		} else {
			String s = "";
			if (!this.children.isEmpty()) {
				s = url;
			}
			s += Direction.getArrows(Direction.FROM) + father.getPath();
			return s;
		}
	}
}
