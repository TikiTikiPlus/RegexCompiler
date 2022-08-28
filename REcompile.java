import java.util.ArrayList;

public class REcompile {
    static ArrayList<state> FiniteStateMachine = new ArrayList<>();
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
    public static void main(String[] args) throws Exception {;
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
        if(input.length() == 0)
        {
            error();
        }
        else {
            input = input.substring(0, 0) + '(' + input.substring(0);
            input = input.substring(0, input.length()) + ')' + input.substring(input.length());
            //fixes a bug where if there is a bracket on the very first entry, it would return an out of bounds exception error
            state firstState = new state(stateInt, '|', stateInt + 1, stateInt + 1);
            FiniteStateMachine.add(firstState);
            stateInt++;
            parse(input);
        }
    }
    //find an expression
    //output a Term first before expression
    public static state expression(String s) throws Exception {
        //to return whatever the start state that i set to be
        if (input.charAt(globalInt) == ')') {
            return FiniteStateMachine.get(startState);
        }
        //refactors square brackets
        if (input.charAt(globalInt) == '[') {
            int squareMarker = globalInt;
            input = input.substring(0, squareMarker) + '(' + input.substring(squareMarker + 1);
            while (squareMarker < input.length()) {

                squareMarker++;
                StringBuffer stringBuffer = new StringBuffer(input);
                // insert() method where position of character to be
                // inserted is specified as in arguments
                stringBuffer.insert(squareMarker, '\\');
                input = stringBuffer.toString();
                squareMarker++;
                squareMarker++;

                if (input.charAt(squareMarker) == ']') {
                    break;
                }
                stringBuffer.insert(squareMarker, '|');
                input = stringBuffer.toString();
            }
            input = input.substring(0, squareMarker) + ')' + input.substring(squareMarker + 1);
        }
        if (input.charAt(globalInt) == '|') {
            String orIllegalNext = illegalNext += '|';
            //checks to see if the previous character is escaped or not
            if(globalInt > 2) {
                if (orIllegalNext.contains(String.valueOf(input.charAt(globalInt + 1))) && (input.charAt(globalInt - 1) != '\\' || input.charAt(globalInt - 2) != '\\')) {
                    error();
                }
            }
            //gets the previous state. basically the state index -1 for the most part
            state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-1);
            //set a default orState
            state orState = new state(stateInt, '|', startState, stateInt + 1);
            FiniteStateMachine.add(orState);
            //fixes a bug where the statement is (A|B)|(C|D)
            //would just cause an infinite recursion
            if(orState.nextPhraseIndex() == 1)
            {
                orState.nextPhraseIndex(startState+1);
            }
            int lastState = FiniteStateMachine.get(orState.nextPhraseIndex()-1).nextPhrase2Index();
            //checks if the state we are targetting
            //is gonna go into a recursive loop
            //if thats the case, we add + 1 to or state index
            if(lastState==orState.nextPhraseIndex())
            {
                if(orState.nextPhraseIndex()==dummyStartInt)
                {
                    orState.nextPhraseIndex(startState+1);
                }
            }
            //set the start state to current or state
            startState = orState.stateIndex();
            stateInt++;
            //if there is an or state before, point to there
            bracketList.add(orState.stateIndex());
            globalInt++;
            state currState = expression(s);
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
        fsm = term(input);
        if (globalInt < input.length()) {
            if (input.charAt(globalInt) != '\0' && valid) {
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
        if (globalInt < input.length()){
            //this would return an error if statement looks like this
            //REGEX: (a*) or (a?)
            //if you have a double star or double question mark, return error
            //if globalint + 1 is greater than size
            if(globalInt > 2) {
                if (illegalNext.contains(String.valueOf(input.charAt(globalInt - 1))) && FiniteStateMachine.get(FiniteStateMachine.size()-1)._symbol()=='|') {
                    if(FiniteStateMachine.size() > 2)
                    {
                        if(FiniteStateMachine.get(FiniteStateMachine.size()-2)._symbol() == '\\')
                        {

                        }
                        else
                        {
                            error();
                        }
                    }

                }
            }
            if (input.charAt(globalInt) == '*') {
                state stateAsteriskState = new state(stateInt, '|', stateInt + 1, fsm.stateIndex());
                state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-2);
                //checks if previous state is the start of an open bracket
                //if so, set a dummy state in front of it and this one
                //points to the asterisk
                if(previousState.stateIndex() == dummyStartInt)
                {
                    state openBracketInteraction = new state(stateInt, '|', stateInt + 1, stateInt+1);
                    FiniteStateMachine.add(FiniteStateMachine.size()-1,openBracketInteraction);
                    stateInt++;
                    stateAsteriskState = new state(stateInt, '|', stateInt + 1, fsm.stateIndex()+1);
                    FiniteStateMachine.get(FiniteStateMachine.size()-1).nextPhraseIndex(stateAsteriskState.stateIndex());
                    FiniteStateMachine.get(FiniteStateMachine.size()-1).nextPhrase2Index(stateAsteriskState.stateIndex());
                    previousState = openBracketInteraction;
                }
                //checks if a recursive loop is happening
                if (stateInt-1== dummyEndInt){
                    previousState = FiniteStateMachine.get(dummyStartInt);
                    if(dummyStartInt == stateAsteriskState.nextPhrase2Index())
                    {
                        stateAsteriskState.nextPhrase2Index(dummyStartInt+1);
                    }
                }
                FiniteStateMachine.add(stateAsteriskState);
                //set the previous state to point to this
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
            } else if (input.charAt(globalInt) == '+') {
                state plusState = new state(stateInt, '|', stateInt + 1, fsm.stateIndex());
                FiniteStateMachine.add(plusState);
                //take note of the final state of previous state
                //what if theres end of disjunction?
                //build a branching machine
                //then get the final state of previous state
                stateInt++;
                globalInt++;
                fsm = plusState;
            } else if (input.charAt(globalInt) == '?') {
                state oneOrNone = new state(stateInt, '|', stateInt + 1, stateInt+1);
                state previousState = FiniteStateMachine.get(FiniteStateMachine.size()-2);
                //checks if previous state is a dummy state
                //that was caused by bracket states
                //then create a dummy state after it to point to both
                //the none or one state
                if(previousState.stateIndex() == dummyStartInt)
                {
                    state openBracketInteraction = new state(stateInt, '|', stateInt, stateInt);
                    FiniteStateMachine.add(FiniteStateMachine.size()-1,openBracketInteraction);
                    stateInt++;
                    oneOrNone = new state(stateInt, '|', stateInt + 1, stateInt+1);
                    FiniteStateMachine.get(FiniteStateMachine.size()-1).nextPhraseIndex(oneOrNone.stateIndex());
                    FiniteStateMachine.get(FiniteStateMachine.size()-1).nextPhrase2Index(oneOrNone.stateIndex());
                    previousState = openBracketInteraction;
                }
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
        if (globalInt < input.length()) {
            //checks if the character doesnt have special meanings
            if (isVocab(input.charAt(globalInt), s)) {
                //add the state here
                fsm = new state(FiniteStateMachine.size(), input.charAt(globalInt), stateInt + 1, stateInt + 1);
                FiniteStateMachine.add(fsm);
                globalInt++;
                stateInt++;
                //check if the character is an escape character
                //if so, create 2 states. One being the escape character
                //and the other, the escaped character
                if (globalInt < input.length()) {
                    if (input.charAt(globalInt - 1) == '\\') {
                        state escapeState = new state(stateInt, input.charAt(globalInt), stateInt + 1, stateInt + 1);
                        FiniteStateMachine.add(escapeState);
                        stateInt++;
                        globalInt++;
                        return escapeState;
                    }
                }
            } else {
                if (input.charAt(globalInt) == '(') {
                    globalInt++;
                    state createOpenState = new state(stateInt, '|',stateInt+1,stateInt+1);
                    FiniteStateMachine.add(createOpenState);
                    stateInt++;
                    state dummyStart = createOpenState;
                    dummyStartInt = dummyStart.stateIndex();
                    int oldStartState = startState;
                    startState = dummyStartInt;
                    fsm = expression(s);
                    if (input.charAt(globalInt) == ')') {
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
                            if(fsm.nextPhraseIndex() == dummyStartInt && FiniteStateMachine.get(dummyStartInt).nextPhraseIndex() == fsm.stateIndex())
                            {
                                fsm.nextPhraseIndex(dummyStartInt+1);
                            }
                        }
                        //checks if the start state is inside current brackets or not
                        //if its not, then don't change current start state
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
            fsm = expression(s);
            //usually happens when an open bracket isn't closed
            //or no open brackets
            if (globalInt < input.length()) {
                if (input.charAt(globalInt) != '\0') {
                    error();
                }
            }
            FiniteStateMachine.add(new state(FiniteStateMachine.size(), '|', 0, 0));
            //print out the states if the things are valid
            if (valid) {
                for (int fsmIndex = 0; fsmIndex < FiniteStateMachine.size(); fsmIndex++) {
                    System.out.println(FiniteStateMachine.get(fsmIndex)._symbol() + "," + FiniteStateMachine.get(fsmIndex).nextPhraseIndex() + "," + FiniteStateMachine.get(fsmIndex).nextPhrase2Index());
                }
            }
        } catch (Exception e) {
            System.err.println(e);
            error();
        }
    }
    //if the character is not a symbol
    public static boolean isVocab(char c, String s) throws Exception {
        //normal check is if there is a special character or not
        //checks if the previous character is escape character. if so, then return true
        if (globalInt < input.length()) {
            if (nonLiterals.contains(c)) {
                return false;
            } else if (globalInt > 0) {
                if (input.charAt(globalInt - 1) == '\\') {
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
        valid = false;
        System.err.println("Invalid Expression");
    }
}
class state {
    private char _symbol;
    private int _nextPhrase1;
    private int _nextPhrase2;
    private int _stateIndex;
    public state(int index,char symbol, int nextPhraseIndex, int nextPhrase2Index)
    {
        _stateIndex = index;
        _symbol = symbol;
        _nextPhrase1 = nextPhraseIndex;
        _nextPhrase2 = nextPhrase2Index;
    }
    public char _symbol()
    {
        return this._symbol;
    }
    public int nextPhraseIndex()
    {
        return this._nextPhrase1;
    }
    public int nextPhraseIndex(int nextPhrase)
    {
        this._nextPhrase1 = nextPhrase;
        return this._nextPhrase1;
    }
    public int nextPhrase2Index()
    {
        return this._nextPhrase2;
    }
    public int nextPhrase2Index(int nextPhrase2)
    {
        this._nextPhrase2 = nextPhrase2;
        return this.nextPhrase2Index();
    }
    public int stateIndex(){return this._stateIndex;}
}
