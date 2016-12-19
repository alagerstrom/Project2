package com.andreas;

import javafx.util.Pair;
import se.kth.id1020.Driver;
import se.kth.id1020.TinySearchEngineBase;

import java.util.LinkedList;

/**
 * Main
 * Created by andreas on 2016-11-28.
 */

public class Main {
    public static void main(String[] args) throws Exception{
        System.out.println("Tiny Search Engine\n");
        TinySearchEngineBase searchEngine = new TinySearchEngine();
        System.out.println("Indexing...");
        Driver.run(searchEngine);

    }
}
