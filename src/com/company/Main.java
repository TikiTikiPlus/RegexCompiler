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
    static int endStateExpression=0;
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

    public static state expression(String expressionS) throws Exception {
        if (expressionS.charAt(globalInt) == ')') {
            startState = bracketList.get(bracketList.size()-1);
            //returns the start of the state of brackets
            //this is also used to check where the or statement is
            endStateExpression = stateInt;
            return FiniteStateMachine.get(startState);
        }
        fsm = term(expressionS);
        if (globalInt < expressionS.length()) {
            if (expressionS.charAt(globalInt) != '\0' && valid) {
                fsm = expression(expressionS);
            }
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
        if (globalInt < s.length()) {
            //this would return an error if statement looks like this
            //REGEX: (a*) or (a?)
            //if you have a double star or double question mark, return error
            //if globalint + 1 is greater than size
            if (s.charAt(globalInt) == '*') {
                //get nexzt phrase
                if(s.charAt(globalInt-1)==')') {

                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), startState, stateInt + 1);
                    newState = FiniteStateMachine.get(startState - 1);
                    if (FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt) == FiniteStateMachine.get(dummyStartInt).nextPhrase2Index(stateInt))
                    {
                        FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt);
                    }
                    FiniteStateMachine.get(dummyStartInt).nextPhrase2Index(stateInt);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), stateInt+1, stateInt-1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                }
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                //if the next phrases point to the same one, we know they are literals
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
                if(s.charAt(globalInt-1)==')') {
                    //start at either the where the brackets start or exit
                    //so make startState get changed
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt),startState, nextPhrase1);
                    newState = FiniteStateMachine.get(startState);
                    if (FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt) == FiniteStateMachine.get(dummyStartInt).nextPhrase2Index(stateInt))
                    {
                        FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt);
                    }
                    FiniteStateMachine.get(dummyStartInt).nextPhrase2Index(stateInt);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), nextPhrase1, stateInt-1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                }
                FiniteStateMachine.add(fsm);
                //updates the state + is pointing to
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
                if(s.charAt(globalInt-1)==')') {
                    fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt),startState, nextPhrase1);
                    newState = FiniteStateMachine.get(startState-1);
                    FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt);
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
                //since start state is set to 1 by default, the start state will always change to this
                //you always point to the next state forward for phrase 2. If there is a special character
                //that needs to point somewhere else, that special character will be the one that is in charge
                //of it.
                state orState = new state(stateInt, '|', startState, stateInt+1);
                if(bracketList.size() > 0)
                {
                    //if you are in the or statement, it changes where the start
                    //state of this expression to the current or statement.
                    bracketList.remove(bracketList.size() - 1);
                }
                bracketList.add(orState.stateIndex());
                FiniteStateMachine.add(orState);
                //since it changes the start state of the expression, we add
                //the or state to the arraylist that handles the start states
                startState = bracketList.get(bracketList.size()-1);
                //get startState and change it
                //if where the startState points, an or character is there, then set fsm nextPhraseIndex1 to be that
                globalInt++;
                stateInt++;
                state orStatePreviousState = FiniteStateMachine.get(orState.stateIndex()-1);
                state disjunctionState = Disjunction(s);
                if(globalInt<s.length() && s.charAt(globalInt)=='(') {
                    //if the next phrase is an expression, get the state index of that is returned.
                    orState.nextPhrase2Index(disjunctionState.stateIndex());
                }
                else
                {
                    int endStatement = getEndStatement(FiniteStateMachine.get(fsm.stateIndex()));
                    if(orStatePreviousState.nextPhraseIndex()==orStatePreviousState.nextPhrase2Index()) {
                        orStatePreviousState.nextPhrase2Index(endStatement);
                    }
                    orStatePreviousState.nextPhraseIndex(endStatement);
                }
                return orState;
            }
        }
        return fsm;
    }
    public static state Disjunction(String s) throws Exception
    {
        state tempState = expression(s);
        return FiniteStateMachine.get(tempState.stateIndex());
    }
    public static state factor(String s) throws Exception {
        if (globalInt < s.length()) {
            //checks if the character doesnt have special meanings
            if (isVocab(s.charAt(globalInt), s)) {
                //add the state here
                fsm = new state(FiniteStateMachine.size(), s.charAt(globalInt), stateInt + 1, stateInt + 1);
                FiniteStateMachine.add(fsm);
                stateInt++;
                globalInt++;
                if(s.charAt(globalInt-1)=='\\')
                {
                    state escapeState = new state(FiniteStateMachine.size(), s.charAt(globalInt), stateInt + 1, stateInt + 1);
                    FiniteStateMachine.add(escapeState);
                    stateInt++;
                    globalInt++;
                }
            }
            else {
                if (s.charAt(globalInt) == '(') {
                    globalInt++;
                    addState = stateInt;
                    bracketList.add(addState);
                    startState = bracketList.get(bracketList.size()-1);
                    state dummyStart = FiniteStateMachine.get(stateInt-1);
                    dummyStartInt = dummyStart.stateIndex();
                    int currState = stateInt;
                    fsm = expression(s);
                    state newState = FiniteStateMachine.get(currState-1);
                    if (s.charAt(globalInt) == ')' && bracketList.size() > 0) {
                        globalInt++;
                        startState = bracketList.get(bracketList.size()-1);
                        newState.nextPhraseIndex(startState);
                        newState.nextPhrase2Index(startState);
                        dummyStartInt = dummyStart.stateIndex();
                        System.out.println(dummyStartInt);
                        state dummyEnd = FiniteStateMachine.get(stateInt-1);
                        dummyEndInt = dummyEnd.stateIndex();
                        System.out.println(dummyEnd.stateIndex());
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
    public static void parse(String s) throws Exception {
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
            if(bracketList.size()>0)
            {
                //change where start happens
                fsm = FiniteStateMachine.get(0);
                if(fsm.nextPhrase2Index() == fsm.nextPhraseIndex())
                {
                    fsm.nextPhraseIndex(bracketList.get(0));
                }
                fsm.nextPhrase2Index(bracketList.get(0));
            }

            for (int fsmIndex = 0; fsmIndex < FiniteStateMachine.size(); fsmIndex++) {
                System.out.println(FiniteStateMachine.get(fsmIndex)._symbol() + "," + FiniteStateMachine.get(fsmIndex).nextPhraseIndex() + "," + FiniteStateMachine.get(fsmIndex).nextPhrase2Index());
            }
        }
    }
    //if the character is not a symbol
    public static boolean isVocab(char c, String s) throws Exception {
        //normal check is if there is a special character or not
        //checks if the previous character is escape character. if so, then return true
        if(globalInt<s.length()) {
            if (nonLiterals.contains(c)) {
                return false;
            } else if (globalInt > 0) {
                if (s.charAt(globalInt-1) == '\\') {
                    return true;
                }
            }
        }
        else
        {
            if(nonLiterals.contains(c))
            {
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
    public static int getEndStatement(state EndState) throws Exception
    {

        if(EndState.nextPhrase2Index()<FiniteStateMachine.size()) {
            state endState = FiniteStateMachine.get(EndState.nextPhrase2Index());
            return getEndStatement(endState);
        }
        return EndState.nextPhrase2Index();
    }
}
