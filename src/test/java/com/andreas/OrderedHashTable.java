package com.andreas;

import com.andreas.WordAttribute;
import se.kth.id1020.util.Attributes;
import se.kth.id1020.util.Document;
import se.kth.id1020.util.Sentence;
import se.kth.id1020.util.Word;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by andreas on 2016-12-16.
 */
public class OrderedHashTable {
    private HashMap<String, List<WordAttribute>> hashMap = new HashMap<>();
    private HashMap<Document, Integer> documentSizeMap = new HashMap<>();


    public void put(Sentence sentence, Attributes attributes){
        Integer integer = documentSizeMap.get(attributes.document);
        int before = 0;
        if (integer != null)
            before += integer;
        documentSizeMap.put(attributes.document, before + sentence.getWords().size());

        for (Word word : sentence.getWords())
            put(new WordAttribute(word,attributes));
    }

    public int getNumberOfDocuments(){
        return documentSizeMap.size();
    }

    public List<WordAttribute> get(String key){
        List<WordAttribute> listOfWordAttributes = hashMap.get(key);
        if (listOfWordAttributes == null)
            return null;
        return listOfWordAttributes;
    }

    public int getSizeOfDocument(Document document){
        return documentSizeMap.get(document);
    }

    private void put(WordAttribute wordAttribute){


        String key = wordAttribute.word.word;
        List<WordAttribute> listOfWordAttributes = hashMap.get(key);
        if (listOfWordAttributes == null){
            listOfWordAttributes = new ArrayList<>();
            hashMap.put(key, listOfWordAttributes);
        }
        listOfWordAttributes.add(wordAttribute);
    }

}
