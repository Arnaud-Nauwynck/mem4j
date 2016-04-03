package fr.an.mem4j.collection.lightweight.impl;

public abstract class AbstractLinkedPageNode<T> implements LinkablePageNode<T> {

    protected LinkablePageNode<T> prevPage;
    protected LinkablePageNode<T> nextPage;
    
    protected int pageSize;
    

    @Override
    public LinkablePageNode<T> getPrevPage() {
        return prevPage;
    }

    @Override
    public void setPrevPage(LinkablePageNode<T> prevPage) {
        this.prevPage = prevPage;
    }

    @Override
    public LinkablePageNode<T> getNextPage() {
        return nextPage;
    }

    @Override
    public void setNextPage(LinkablePageNode<T> nextPage) {
        this.nextPage = nextPage;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public void add(T e) {
        set(pageSize, e);
        this.pageSize++;
    }

}
