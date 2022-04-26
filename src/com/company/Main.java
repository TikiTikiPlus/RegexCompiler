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

    public static state expression(String s) throws Exception {
        if (s.charAt(globalInt) == ')') {
            //returns the start of the state of brackets
            //this is also used to check where the or statement is
            return fsm;
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
        if (globalInt < s.length()) {
            if (s.charAt(globalInt) != '\0' && valid) {
                fsm = expression(s);
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
            if(s.charAt(globalInt)=='*')
            {
                fsm = new state(stateInt, '|', stateInt+1, stateInt-1);
                FiniteStateMachine.add(fsm);
                state changeState = FiniteStateMachine.get(fsm.nextPhrase2Index()-1);
                if(fsm.stateIndex()-1 == dummyEndInt) {
                    if(bracketList.size()>0)
                    {
                        fsm.nextPhrase2Index(startState);
                        bracketList.remove(bracketList.size()-1);
                    }
                    for (state x : FiniteStateMachine) {
                        if (x.nextPhrase2Index() == fsm.stateIndex()) {
                            if (x.nextPhraseIndex() == x.nextPhrase2Index()) {
                                x.nextPhrase2Index(fsm.stateIndex());
                                x.nextPhraseIndex(fsm.stateIndex());
                            }
                        }
                        //this is for or statements
                        if (x.stateIndex() == dummyStartInt - 1 && startState != x.stateIndex()) {
                            x.nextPhrase2Index(fsm.stateIndex());
                        }
                        if (x.stateIndex() == dummyStartInt - 1 && startState != x.stateIndex()) {
                            x.nextPhraseIndex(fsm.stateIndex());
                        }
                    }
                }
                for(state x: FiniteStateMachine) {
                    if(x.nextPhraseIndex()==fsm.nextPhrase2Index()) {
                        if (x.nextPhraseIndex() == x.nextPhrase2Index()) {
                            x.nextPhraseIndex(stateInt);
                        }
                        x.nextPhrase2Index(stateInt);
                    }
                }
                globalInt++;
                stateInt++;
            }
            else if (s.charAt(globalInt) == '+') {
                //get next phrase
                if(s.charAt(globalInt-1)==')') {
                    fsm = new state(FiniteStateMachine.size(), '|', stateInt+1, startState);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(),'|', stateInt+1, stateInt-1);
                    newState = FiniteStateMachine.get(stateInt - 2);
                    System.out.println(dummyEndInt+1);
                }
                FiniteStateMachine.add(fsm);
                //updates the state * is pointing to
                //if the next phrases point to the same one, we know they are literals
                stateInt++;
                globalInt++;
            }
            else if (s.charAt(globalInt) == '?') {
                //get next phrase
                //get the start state of previous state
                nextPhrase1 = stateInt + 1;
                fsm = new state(FiniteStateMachine.size(), '|',stateInt+1, stateInt+1);
                if(s.charAt(globalInt-1)==')') {
                    newState = FiniteStateMachine.get(startState-1);
                    //FiniteStateMachine.get(dummyStartInt).nextPhraseIndex(stateInt);
                }
                else
                {
                    fsm = new state(FiniteStateMachine.size(), '|', nextPhrase1, stateInt-1);
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
                state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-1);
                state orState = new state(stateInt,'|',startState,stateInt+1);
                if(dummyStartInt>dummyEndInt)
                {
                    orState.nextPhraseIndex(dummyStartInt);
                }
//                if(bracketList.size()>0 && startState!=stateInt)
//                {
//                    if(startState > dummyStartInt) {
//                        orState.nextPhraseIndex(startState);
//                    }
//                    else
//                    {
//                        orState.nextPhraseIndex(dummyStartInt+1);
//                    }
//                }
                FiniteStateMachine.add(orState);
                bracketList.add(orState.stateIndex());
                startState=stateInt;
                globalInt++;
                stateInt++;
                expression(s);
                state newState = new state(stateInt,'|',stateInt+1,stateInt+1);
                if(dummyStartInt==orState.stateIndex()+1)
                {
                    orState.nextPhrase2Index(startState);

                }
                //if the thing is in brackets
//                if(startState != orState.stateIndex()) {
//                    orState.nextPhrase2Index(startState);
//                }
                //take note of the final state of previous state
                //what if theres end of disjunction?
                //build a branching machine
                //then get the final state of previous state
                stateInt++;
                if(previousState.nextPhraseIndex()==previousState.nextPhrase2Index()) {
                    previousState.nextPhraseIndex(newState.stateIndex());
                }
                previousState.nextPhrase2Index(newState.stateIndex());
                FiniteStateMachine.add(newState);
                return fsm = orState;

            }
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
                if(globalInt<s.length()) {
                    if (s.charAt(globalInt-1) == '\\') {
                        state escapeState = new state(stateInt, s.charAt(globalInt), stateInt + 1, stateInt + 1);
                        FiniteStateMachine.add(escapeState);
                        stateInt++;
                        globalInt++;
                    }
                }

            }
            else {
                if (s.charAt(globalInt) == '(') {
                    globalInt++;
                    if(FiniteStateMachine.size()>1&& bracketList.size()>0) {
                        startState = bracketList.get(bracketList.size() - 1);
                    }
                    state dummyStart = FiniteStateMachine.get(stateInt-1);
                    dummyStartInt = dummyStart.stateIndex()+1;
                    int bracketListCount = bracketList.size();
                    fsm = expression(s);
                    if (s.charAt(globalInt) == ')') {
                        globalInt++;
                        if (bracketListCount<bracketList.size())
                        {
                            bracketList.remove(bracketList.size()-1);
                        }
//                        if(FiniteStateMachine.size()>1&&bracketList.size()>0) {
//                            bracketList.remove(bracketList.size()-1);
//                        }
                        dummyStartInt = dummyStart.stateIndex()+1;
                        state dummyEnd = FiniteStateMachine.get(FiniteStateMachine.size()-1);
                        dummyEndInt = dummyEnd.stateIndex();
                        //for each states that point to the dummy start state,
                        //change that start states of these states
                        for(state x: FiniteStateMachine)
                        {
                            if(x.nextPhrase2Index()==dummyStartInt && x.nextPhraseIndex()==dummyStartInt)
                            {
                                if(x.nextPhraseIndex()==x.nextPhrase2Index()&& x.nextPhraseIndex() <= startState)
                                {
                                    x.nextPhrase2Index(startState);
                                    x.nextPhraseIndex(startState);
                                }
                            }
                        }
//                        {
//                            state x = FiniteStateMachine.get(i);
//
//                            //this is for or statements
//                            else if(x.nextPhrase2Index()==dummyStartInt && x.nextPhrase2Index()>x.nextPhraseIndex()&&startState!=x.stateIndex())
//                            {
//                                x.nextPhrase2Index(startState);
//                            }
//                            else if(x.nextPhraseIndex()==dummyStartInt&&x.nextPhraseIndex()>x.nextPhrase2Index()&&startState!=x.stateIndex())
//                            {
//                                x.nextPhraseIndex(startState);
//                            }
//                        }
                    }
                    else {
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
                //checks the first occurence of a start state
                fsm = FiniteStateMachine.get(0);
                fsm.nextPhraseIndex(startState);
                fsm.nextPhrase2Index(startState);
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

        if(EndState.nextPhrase2Index()<FiniteStateMachine.size()&& EndState.nextPhraseIndex()< FiniteStateMachine.size()) {
            state endState;
            if(EndState.nextPhrase2Index()> EndState.nextPhraseIndex())
            {
                endState = FiniteStateMachine.get(EndState.nextPhrase2Index());
            }
            else {
                endState = FiniteStateMachine.get(EndState.nextPhraseIndex());
            }
            return getEndStatement(endState);
        }
        if(EndState.nextPhraseIndex()> EndState.nextPhrase2Index())
        {
            return EndState.nextPhraseIndex();
        }
        return EndState.nextPhrase2Index();
    }
}
