package com.company;
import java.io.IOException;
import java.util.ArrayList;

public class REcompile {
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
    public static int startState = 1;
    static state newState;
    static int endStateExpression = 0;
    static int dummyStartInt = 0;
    static int dummyEndInt = 0;
    static String illegalNext;
    public static void main(String[] args) throws Exception {
        FiniteStateMachine = new ArrayList<>();
        String nonLiteralString = "*?+|()[]";
        illegalNext = "*?+";
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
            return FiniteStateMachine.get(startState);
        }
        if (s.charAt(globalInt) == '[') {
            int squareMarker = globalInt;
            input = input.substring(0, squareMarker) + '(' + input.substring(squareMarker + 1);
            s = s.substring(0, squareMarker) + '(' + s.substring(squareMarker + 1);
            while (squareMarker < s.length()) {

                squareMarker++;
                StringBuffer stringBuffer = new StringBuffer(input);
                // insert() method where position of character to be
                // inserted is specified as in arguments
                stringBuffer.insert(squareMarker, '\\');
                input = stringBuffer.toString();
                s = stringBuffer.toString();
                squareMarker++;
                squareMarker++;

                if (s.charAt(squareMarker) == ']') {
                    break;
                }
                s = stringBuffer.toString();
                stringBuffer.insert(squareMarker, '|');
                input = stringBuffer.toString();
            }

            input = input.substring(0, squareMarker) + ')' + input.substring(squareMarker + 1);
            s = s.substring(0, squareMarker) + ')' + s.substring(squareMarker + 1);
        }
        if (s.charAt(globalInt) == '|') {
            //gets the previous state. basically the state index -1 for the most part
            state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-1);
            //set a default orState
            state orState = new state(stateInt, '|', startState, stateInt + 1);
            System.out.print(orState.stateIndex() + ", ");
            if(dummyStartInt<=startState)
            {
                orState.nextPhraseIndex(startState+1);
            }
            FiniteStateMachine.add(orState);
            stateInt++;
            //if there is an or state before, point to there
            bracketList.add(orState.stateIndex());
            globalInt++;
            startState = orState.stateIndex();
            state currState = expression(s);
            //orState.nextPhrase2Index(fsm.stateIndex());
            //orState.nextPhrase2Index(stateInt+1);
            state newState = new state(stateInt, '|', stateInt + 1, stateInt + 1);
            //take note of the final state of previous state
            //what if theres end of disjunction?
            //build a branching machine
            //then get the final state of previous state
            FiniteStateMachine.add(newState);
            if (previousState.nextPhraseIndex() == previousState.nextPhrase2Index()) {
                previousState.nextPhrase2Index(newState.stateIndex());
            }
            previousState.nextPhraseIndex(newState.stateIndex());
            stateInt++;
            return fsm;
        }
        fsm = term(s);
        if (globalInt < s.length()) {
            if (s.charAt(globalInt) != '\0' && valid) {
                fsm = expression(s);
            }
        }
        return fsm;
    }
    public static state term(String s) throws Exception {
        fsm = factor(s);
        //newState = expression(s);
        //if symbol at pointer is a * or ?
        //get previous index and the next one
        //update the pointer of previous one
        //forward the pointer by one
        if (globalInt < s.length()){
            //this would return an error if statement looks like this
            //REGEX: (a*) or (a?)
            //if you have a double star or double question mark, return error
            //if globalint + 1 is greater than size
            if(globalInt > 2) {
                if (illegalNext.contains(String.valueOf(s.charAt(globalInt - 1))) && FiniteStateMachine.get(FiniteStateMachine.size()-1)._symbol()=='|') {
                    error();
                }
            }
            if (s.charAt(globalInt) == '*') {
                state stateAsteriskState = new state(stateInt, '|', stateInt + 1, fsm.stateIndex());
                state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-2);
                if (stateInt-1== dummyEndInt){
                    previousState = FiniteStateMachine.get(dummyStartInt);
                }
                FiniteStateMachine.add(stateAsteriskState);
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
                globalInt++;
                fsm = stateAsteriskState;
            } else if (s.charAt(globalInt) == '+') {
                state plusState = new state(stateInt, '|', stateInt + 1, fsm.stateIndex());
                FiniteStateMachine.add(plusState);
                stateInt++;
                //take note of the final state of previous state
                //what if theres end of disjunction?
                //build a branching machine
                //then get the final state of previous state
                FiniteStateMachine.add(newState);
                stateInt++;
                globalInt++;
                fsm = plusState;
            } else if (s.charAt(globalInt) == '?') {
                state oneOrNone = new state(stateInt, '|', stateInt + 1, stateInt+1);
                state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-2);
                if (stateInt-1== dummyEndInt) {
                    previousState = FiniteStateMachine.get(dummyStartInt);
                }
                FiniteStateMachine.add(oneOrNone);
                if (previousState.nextPhraseIndex() > previousState.nextPhrase2Index()) {
                    previousState.nextPhraseIndex(stateInt);
                }
                else {
                    previousState.nextPhrase2Index(stateInt);
                }
                //take note of the final state of previous state
                //what if theres end of disjunction?
                //build a branching machine
                //then get the final state of previous state
                stateInt++;
                globalInt++;
                fsm = oneOrNone;
            }

            //checks if the character is an or sign
            //also checks if the or sign is adjacent to each other
        }
        return fsm;
    }
    public static state factor(String s) throws Exception {
        if (globalInt < s.length()) {
            //checks if the character doesnt have special meanings
            if (isVocab(s.charAt(globalInt), s)) {
                //add the state here
                fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), stateInt + 1, stateInt + 1);
                FiniteStateMachine.add(fsm);
                globalInt++;
                stateInt++;
                if (globalInt < s.length()) {
                    if (s.charAt(globalInt - 1) == '\\') {
                        state escapeState = new state(stateInt, s.charAt(globalInt), stateInt + 1, stateInt + 1);
                        FiniteStateMachine.add(escapeState);
                        stateInt++;
                        globalInt++;
                    }
                }
            } else {
                if (s.charAt(globalInt) == '(') {
                    globalInt++;
                    state createOpenState = new state(stateInt, '|',stateInt+1,stateInt+1);
                    FiniteStateMachine.add(createOpenState);
                    stateInt++;
                    state dummyStart = createOpenState;
                    dummyStartInt = dummyStart.stateIndex();
                    int oldStartState = startState;
                    startState = dummyStartInt;
                    fsm = expression(s);
                    if (s.charAt(globalInt) == ')') {
                        globalInt++;
                        dummyEndInt = FiniteStateMachine.size()-1;
                        //nested bracket fix for *
                        dummyStartInt = dummyStart.stateIndex();
                        //checks if the startState is in the bracket or not.
                        //if its inside this bracket, do nothing since
                        //the or state still wants this
                        //otherwise, throw it out
                        startState = oldStartState;
                        //prevents start state from looping upon itself
                        if(dummyStartInt != fsm.stateIndex()) {
                            dummyStart.nextPhraseIndex(fsm.stateIndex());
                            dummyStart.nextPhrase2Index(fsm.stateIndex());
                        }
                        if(startState>dummyStartInt+1 && dummyEndInt > startState)
                        {

                        }
                        else
                        {
                            startState = oldStartState;
                        }
                        return FiniteStateMachine.get(fsm.stateIndex());
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
                //if my special characters didn't do anything to alter the start state, don't do anything to it
                if(FiniteStateMachine.get(FiniteStateMachine.get(0).nextPhrase2Index())._symbol()!='|'||FiniteStateMachine.get(FiniteStateMachine.get(0).nextPhrase2Index())._symbol()!='|') {
                    FiniteStateMachine.get(0).nextPhraseIndex(startState);
                    FiniteStateMachine.get(0).nextPhrase2Index(startState);
                }
                for (int fsmIndex = 0; fsmIndex < FiniteStateMachine.size(); fsmIndex++) {
                    System.out.println(FiniteStateMachine.get(fsmIndex)._symbol() + "," + FiniteStateMachine.get(fsmIndex).nextPhraseIndex() + "," + FiniteStateMachine.get(fsmIndex).nextPhrase2Index());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    //if the character is not a symbol
    public static boolean isVocab(char c, String s) throws Exception {
        //normal check is if there is a special character or not
        //checks if the previous character is escape character. if so, then return true
        if (globalInt < s.length()) {
            if (nonLiterals.contains(c)) {
                return false;
            } else if (globalInt > 0) {
                if (s.charAt(globalInt - 1) == '\\') {
                    return true;
                }
            }
        } else {
            if (nonLiterals.contains(c)) {
                return false;
            }
            return true;
        }
        return true;
    }

    public static void error() {
        System.err.print("Invalid expression");
        valid = false;
    }
}