import java.util.*;
import java.io.*;

public class REsearch {
    public static String specialChars = "?|*+[]().\\";
    public static void main(String args[]) {  
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   // Buffered reader for reading input
        ArrayList<FSMstate> fsmStateList = new ArrayList<FSMstate>();   // Stores the states that represent the regex
        ArrayList<String> stringArr = new ArrayList<String>();  // Stores strings to be compared with the regex
        String input = "";  // Temporarily stores input from reading
        
        // try catch that reads the text file from the command line and stores it
        try {   
            File file = new File(args[0]);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                input = sc.nextLine();
                stringArr.add(input += "\0");
            }
            sc.close();
        }
        catch(Exception e) {
            System.out.println("Error, file does not exist.");
            return;
        }

        // loop that reads the output from the compiler and stores the states
        while(input != null) {  
            try {
                input = br.readLine();
                String[] inputArr = input.split(",");
                if(inputArr.length == 3) {
                    fsmStateList.add(new FSMstate(inputArr[0].charAt(0), Integer.parseInt(inputArr[1]), Integer.parseInt(inputArr[2])));
                }
            }
            catch(Exception e) {}
        }

        // for loop that prints out the values in stringArr that match the regex
        for(int i = 0; i < stringArr.size(); i++) {
            if(checkLine(fsmStateList, stringArr.get(i))) System.out.println(stringArr.get(i));
        }
    }  

    /**
     * Checks if a string matches with the regex
     * @param fsmsl the list of states that represent the regex
     * @param s string to be checked
     * @return
     */
    public static boolean checkLine(ArrayList<FSMstate> fsmsl, String s) {
        Deque deque = new Deque(fsmsl.get(0));  // deque that holds every possible current and possible next states
        char[] chArr = s.toCharArray(); // sets the string to check to a char array
        int mark = 0; // used to know where to start the search in the char array
        int pointer = 0; // used to move through the char array after the mark
        boolean incrementPointer = false; // flag used to know whether or not the pointer can be incremented

        while(mark < chArr.length) { // algorithm for trying to find a match, mark reaches end of char array = no match
            incrementPointer = false;
            try {
                while(true) {   // "infinite loop", terminating statement will always be reached
                    int stateNum = deque.popCurr();
                    FSMstate currState = fsmsl.get(stateNum);
                    
                    // checks if the state popped off the deque has a type that matches with the current sub-character or if it's an operation
                    if(currState.getChar() == chArr[mark+pointer] || currState.getChar() == '.' || currState.getChar() == '|' || currState.getChar() == '\\') {
                        // checks if the state popped off the deque is the final state, if so return true
                        if(currState.getNext1() == 0 && currState.getNext2() == 0) {
                            return true;
                        }

                        // checks if the state type is a branching state
                        if((currState.getChar() == '|' && fsmsl.get(stateNum-1).getChar() != '\\') ||
                        currState.getChar() == '|' && fsmsl.get(stateNum - 1).getChar() == '\\' && fsmsl.get(stateNum - 2).getChar() == '\\') {
                            // insert the next value(s) of the state into the stack of the deque
                            deque.insertCurr(currState.getNext1());
                            if(!currState.isNextDupe()) deque.insertCurr(currState.getNext2());
                        }
                        // else check if the current state type is an escape character
                        else if(currState.getChar() == '\\' && fsmsl.get(stateNum - 1).getChar() != '\\') {
                            // insert the next value(s) of the queue into the stack of the deque and 
                            deque.insertCurr(currState.getNext1());
                            if(!currState.isNextDupe()) deque.insertCurr(currState.getNext2());
                        }
                        /**
                         * else check if the type of the current state matches the current subcharacter, or if state type is '.'
                         * if the state type is '.', it will also check if it is a literal and see if the current sub-character matches
                         */
                        else if((currState.getChar() == '.' && fsmsl.get(stateNum - 1).getChar() == '\\' && currState.getChar() == chArr[mark+pointer]) ||
                        (currState.getChar() == chArr[mark+pointer] || (currState.getChar() == '.' && fsmsl.get(stateNum - 1).getChar() != '\\'))) {
                            // set the increment pointer flag to true so that the pointer can carry on
                            incrementPointer = true; 
                            // insert the next value(s) of the queue into the stack of the deque and 
                            deque.insertNext(currState.getNext1());
                            if(!currState.isNextDupe()) deque.insertNext(currState.getNext2());
                        }
                    }
                    
                    // checks if all the possible current states have been exhausted
                    if(deque.isPossCurrEmpty()) {
                        // checks if the deque swap wasn't successful (fails if there is no possible next values)
                        if(!deque.swap()) { 
                            // resets and stops the search and increments the mark for the next search
                            mark++;
                            pointer = 0;
                            deque = new Deque(fsmsl.get(0));
                            break;
                        }
                        // carries on with the search if increment pointer flag is true
                        else if (incrementPointer) {
                            incrementPointer = false;
                            pointer++;
                        }
                    }
                }
            }
            catch(Exception e) { return false; }
        }
        return false;
    }
}

class Deque {       
    private Stack<Integer> possCurr = new Stack<Integer>();
    private Queue<Integer> possNext = new LinkedList<Integer>();

    public Deque(FSMstate fsms) {
        possCurr.add(fsms.getNext2());
        if(fsms.getNext1() != fsms.getNext2()) possCurr.add(fsms.getNext1());
    }
    
    /**
     * Pops the top value off the stack and returns the value
     * @return
     */
    public int popCurr() { 
        try { return possCurr.pop(); }
        catch(Exception e) { return -1; }
    }

    /**
     * Method that swaps the values in the queue into the stack,
     * given that the stack is empty and the queue still has values.
     * @return
     */
    public boolean swap() {
        if(possNext.isEmpty() || !possCurr.isEmpty()) return false; // returns false if there's nothing to swap
        while(!possNext.isEmpty()) {    // pushes all the values of the Queue onto the Stack
            possCurr.add(possNext.remove());
        }
        return true;
    }

    public boolean isPossCurrEmpty() { return possCurr.isEmpty(); } // returns if the stack is empty or not
    public void insertNext(int i) { possNext.add(i); }  // inserts a new value into the queue
    public void insertCurr(int i) { possCurr.add(i); }  // inserts a new value into the queue
}

class FSMstate {
    private char c;
    private int next1;
    private int next2;

    public FSMstate(char c, int next1, int next2) {
        this.c = c;
        this.next1 = next1;
        this.next2 = next2;
    }

    // get and set methods
    public char getChar() { return this.c; }
    public int getNext1(){ return this.next1; }
    public int getNext2() { return this.next2; }

    public boolean isNextDupe() { return this.next1 == this.next2; } // returns if the next states are the same or not
}