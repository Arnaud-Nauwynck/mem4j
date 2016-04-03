package fr.an.mem4j.collection.lightweight.impl;

import fr.an.mem4j.collection.lightweight.LinkedPagedCollection;

public class Linked32PageNode<T> extends AbstractLinkedPageNode<T> {

    private static final int PAGE_ALLOC_LEN = 32;

    public static class Linked32PagedCollection<T> extends LinkedPagedCollection<T> {

        public Linked32PagedCollection() {
        }

        @Override
        protected LinkablePageNode<T> createPage() {
            return new Linked32PageNode<>();
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
    protected T value16;
    protected T value17;
    protected T value18;
    protected T value19;
    protected T value20;
    protected T value21;
    protected T value22;
    protected T value23;
    protected T value24;
    protected T value25;
    protected T value26;
    protected T value27;
    protected T value28;
    protected T value29;
    protected T value30;
    protected T value31;
    protected T value32;

    
    // ------------------------------------------------------------------------

    public Linked32PageNode() {
    }

    // ------------------------------------------------------------------------

    @Override
    public int getFreeSlotCount() {
        return PAGE_ALLOC_LEN - pageSize;
    }
    
    public void insert(int index, T e) {
        switch(index) {
        case 31: 
        case 30: value31 = value30; // no break
        case 29: value30 = value29; // no break
        case 28: value29 = value28; // no break
        case 27: value28 = value27; // no break
        case 26: value27 = value26; // no break
        case 25: value26 = value25; // no break
        case 24: value25 = value24; // no break
        case 23: value24 = value23; // no break
        case 22: value23 = value22; // no break
        case 21: value22 = value21; // no break
        case 20: value21 = value20; // no break
        case 19: value20 = value19; // no break
        case 18: value19 = value18; // no break
        case 17: value18 = value17; // no break
        case 16: value17 = value16; // no break
        case 15: value16 = value15; // no break
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
        case 16: value16 = e; break;
        case 17: value17 = e; break;
        case 18: value18 = e; break;
        case 19: value19 = e; break;
        case 20: value20 = e; break;
        case 21: value21 = e; break;
        case 22: value22 = e; break;
        case 23: value23 = e; break;
        case 24: value24 = e; break;
        case 25: value25 = e; break;
        case 26: value26 = e; break;
        case 27: value27 = e; break;
        case 28: value28 = e; break;
        case 29: value29 = e; break;
        case 30: value30 = e; break;
        case 31: value31 = e; break;
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
        case 15: value15 = value16; // no break
        case 16: value16 = value17; // no break
        case 17: value17 = value18; // no break
        case 18: value18 = value19; // no break
        case 19: value19 = value20; // no break
        case 20: value20 = value21; // no break
        case 21: value21 = value22; // no break
        case 22: value22 = value23; // no break
        case 23: value23 = value24; // no break
        case 24: value24 = value25; // no break
        case 25: value25 = value26; // no break
        case 26: value26 = value27; // no break
        case 27: value27 = value28; // no break
        case 28: value28 = value29; // no break
        case 29: value29 = value30; // no break
        case 30: value30 = value31; // no break
        case 31: value31 = null;
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
        case 16: return value16;
        case 17: return value17;
        case 18: return value18;
        case 19: return value19;
        case 20: return value20;
        case 21: return value21;
        case 22: return value22;
        case 23: return value23;
        case 24: return value24;
        case 25: return value25;
        case 26: return value26;
        case 27: return value27;
        case 28: return value28;
        case 29: return value29;
        case 30: return value30;
        case 31: return value31;
        default: throw new IllegalArgumentException();
        }
    }

}