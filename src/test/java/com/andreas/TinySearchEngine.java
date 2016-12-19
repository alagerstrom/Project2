package com.andreas;

import edu.princeton.cs.algs4.Stack;
import se.kth.id1020.TinySearchEngineBase;
import se.kth.id1020.util.Attributes;
import se.kth.id1020.util.Document;
import se.kth.id1020.util.Sentence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by andreas on 2016-12-12.
 */
public class TinySearchEngine implements TinySearchEngineBase {

    public static final String ORDER_BY = "orderby";
    public static final String PROPERTY_RELEVANCE = "relevance";
    public static final String PROPERTY_POPULARITY = "popularity";
    public static final String DIRECTION_ASC = "asc";
    public static final String DIRECTION_DESC = "desc";
    public static final String OPERATOR_INTERSECTION = "+";
    public static final String OPERATOR_UNION = "|";
    public static final String OPERATOR_DIFFERENCE = "-";


    private OrderedHashTable orderedHashTable = new OrderedHashTable();
    private Cache cache = new Cache(orderedHashTable);

    private String infixString;

    @Override
    public void preInserts() {

    }

    @Override
    public void insert(Sentence sentence, Attributes attributes) {
        orderedHashTable.put(sentence,attributes);
    }

    @Override
    public void postInserts() {

    }

    @Override
    public List<Document> search(String s) {
        infixString = "";

        String[] strings = s.split("\\s+");
        if (strings.length <= 1){
            return getDocuments(printDocuments(simpleQuery(s)));
        }

        int indexOfOrderBy = findIndexOfOrderBy(strings);
        if (indexOfOrderBy < 0){
            return getDocuments(printDocuments(query(strings)));
        }else {
            if (strings.length != indexOfOrderBy + 3){
                printError();
                return null;
            }
            String property = strings[indexOfOrderBy + 1];
            String direction = strings[indexOfOrderBy + 2];

            if (isInvalidPropertyArgument(property)){
                printError();
                return null;
            }
            if (isInvalidDirectionArgument(direction)){
                printError();
                return null;
            }
            String[] queryPart = new String[strings.length - 3];
            for (int i = 0 ; i < queryPart.length ; i++)
                queryPart[i] = strings[i];
            List <DocumentEntry> result = query(queryPart);

            sort(result, property, direction);

            return getDocuments(printDocuments(result));
        }
    }

    @Override
    public String infix(String s) {
        return infixString;
    }

    private List<DocumentEntry> printDocuments(List<DocumentEntry> documentEntries) {
        for (DocumentEntry documentEntry : documentEntries)
            System.out.println("DocumentEntry " + documentEntry + ", total count: " + orderedHashTable.getSizeOfDocument(documentEntry.document));
        return documentEntries;
    }


    private List<Document> getDocuments(List<DocumentEntry> documentEntries){
        List<Document> result = new ArrayList<>();
        for (DocumentEntry documentEntry : documentEntries)
            if (!result.contains(documentEntry.document))
                result.add(documentEntry.document);
        return result;
    }

    private void sort(List<DocumentEntry> list, String property, String direction) {
        if (property.equalsIgnoreCase(PROPERTY_POPULARITY)){
            if (direction.equalsIgnoreCase(DIRECTION_ASC)){
                Collections.sort(list, new PopularityComparator());
            }else {
                Collections.sort(list, new PopularityComparator().reversed());
            }
        }else {
            if (direction.equalsIgnoreCase(DIRECTION_ASC)){
                Collections.sort(list, new RelevanceComparator());
            }else {
                Collections.sort(list, new RelevanceComparator().reversed());
            }
        }
    }

    private List<DocumentEntry> query(String[] strings) {

        Stack<List<DocumentEntry>> operandStack = new Stack<>();

        Stack<String> infixStack = new Stack<>();

        if (!isOperator(strings[0]))
            return simpleQuery(strings[0]);

        if (isOperator(strings[strings.length - 1]) || isOperator(strings[strings.length - 2])){
            printError();
            return null;
        }
        int index = strings.length - 1;


        while (index >= 0){
            String token = strings[index];
            if (isOperator(token)){
                List<DocumentEntry> operand1 = operandStack.pop();
                List<DocumentEntry> operand2 = operandStack.pop();
                operandStack.push(performOperation(token, operand1, operand2));

                infixStack.push("(" + infixStack.pop() + " " +  token + " " + infixStack.pop() + ")");
            }else{
                operandStack.push(simpleQuery(token));
                infixStack.push(token);
            }
            index--;
        }

        infixString = infixStack.pop();
        return operandStack.pop();
    }

    private List<DocumentEntry> performOperation(String operator, List<DocumentEntry> operand1, List<DocumentEntry> operand2) {
        if (operator.equals(OPERATOR_INTERSECTION))
            return intersection(operand1, operand2);
        else if (operator.equals(OPERATOR_DIFFERENCE))
            return difference(operand1, operand2);
        else
            return union(operand1,operand2);
    }

    private List<DocumentEntry> union(List<DocumentEntry> operand1, List<DocumentEntry> operand2) {
        List<DocumentEntry> result = new ArrayList<>();

        result.addAll(operand1);

        for (DocumentEntry secondDocumentEntry : operand2){
            int foundIndex = -1;
            for (int i = 0; i < result.size(); i++){
                DocumentEntry firstDocumentEntry = result.get(i);
                if (firstDocumentEntry.document.equals(secondDocumentEntry.document)){

                    foundIndex = i;
                }
            }
            if (foundIndex > 0){
                DocumentEntry newDocumentEntry = new DocumentEntry(result.get(foundIndex).document);
                newDocumentEntry.setRelevance(result.get(foundIndex).getRelevance() + secondDocumentEntry.getRelevance());
                newDocumentEntry.setCount(result.get(foundIndex).getCount() + secondDocumentEntry.getCount());
                result.add(newDocumentEntry);
            }else {
                result.add(secondDocumentEntry);
            }
        }
        return result;
    }

    private List<DocumentEntry> difference(List<DocumentEntry> operand1, List<DocumentEntry> operand2) {
        List<DocumentEntry> result = new ArrayList<>();
        result.addAll(operand1);
        for (DocumentEntry documentEntry : operand2){
            int foundIndex = -1;
            for (int i = 0; i < result.size(); i++)
                if (documentEntry.document.equals(result.get(i).document))
                    foundIndex = i;
            if (foundIndex >= 0)
                result.remove(foundIndex);
        }
        return result;
    }

    private List<DocumentEntry> intersection(List<DocumentEntry> operand1, List<DocumentEntry> operand2) {
        List<DocumentEntry> result = new ArrayList<>();

        for (DocumentEntry firstDocumentEntry : operand1){
            for (int i = 0; i < operand2.size(); i++){
                DocumentEntry secondDocumentEntry = operand2.get(i);
                if (firstDocumentEntry.document.equals(secondDocumentEntry.document)){
                    DocumentEntry resultDocumentEntry = new DocumentEntry(firstDocumentEntry.document);
                    resultDocumentEntry.setRelevance(firstDocumentEntry.getRelevance() + secondDocumentEntry.getRelevance());
                    resultDocumentEntry.setCount(firstDocumentEntry.getCount() + secondDocumentEntry.getCount());
                    result.add(resultDocumentEntry);

                }
            }
        }
        return result;
    }

    private int findIndexOfOrderBy(String[] strings) {
        int result = -1;
        for (int i = 0; i < strings.length; i++)
            if (strings[i].equalsIgnoreCase(ORDER_BY))
                result = i;
        return result;
    }





    private List<DocumentEntry> simpleQuery(String s){
        List<WordAttribute> wordAttributes = orderedHashTable.get(s);

        List<DocumentEntry> result = calculateRelevances(wordAttributes);

        return result;
    }

    private List<DocumentEntry> calculateRelevances(List<WordAttribute> wordAttributes) {
        List<DocumentEntry> result = new ArrayList<>();

        // Count duplicate words
        if (wordAttributes != null){
            for (WordAttribute wordAttribute : wordAttributes){
                boolean found = false;
                for (int i = 0; i < result.size(); i++){
                    if (result.get(i).document.equals(wordAttribute.attributes.document)){
                        result.get(i).increaseCount();
                        found = true;
                        break;
                    }
                }
                if (!found){
                    result.add(new DocumentEntry(wordAttribute.attributes.document));
                }
            }
        }

        // Calculate relevance

        for (DocumentEntry documentEntry : result){
            double termFrequency = (double) documentEntry.getCount()/orderedHashTable.getSizeOfDocument(documentEntry.document);
            double inverseDocumentFrequency = Math.log10((double) orderedHashTable.getNumberOfDocuments()/result.size());
            documentEntry.setRelevance(termFrequency * inverseDocumentFrequency);
        }

        return result;
    }


    private boolean isOperator(String s){
        return s.equals(OPERATOR_DIFFERENCE) ||
                s.equals(OPERATOR_INTERSECTION) ||
                s.equals(OPERATOR_UNION);
    }

    private boolean isInvalidDirectionArgument(String direction) {
        return !direction.equalsIgnoreCase(DIRECTION_ASC) &&
                !direction.equalsIgnoreCase(DIRECTION_DESC);
    }

    private boolean isInvalidPropertyArgument(String property) {
        return !property.equalsIgnoreCase(PROPERTY_RELEVANCE) &&
                !property.equalsIgnoreCase(PROPERTY_POPULARITY);
    }

    private void printError() {
        System.out.println("Syntax error.");
        System.out.println("Usage: [EXPRESSION] orderby [PROPERTY] [DIRECTION]");
    }
}
