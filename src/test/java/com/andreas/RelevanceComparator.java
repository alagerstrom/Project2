package com.andreas;

import java.util.Comparator;

/**
 * Created by andreas on 2016-12-19.
 */
public class RelevanceComparator implements Comparator<DocumentEntry>{

    public int compare(DocumentEntry o1, DocumentEntry o2) {
        if (o1.getRelevance() < o2.getRelevance())
            return -1;
        else if (o1.getRelevance() > o2.getRelevance())
            return 1;
        return 0;
    }
}
