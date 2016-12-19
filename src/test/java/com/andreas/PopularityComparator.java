package com.andreas;

import se.kth.id1020.util.Document;

import java.util.Comparator;

/**
 * Created by andreas on 2016-12-19.
 */
public class PopularityComparator implements Comparator<DocumentEntry> {
    public int compare(DocumentEntry o1, DocumentEntry o2) {
        if (o1.document.popularity < o2.document.popularity)
            return -1;
        else if (o1.document.popularity > o2.document.popularity)
            return 1;
        return 0;
    }
}
