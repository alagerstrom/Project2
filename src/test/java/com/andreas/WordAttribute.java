package com.andreas;

import se.kth.id1020.util.Attributes;
import se.kth.id1020.util.Word;

/**
 * WordAttribute
 *
 * A WordAttribute has one instance of Word and one instance of Attributes,
 * and a count set when the word is counted by TinySearchEngine. If the count
 * is -1, it means that it has not yet been counted.
 *
 * Created by andreas on 2016-11-29.
 */

public class WordAttribute implements Comparable<WordAttribute>{

    public final Word word;
    public final Attributes attributes;
    public int count = -1;

    public WordAttribute(Word word, Attributes attributes){
        this.word = word;
        this.attributes = attributes;
    }

    public int compareTo(WordAttribute wordAttributes) {
        return this.word.word.compareToIgnoreCase(wordAttributes.word.word);
    }
    @Override
    public String toString(){
        return this.word.toString() + " " + this.attributes.toString() + " Count: " + this.count + "\n";
    }
    public int compareTo(String string) {
        return this.word.word.compareToIgnoreCase(string);
    }
}
