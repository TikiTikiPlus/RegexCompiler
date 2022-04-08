package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    static ArrayList<state> FiniteStateMachine;
    static String input = "";
    static int globalInt = 0;
    static boolean valid = true;
    static ArrayList<Character> nonLiterals = new ArrayList<Character>();
    static state fsm;
    //nextPhrase1 is the one that looks ahead
    static int nextPhrase1;
    //nextPhrase2 is the one that looks behind IF you need to look from behind
    static int nextPhrase2;
    public static void main(String[] args)
    {
        FiniteStateMachine = new ArrayList<state>();
        String nonLiteralString = "?|()[]";
        for(int i = 0; i < nonLiteralString.length();i++)
        {
            nonLiterals.add(nonLiteralString.charAt(i));
        }
        //accept character from a string
        input = "";
        for(int argsIndex = 0; argsIndex <args.length; argsIndex++)
        {
            if(argsIndex!= args.length-1)
            {
                input += args[argsIndex] + " ";
            }
            else
            {
                input += args[argsIndex];
            }
        }
        parse(input);
    }
    //find an expression
    //output a Term first before expression

    public static void expression(String expressionS)
    {
        term(expressionS);
        if(globalInt < expressionS.length())
        {
            if(expressionS.charAt(globalInt)!='\0'&&valid == true) {
                expression(expressionS);
                //globalInt++;
            }
        }
        return;
    }
    public static void term(String s)
    {
        factor(s);
        //if symbol at pointer is a * or ?
        //get previous index and the next one
        //update the pointer of previous one
        //forward the pointer by one
        if(globalInt < s.length())
            //this would return an error if statement looks like this
            //REGEX: (a*) or (a?)
            //if you have a double star or double question mark, return error
        if((s.charAt(globalInt)=='*' || s.charAt(globalInt)=='?')&&(s.charAt(globalInt+1) != '*' || s.charAt(globalInt)!='?'))
        {
            //get next phrase
            nextPhrase1 = globalInt + 1;
            //get previous phrase
            nextPhrase2 = globalInt - 1;
            fsm = new state(s.charAt(globalInt), nextPhrase1,nextPhrase2);
            //add new phrase
            FiniteStateMachine.add(fsm);
            globalInt++;
        }
        //checks if the character is an or sign
        //also checks if the or sign is adjacent to each other
        else if(s.charAt(globalInt) == '|' && s.charAt(globalInt+1) != '|')
        {
//            if(s.charAt(globalInt - 2) == '|')
//            {
//
//            }
//            nextPhrase1 = globalInt -1;
//            nextPhrase2 = globalInt +1;
//            fsm = new state(s.charAt(globalInt), nextPhrase1,nextPhrase2);
//            globalInt++;
            term(s);
        }
        else
        {
            error();
        }
    }
    public static void factor(String s)
    {
        if(globalInt < s.length()) {
            //checks if the character doesnt have special meanings
            if (isVocab(s.charAt(globalInt))) {
                //add the state here
                fsm = new state(s.charAt(globalInt), globalInt + 1, globalInt + 1);
                FiniteStateMachine.add(fsm);
                //if the character is an escape state, turn next state into a literal fsm
                if(s.charAt(globalInt)=='\\')
                {
                    globalInt++;
                    fsm = new state(s.charAt(globalInt),globalInt+1, globalInt+1);
                }
                globalInt++;
            } else {
                if (s.charAt(globalInt) == '(') {
                    globalInt++;
                    expression(s);
                    if (s.charAt(globalInt) == ')')
                        globalInt++;
                    else {
                        error();
                    }
                }
            }
        }
    }
    public static void parse(String s)
    {
        expression(s);
        if(globalInt < s.length()) {
            if (s.charAt(globalInt) != '\0') {
                error();
            }
        }
        FiniteStateMachine.add(new state('\0', 0,0));
        for(int fsmIndex = 0; fsmIndex<FiniteStateMachine.size(); fsmIndex++)
        {
            System.out.println("Index: " + fsmIndex + ", symbol: " + FiniteStateMachine.get(fsmIndex)._symbol() + ", np1:  " + FiniteStateMachine.get(fsmIndex).nextPhraseIndex() + ", np2: " + FiniteStateMachine.get(fsmIndex).nextPhrase2Index());
        }
    }
    //if the character is not a symbol
    public static boolean isVocab(char c)
    {
        if(nonLiterals.contains(c))
        {
            return false;
        }
        return true;
    }
    public static void error()
    {
        System.err.print("Invalid expression");
        valid = false;
    }
}
