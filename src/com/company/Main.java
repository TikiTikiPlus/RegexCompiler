package com.company;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
    static ArrayList<state> FiniteStateMachine;
    static String input = "";
    //the variable that is responsible for looking at the string input
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
    public static int startState=1;
    static state newState;
    static int dummyStartInt=0;
    static int dummyEndInt=0;
    public static void main(String[] args)  throws Exception{
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

    public static state expression(String s) throws Exception {
        if (s.charAt(globalInt) == ')') {
            //returns the start of the state of brackets
            //this is also used to check where the or statement is
            return FiniteStateMachine.get(startState);
        }
        if(s.charAt(globalInt)=='[')
        {
            int squareMarker = globalInt;
            input =  input.substring(0, squareMarker) + '(' + input.substring(squareMarker + 1);
            s = s.substring(0, squareMarker) + '(' + s.substring(squareMarker + 1);
            while(squareMarker<s.length())
            {

                squareMarker++;
                StringBuffer stringBuffer = new StringBuffer(input);
                // insert() method where position of character to be
                // inserted is specified as in arguments
                stringBuffer.insert(squareMarker, '\\');
                input = stringBuffer.toString();
                s = stringBuffer.toString();
                squareMarker++;
                squareMarker++;

                if(s.charAt(squareMarker)==']')
                {
                    break;
                }
                s = stringBuffer.toString();
                stringBuffer.insert(squareMarker, '|');
                input = stringBuffer.toString();
            }
            input =  input.substring(0, squareMarker) + ')' + input.substring(squareMarker + 1);
            s = s.substring(0, squareMarker) + ')' + s.substring(squareMarker + 1);
        }
        fsm = term(s);
        globalInt++;
        if (globalInt < s.length()) {
            fsm = expression(s);
        }
        return fsm;
    }

    public static state term(String s)  throws Exception{
        fsm = factor(s);
        //newState = expression(s);
        //if symbol at pointer is a * or ?
        //get previous index and the next one
        //update the pointer of previous one
        //forward the pointer by one
        //this would return an error if statement looks like this
        //REGEX: (a*) or (a?)
        //if you have a double star or double question mark, return error
        //if globalint + 1 is greater than size
        if(globalInt<s.length()) {
            if (s.charAt(globalInt) == '*') {
                fsm = new state(stateInt, '|', stateInt + 1, stateInt - 1);
                if (fsm.stateIndex() == dummyEndInt) {
                    fsm.nextPhrase2Index(startState);
                    state changeState = FiniteStateMachine.get(startState - 1);
                    if (changeState.nextPhraseIndex() == changeState.nextPhrase2Index()) {
                        changeState.nextPhraseIndex(fsm.stateIndex());
                    }
                    changeState.nextPhrase2Index(fsm.stateIndex());
                }
                FiniteStateMachine.add(fsm);
                System.out.println(startState);
                state previousState = FiniteStateMachine.get(FiniteStateMachine.size() - 3);
                if (previousState.nextPhraseIndex() == previousState.nextPhrase2Index()) {
                    previousState.nextPhraseIndex(stateInt);
                }
                previousState.nextPhrase2Index(stateInt);
                stateInt++;
                state newState = new state(stateInt, '|', stateInt + 1, stateInt + 1);
                //take note of the final state of previous state
                //what if theres end of disjunction?
                //build a branching machine
                //then get the final state of previous state
                FiniteStateMachine.add(newState);
                stateInt++;
            } else if (s.charAt(globalInt) == '+') {
                //get next phrase
                if (s.charAt(globalInt - 1) == ')') {
                    fsm = new state(FiniteStateMachine.size(), '|', stateInt + 1, startState);
                } else {
                    fsm = new state(FiniteStateMachine.size(), '|', stateInt + 1, stateInt - 1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                    System.out.println(dummyEndInt + 1);
                }
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                //if the next phrases point to the same one, we know they are literals
                stateInt++;
            } else if (s.charAt(globalInt) == '?') {
                //get next phrase
                //get the start state of previous state
                nextPhrase1 = stateInt + 1;
                fsm = new state(FiniteStateMachine.size(), '|', stateInt + 1, stateInt + 1);
                if (s.charAt(globalInt - 1) == ')') {
                    newState = FiniteStateMachine.get(startState - 1);
                    //FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt);
                } else {
                    fsm = new state(FiniteStateMachine.size(), '|', nextPhrase1, stateInt - 1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                }
                newState.nextPhraseIndex(stateInt);
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                stateInt++;
            }
            //checks if the character is an or sign
            //also checks if the or sign is adjacent to each other
            else if (s.charAt(globalInt) == '|') {
                //gets the previous state. basically the state index -1 for the most part
                state previousState = FiniteStateMachine.get(stateInt - 1);
                //set a default orState
                System.out.println(startState);
                state orState = new state(stateInt, '|', startState, stateInt + 1);
                System.out.print(orState.stateIndex() + ", ");
                FiniteStateMachine.add(orState);
//                startState = orState.stateIndex();
                stateInt++;
                //if there is an or state before, point to there
                if (bracketList.size() > 0) {
                    startState = bracketList.get(bracketList.size() - 1);
                }
                bracketList.add(orState.stateIndex());
                globalInt++;
                state currState = expression(s);
                state newState = new state(stateInt, '|', stateInt + 1, stateInt + 1);
                //take note of the final state of previous state
                //what if theres end of disjunction?
                //build a branching machine
                //then get the final state of previous state
                stateInt++;
                if (previousState.nextPhraseIndex() == previousState.nextPhrase2Index()) {
                    previousState.nextPhrase2Index(newState.stateIndex());
                }
                previousState.nextPhraseIndex(newState.stateIndex());
                FiniteStateMachine.add(newState);
                fsm = currState;
            }
        }
        return fsm;
    }
    public static state factor(String s) throws Exception {
        //checks if the character doesnt have special meanings
        if(globalInt<s.length()) {
            if (isVocab(s.charAt(globalInt), s)) {
                //add the state here
                fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), stateInt + 1, stateInt + 1);
                FiniteStateMachine.add(fsm);
                globalInt++;
                stateInt++;
                if (globalInt < s.length()) {
                    if (s.charAt(globalInt) == '\\') {
                        state escapeState = new state(stateInt, s.charAt(globalInt), stateInt + 1, stateInt + 1);
                        FiniteStateMachine.add(escapeState);
                        stateInt++;
                        globalInt++;
                    }
                }

            } else if (s.charAt(globalInt) == '(') {
                globalInt++;
                int localStart = stateInt;
                state dummyStart = FiniteStateMachine.get(stateInt);
                dummyStartInt = dummyStart.stateIndex();
                startState = dummyStartInt;
                int bracketListCount = bracketList.size();
                int oldStartState = dummyStart.stateIndex();
                fsm = expression(s);
                if (s.charAt(globalInt) == ')') {
                    globalInt++;
                    dummyStartInt = dummyStart.stateIndex();
                    startState = localStart;
                    state dummyEnd = FiniteStateMachine.get(FiniteStateMachine.size() - 1);
                    dummyEndInt = dummyEnd.stateIndex() + 1;
                    fsm.nextPhrase2Index(startState);
                    //this is to basically check where the or states should end
                        if (bracketList.size()>0&& bracketListCount<bracketList.size()) {
                            startState = bracketList.get(bracketList.size()-1);
                            bracketList.remove(bracketList.size()-1);
                        }
                    state thisState = FiniteStateMachine.get(dummyStart.stateIndex());
                    System.out.println(dummyStart);
                    //stop or then bracket interactions
                    if(startState!= thisState.stateIndex()) {
                        if (thisState.nextPhraseIndex() == thisState.nextPhrase2Index()) {
                            thisState.nextPhraseIndex(startState);
                        }
                        thisState.nextPhrase2Index(startState);
                    }
                    if(dummyStartInt<startState) {
                        startState = oldStartState;
                    }
                    //for each states that point to the dummy start state,
                    return FiniteStateMachine.get(oldStartState);
                } else {
                    error();
                }
            }
        }
        //return whatever the fsm is
        return fsm;
    }
    public static void parse(String s) throws Exception {
        try {
            fsm = new state(stateInt, '|', 1, 1);
            FiniteStateMachine.add(fsm);
            stateInt++;
            startState = stateInt;
            fsm = expression(s);
            if (globalInt < s.length()) {
                if (s.charAt(globalInt) != '\0') {
                    error();
                }
            }
            FiniteStateMachine.add(new state(FiniteStateMachine.size(), '|', 0, 0));
            if (valid) {
                //checks the first occurence of a start state
                FiniteStateMachine.get(0).nextPhraseIndex(fsm.stateIndex());
                FiniteStateMachine.get(0).nextPhrase2Index(fsm.stateIndex());
                for (int fsmIndex = 0; fsmIndex < FiniteStateMachine.size(); fsmIndex++) {
                    System.out.println(FiniteStateMachine.get(fsmIndex).stateIndex() +","+FiniteStateMachine.get(fsmIndex)._symbol() + "," + FiniteStateMachine.get(fsmIndex).nextPhraseIndex() + "," + FiniteStateMachine.get(fsmIndex).nextPhrase2Index());
                }
            }
        }
        catch(Exception e)
        {
            System.err.println(e);
        }
    }
    //if the character is not a symbol
    public static boolean isVocab(char c, String s) throws Exception {
        //normal check is if there is a special character or not
        //checks if the previous character is escape character. if so, then return true
        if(globalInt<s.length()) {
            if (nonLiterals.contains(c)) {
                return false;
            }
        }
        return true;
    }

    public static void error() {
        System.err.print("Invalid expression");
        valid = false;
    }
}
