package fr.an.mem4j.collection.lightweight.impl;

import fr.an.mem4j.collection.lightweight.LinkedPagedCollection;

public class Linked16PageNode<T> extends AbstractLinkedPageNode<T> {

    private static final int PAGE_ALLOC_LEN = 16;
    
    public static class Linked16PagedCollection<T> extends LinkedPagedCollection<T> {

        public Linked16PagedCollection() {
        }

        @Override
        protected LinkablePageNode<T> createPage() {
            return new Linked16PageNode<>();
        }
        
    }
    
    
    protected T value0;
    protected T value1;
    protected T value2;
    protected T value3;
    protected T value4;
    protected T value5;
    protected T value6;
    protected T value7;
    protected T value8;
    protected T value9;
    protected T value10;
    protected T value11;
    protected T value12;
    protected T value13;
    protected T value14;
    protected T value15;

    // ------------------------------------------------------------------------

    public Linked16PageNode() {
    }

    // ------------------------------------------------------------------------

    @Override
    public int getFreeSlotCount() {
        return PAGE_ALLOC_LEN - pageSize;
    }
    
    public void insert(int index, T e) {
        switch(index) {
        case 15: // no break 
        case 14: value15 = value14; // no break
        case 13: value14 = value13; // no break
        case 12: value13 = value12; // no break
        case 11: value12 = value11; // no break
        case 10: value11 = value10; // no break
        case 9: value10 = value9; // no break
        case 8: value9 = value8; // no break
        case 7: value8 = value7; // no break
        case 6: value7 = value6; // no break
        case 5: value6 = value5; // no break
        case 4: value5 = value4; // no break
        case 3: value4 = value3; // no break
        case 2: value3 = value2; // no break
        case 1: value2 = value1; // no break
        case 0: value1 = value0;
            break;
        default: throw new IllegalArgumentException();
        }
        set(index, e);
        this.pageSize++;
    }

    public void set(int index, T e) {
        switch(index) {
        case 0: value0 = e; break;
        case 1: value1 = e; break;
        case 2: value2 = e; break;
        case 3: value3 = e; break;
        case 4: value4 = e; break;
        case 5: value5 = e; break;
        case 6: value6 = e; break;
        case 7: value7 = e; break;
        case 8: value8 = e; break;
        case 9: value9 = e; break;
        case 10: value10 = e; break;
        case 11: value11 = e; break;
        case 12: value12 = e; break;
        case 13: value13 = e; break;
        case 14: value14 = e; break;
        case 15: value15 = e; break;
        default: throw new IllegalArgumentException();
        }
    }
    
    @Override
    public void remove(int index) {
        switch(index) {
        case 0: value0 = value1; // no break
        case 1: value1 = value2; // no break
        case 2: value2 = value3; // no break
        case 3: value3 = value4; // no break
        case 4: value4 = value5; // no break
        case 5: value5 = value6; // no break
        case 6: value6 = value7; // no break
        case 7: value7 = value8; // no break
        case 8: value8 = value9; // no break
        case 9: value9 = value10; // no break
        case 10: value10 = value11; // no break
        case 11: value11 = value12; // no break
        case 12: value12 = value13; // no break
        case 13: value13 = value14; // no break
        case 14: value14 = value15; // no break
        case 15: value15 = null;
            break; 
        default: throw new IllegalArgumentException();
        }
        this.pageSize--;
    }

    @Override
    public T get(int index) {
        switch(index) {
        case 0: return value0;
        case 1: return value1;
        case 2: return value2;
        case 3: return value3;
        case 4: return value4;
        case 5: return value5;
        case 6: return value6;
        case 7: return value7;
        case 8: return value8;
        case 9: return value9;
        case 10: return value10;
        case 11: return value11;
        case 12: return value12;
        case 13: return value13;
        case 14: return value14;
        case 15: return value15;
        default: throw new IllegalArgumentException();
        }
    }
    
}
