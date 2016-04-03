package fr.an.mem4j.collection.lightweight.impl;

public interface LinkablePageNode<T> {

    public LinkablePageNode<T> getPrevPage();
    public void setPrevPage(LinkablePageNode<T> p);
    
    public LinkablePageNode<T> getNextPage();
    public void setNextPage(LinkablePageNode<T> p);
    
    public int getFreeSlotCount();

    public void add(T value);
    public void remove(int index);
    public T get(int index);
    public void set(int index, T value);
    
    public int getPageSize();
    
}
