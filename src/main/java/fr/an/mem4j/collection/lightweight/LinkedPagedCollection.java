package fr.an.mem4j.collection.lightweight;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;

import fr.an.mem4j.collection.lightweight.impl.LinkablePageNode;
import fr.an.mem4j.collection.lightweight.impl.Linked16PageNode;
import fr.an.mem4j.collection.lightweight.impl.Linked32PageNode;

/**
 * <PRE>
 *                         +--------------------+     +---
 * +----------------+  <---|-prevPage, nextPage-|---> |
 * | 4: int size    |      | value0,value1,...  |     |
 * | 4/8 ptr: head -|----> |         value8/32  |     |
 * | 4/8 ptr: tail -|--    +--------------------+     +----
 * +----------------+  \                                /
 *                      \                              /
 *                       ------------------------------
 * </PRE>
 */
public abstract class LinkedPagedCollection<T> extends AbstractCollection<T> implements Collection<T>, Queue<T> {

    protected int size;
    
    protected LinkablePageNode<T> headPage;
    
    protected LinkablePageNode<T> tailPage;

    // ------------------------------------------------------------------------

    protected LinkedPagedCollection() {
    }

    public static <T> LinkedPagedCollection<T> new16Paged() {
        return new Linked16PageNode.Linked16PagedCollection<>();
    }

    public static <T> LinkedPagedCollection<T> new32Paged() {
        return new Linked32PageNode.Linked32PagedCollection<>();
    }

    // ------------------------------------------------------------------------

    protected abstract LinkablePageNode<T> createPage();

    // ------------------------------------------------------------------------
    
    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        this.size = 0;
        this.headPage = null;
        this.tailPage = null;
    }
    
    @Override
    public boolean add(T e) {
        if (tailPage == null) {
            headPage = tailPage = createPage();
        }
        if (tailPage.getFreeSlotCount() == 0) {
            LinkablePageNode<T> page = createPage();
            tailPage.setNextPage(page);
            page.setPrevPage(tailPage);
            this.tailPage = page;
        }
        tailPage.add(e);
        this.size++;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        boolean res = super.remove(o); // use find by iterator, then remove if found
        if (res) {
            size--;
        }
        return res;
    }
    
    @Override
    public Iterator<T> iterator() {
        return new InnerIterator(headPage, 0);
    }

    protected class InnerIterator implements Iterator<T> {
        private LinkablePageNode<T> currPage;
        private int currIndex;
        // private LinkablePageNode<T> prevPage;
        
        public InnerIterator(LinkablePageNode<T> currPage, int currIndex) {
            this.currPage = currPage;
            this.currIndex = currIndex;
        }

        @Override
        public boolean hasNext() {
            return currPage != null && (currIndex < currPage.getPageSize() || currPage.getNextPage() != null);
        }

        @Override
        public T next() {
            if (currIndex >= currPage.getPageSize()) {
                this.currIndex = 0;
                this.currPage = currPage.getNextPage();
            }
            T res = currPage.get(currIndex);
            this.currIndex++;
            return res;
        }

        @Override
        public void remove() {
            currPage.remove(currIndex-1);
            this.currIndex--;
        }
        
    }

    // ------------------------------------------------------------------------
    
    public T removeFirst() {
        if (headPage == null) {
            throw new NoSuchElementException();
        }
        T res = headPage.get(0);
        headPage.remove(0);
        if (headPage.getPageSize() == 0) {
            LinkablePageNode<T> headNextPage = headPage.getNextPage();
            if (headNextPage != null) {
                headNextPage.setPrevPage(null);
            } else {
                tailPage = null;
            }
            headPage = headNextPage;
        }
        this.size--;
        return res;
    }
    
    public T removeLast() {
        if (tailPage == null) {
            throw new NoSuchElementException();
        }
        int lastTailPageIndex = tailPage.getPageSize() - 1;
        T res = tailPage.get(lastTailPageIndex);
        tailPage.remove(lastTailPageIndex);
        if (lastTailPageIndex == 0) {
            LinkablePageNode<T> tailPrevPage = tailPage.getPrevPage();
            if (tailPrevPage != null) {
                tailPrevPage.setNextPage(null);
            } else {
                headPage = null;
            }
            tailPage = tailPrevPage;
        }
        this.size--;
        return res;
    }
    
    //implements Queue
    // ------------------------------------------------------------------------

    /**
     * cf java.util.Queue
     * add element to tail of queue
     */
    @Override
    public boolean offer(T value) {
        add(value);
        return true;
    }

    /**
     * cf java.util.Queue
     * @return the head of this queue removed
     * @throws NoSuchElementException if this queue is empty
     */
    @Override
    public T remove() {
        return removeLast();
    }

    /**
     * cf java.util.Queue
     * @return the removed head of this queue, or {@code null} if this queue is empty
     */
    @Override
    public T poll() {
        if (size == 0) {
            return null;
        }
        return removeFirst();
    }

    /**
     * cf java.util.Queue
     * @return the head of this queue (retreive but not removed)
     * @throws NoSuchElementException if this queue is empty
     */
    @Override
    public T element() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return headPage.get(0);
    }

    /**
     * cf java.util.Queue
     * @return the head of this queue, or {@code null} if this queue is empty
     */
    @Override
    public T peek() {
        if (size == 0) {
            return null;
        }
        return headPage.get(0);
    }
    
    // ------------------------------------------------------------------------
    
    @Override
    public String toString() {
        return super.toString(); // use iterator
    }
    
}
