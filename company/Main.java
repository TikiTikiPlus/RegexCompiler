package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Main {
    static ArrayList<state> FiniteStateMachine;
    static String input = "";
    static int globalInt = 0;
    static int addState = 0;
    static int stateInt = 0;
    static boolean valid = true;
    static int closeBracketCount = 0;
    static ArrayList<Character> nonLiterals = new ArrayList<>();
    //used to know which bracket we are at. Consume one after one successful forward state
    static ArrayList<Integer> bracketList = new ArrayList<>();
    static state fsm;
    //nextPhrase1 is the one that looks ahead
    static int nextPhrase1;
    //nextPhrase2 is the one that looks behind IF you need to look from behind
    static int nextPhrase2;
    public static int startState;
    static state newState;
    public static void main(String[] args) {
        FiniteStateMachine = new ArrayList<>();
        String nonLiteralString = "*?+|()[]";
        for (int i = 0; i < nonLiteralString.length(); i++) {
            nonLiterals.add(nonLiteralString.charAt(i));
        }
        //accept character from a string
        input = "";
        for (int argsIndex = 0; argsIndex < args.length; argsIndex++) {
            if (argsIndex != args.length - 1) {
                input += args[argsIndex] + " ";
            } else {
                input += args[argsIndex];
            }
        }
        parse(input);
    }
    //find an expression
    //output a Term first before expression

    public static state expression(String expressionS) {

        if (expressionS.charAt(globalInt) == ')') {
//            fsm = new state(bracketList.get(bracketList.size()-1), ' ', 0,0);
            return fsm;
        }
        fsm = term(expressionS);
        //FiniteStateMachine.add(fsm);
        if (globalInt < expressionS.length()) {
            if (expressionS.charAt(globalInt) != '\0' && valid) {

                fsm = expression(expressionS);
                //globalInt++;
            }
        }
        return fsm;
    }

    public static state term(String s) {
        fsm = factor(s);
        //newState = expression(s);
        //if symbol at pointer is a * or ?
        //get previous index and the next one
        //update the pointer of previous one
        //forward the pointer by one
        if (globalInt < s.length()) {
            //this would return an error if statement looks like this
            //REGEX: (a*) or (a?)
            //if you have a double star or double question mark, return error
            //if globalint + 1 is greater than size
            if (s.charAt(globalInt) == '*') {
                //get next phrase
                nextPhrase1 = stateInt + 1;
                System.out.println(startState);
                if(s.charAt(globalInt-1)==')') {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt),startState+1, nextPhrase1);
                    newState = FiniteStateMachine.get(startState);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), nextPhrase1, stateInt-1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                }
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                if(newState.nextPhraseIndex() == newState.nextPhrase2Index())
                {
                    newState.nextPhraseIndex(fsm.stateIndex());
                }
                newState.nextPhrase2Index(fsm.stateIndex());
                stateInt++;
                globalInt++;
            }
            else if (s.charAt(globalInt) == '+') {
                //get next phrase
                nextPhrase1 = stateInt + 1;
                System.out.println(startState);
                if(s.charAt(globalInt-1)==')') {
                    //start at either the where the brackets start or exit
                    //so make startState get changed
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt),startState+1, nextPhrase1);
                    newState = FiniteStateMachine.get(startState);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), nextPhrase1, stateInt-1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                }
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                if(newState.nextPhraseIndex() == newState.nextPhrase2Index())
                {
                    newState.nextPhraseIndex(fsm.stateIndex());
                }
                newState.nextPhrase2Index(fsm.stateIndex());
                stateInt++;
                globalInt++;
            }
            else if (s.charAt(globalInt) == '?') {
                //get next phrase
                nextPhrase1 = stateInt + 1;
                System.out.println(startState);
                if(s.charAt(globalInt-1)==')') {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt),startState+1, nextPhrase1);
                    newState = FiniteStateMachine.get(startState);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), nextPhrase1, stateInt-1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                }
                newState.nextPhraseIndex(stateInt);
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                stateInt++;
                globalInt++;
            }
            //checks if the character is an or sign
            //also checks if the or sign is adjacent to each other
            else if (s.charAt(globalInt) == '|') {
                //if the or statement is in a bracket,
                //change the start state of the bracket to be the or statement

                if((s.charAt(globalInt-1)==')')||(FiniteStateMachine.get(startState)._symbol()=='|'&&bracketList.size()!=0))
                {
                    //set the nextPhrase1 of current fsm to be this
                    fsm = new state(stateInt, '|', startState, stateInt + 1);
                    //change bracketList 0? or whatever current bracketList is
                    if(bracketList.size()!=-0) {
                        bracketList.remove(bracketList.size() - 1);
                    }
                    bracketList.add(stateInt);
                    startState = bracketList.get(bracketList.size()-1);
                }
                else {
                    fsm = new state(stateInt, '|', startState + 1, stateInt + 1);
                    bracketList.remove(bracketList.size()-1);
                    bracketList.add(stateInt);
                    startState = bracketList.get(bracketList.size()-1);
                }
                FiniteStateMachine.add(fsm);
                //get startState and change it
                //if where the startState points, an or character is there, then set fsm nextPhraseIndex1 to be that
                state previousState = FiniteStateMachine.get(stateInt-1);
                globalInt++;
                stateInt++;
                while(globalInt < s.length())
                {
                    newState = term(s);
                    if(globalInt<s.length())
                    {
                        if(s.charAt(globalInt)==')')
                        {
                            break;
                        }

                    }
                }

                if (previousState.nextPhrase2Index() == previousState.nextPhraseIndex()) {
                    previousState.nextPhrase2Index(newState.nextPhrase2Index());
                }
                previousState.nextPhraseIndex(newState.nextPhrase2Index());
            }
        }
        return fsm;
    }

    public static state factor(String s) {
        if (globalInt < s.length()) {
            //checks if the character doesnt have special meanings
            if (isVocab(s.charAt(globalInt), s)) {
                //add the state here
                fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), stateInt + 1, stateInt + 1);
                FiniteStateMachine.add(fsm);
                stateInt++;
                globalInt++;
            }
            else {
                if (s.charAt(globalInt) == '(') {
                    globalInt++;
                    addState = stateInt+1;
                    bracketList.add(addState);
                    state newState = FiniteStateMachine.get(stateInt);
                    fsm = expression(s);
                    startState = bracketList.get(bracketList.size()-1);

                    if (s.charAt(globalInt) == ')' && bracketList.size() > 0) {
                        globalInt++;
                        bracketList.remove(bracketList.size() - 1);
                    } else {
                        error();
                    }
                }
            }
        }
        //return whatever the fsm is
        //term(s);
        return fsm;
    }

    public static void parse(String s) {
        fsm = new state(stateInt, '\0', 1, 1);
        FiniteStateMachine.add(fsm);
        stateInt++;
        expression(s);
        if (globalInt < s.length()) {
            if (s.charAt(globalInt) != '\0') {
                error();
            }
        }

        FiniteStateMachine.add(new state(FiniteStateMachine.size(), '\0', 0, 0));
        if (valid) {
            for (int fsmIndex = 0; fsmIndex < FiniteStateMachine.size(); fsmIndex++) {
                System.out.println("Index: " + FiniteStateMachine.get(fsmIndex).stateIndex() + ", symbol: " + FiniteStateMachine.get(fsmIndex)._symbol() + ", np1:  " + FiniteStateMachine.get(fsmIndex).nextPhraseIndex() + ", np2: " + FiniteStateMachine.get(fsmIndex).nextPhrase2Index());
            }
        }
    }

    //if the character is not a symbol
    public static boolean isVocab(char c, String s) {
        //normal check is if there is a special character or not
        //checks if the previous character is escape character. if so, then return true
        if (nonLiterals.contains(c)) {
            return false;
        } else if (globalInt > 0) {
            if (s.charAt(globalInt) == '\\') {
                return true;
            }
        }
        return true;
    }

    public static void error() {
        System.err.print("Invalid expression");
        valid = false;
    }
}
