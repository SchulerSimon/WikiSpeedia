

import java.util.Iterator;

public class PageSetIterator implements Iterator<PageSet> {

	private final PageSet set;
	private boolean hasNext = true;

	public PageSetIterator(PageSet set) {
		this.set = set;
	}

	@Override
	public boolean hasNext() {
		for (PageSet child : set.children) {
			if (child.iterator().hasNext()) {
				return true;
			}
		}
		return hasNext;
	}

	@Override
	public PageSet next() {
		for(PageSet child : set.children) {
			if(child.iterator().hasNext()){
				return child.iterator().next();
			}
		}
		hasNext = false;
		return set;
	}
}
