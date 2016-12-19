package com.andreas;

import se.kth.id1020.util.Document;

/**
 * DocumentEntry
 * Created by andreas on 2016-12-19.
 */

public class DocumentEntry {
    public final Document document;
    private double relevance;
    private int count = 1;

    public DocumentEntry(Document document) {
        this.document = document;
    }
    public void increaseCount(){
        count++;
    }
    public int getCount(){
        return this.count;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString(){
        return "[" + document.toString() + ", count: " + count + ", relevance: " + relevance + "]";
    }
}
