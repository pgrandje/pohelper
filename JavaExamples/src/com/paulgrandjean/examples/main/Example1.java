package com.paulgrandjean.examples.main;

import com.paulgrandjean.examples.classes.ExampleClass1;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 * User: pgrandje
 * Date: 9/4/13
 * Time: 1:04 PM
 * To change this template use File | Settings | File Templates.
 */



public class Example1 {


    public static void main(String[] args) {

        System.out.println("Hello World");

        ExampleClass1 myExampleClass1 = new ExampleClass1();
        System.out.println("It stores: " + myExampleClass1.getTheInt());

        // Using the parameterized constructor
        ExampleClass1 mySecondExampleClass1 = new ExampleClass1(100);


        String myString1 = null;
        String myString2 = "Howdy Doody";

        if (myString2.equals("Howdy Doody")){
            // … do stuff here …
        }
        if (myString2.equals(myString1)){
         // … do stuff here …
        }



        ArrayList<ExampleClass1> myList = new ArrayList<ExampleClass1>();
        myList.add(new ExampleClass1(100));
        myList.add(new ExampleClass1(200));

        LinkedList<ExampleClass1> myLinkedList = new LinkedList<ExampleClass1>();
        myLinkedList.add(new ExampleClass1(5));
        myLinkedList.add(new ExampleClass1(10));

        int myInt = 5;
        int myInt2 = myInt + 10;

        for (int i=0; i<=10; i++) {
            System.out.println(i);
        }

        for (int i=0; i<=myList.size()-1; i++) {
            System.out.println("Found Item: " + myList.get(i).getTheInt());
        }

        String myString = "howdy doody";
        while (myString.equals("howdy doody")) {
            // .... do stuff .....
        };

        for(ExampleClass1 ex1: myList) {
            System.out.println(ex1.getTheInt());
        }

        Iterator<ExampleClass1> iterator = myList.iterator();
        while(iterator.hasNext()) {
            // ... do stuff ...
            iterator.next();
        }


        if (myString.equals("Kermit")) {
            // ... do some code here ....
        }

        if (myInt == 5) {
            // ... do some code here ...
        }


        try {
            if (myString.equals("howdy doody")) {
                throw new Exception("Howdy Doody Error occurred");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        LinkedList myPolymorphicList = new LinkedList();
        myPolymorphicList.add(myString);
        myPolymorphicList.add(myInt);
        myPolymorphicList.add(myList);


        System.out.println(myPolymorphicList);

    }




}


