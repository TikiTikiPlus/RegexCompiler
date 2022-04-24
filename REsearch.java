import java.util.*;
import java.io.*;

public class REsearch {
    public static String specialChars = "?|*+[]().\\";
    public static void main(String args[]) {  
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   // Buffered reader for reading input
        ArrayList<FSMstate> fsmStateList = new ArrayList<FSMstate>();   // Stores the states that represent the regex
        ArrayList<String> stringArr = new ArrayList<String>();  // Stores strings to be compared with the regex
        String input = "";  // Temporarily stores input from reading

        try {   // try catch that reads the text file from the command line
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
        
        while(input != null) {  // loop that reads the output from the compiler and stores it
            try {
                input = br.readLine();
                String[] inputArr = input.split(",");
                if(inputArr.length == 3) {
                    fsmStateList.add(new FSMstate(inputArr[0].charAt(0), Integer.parseInt(inputArr[1]), Integer.parseInt(inputArr[2])));
                }
            }
            catch(Exception e) {}
        }

        for(int i = 0; i < stringArr.size(); i++) { // for loop that prints out the values in stringArr that match the regex
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
        Deque deque = new Deque(fsmsl.get(0));
        char[] chArr = s.toCharArray();
        int mark = 0;
        int pointer = 0;
        int nextStateLiteral = 0;
        boolean incrementPointer = false;
        while(mark < chArr.length) {
            incrementPointer = false;
            try {
                while(true) {
                    int stateNum = deque.popCurr();
                    FSMstate currState = fsmsl.get(stateNum);
                    if(currState.getChar() == chArr[mark+pointer] || currState.getChar() == '.' || currState.getChar() == '|' || currState.getChar() == '\\') {
                        if(currState.getNext1() == 0 && currState.getNext2() == 0) {
                            setStatesFalse(fsmsl);
                            return true;
                        }

                        if(currState.getChar() == chArr[mark+pointer] || currState.getChar() == '.') incrementPointer = true;
                        
                        if(!currState.getVisited() && fsmsl.get(stateNum).getChar() == '\\' && nextStateLiteral == 0 && chArr[mark+pointer] == '\\') {
                            if(fsmsl.get(stateNum + 1).getChar() == '\\') {  
                                currState = fsmsl.get(stateNum + 1);
                                incrementPointer = true;
                                nextStateLiteral = 0;  
                                deque.insertNext(currState.getNext1());
                                if(!currState.isNextDupe()) deque.insertNext(currState.getNext2());  
                            }
                            else {
                                nextStateLiteral = stateNum + 1;
                                deque.insertNext(currState.getNext1());
                                if(!currState.isNextDupe()) deque.insertNext(currState.getNext2());
                            }
                        }
                        else if(!currState.getVisited() && currState.getChar() == chArr[mark+pointer] && stateNum == nextStateLiteral) {
                            incrementPointer = true;
                            nextStateLiteral = 0;  
                            deque.insertNext(currState.getNext1());
                            if(!currState.isNextDupe()) deque.insertNext(currState.getNext2());  
                        }
                        else if(!currState.getVisited() && fsmsl.get(stateNum - 1).getChar() != '\\') {
                            deque.insertNext(currState.getNext1());
                            if(!currState.isNextDupe()) deque.insertNext(currState.getNext2());
                        }
                    }
                    if(deque.isPossCurrEmpty()) {
                        if(!deque.swap()) {
                            setStatesFalse(fsmsl);
                            mark++;
                            pointer = 0;
                            deque = new Deque(fsmsl.get(0));
                            break;
                        }
                        else if (incrementPointer){
                            incrementPointer = false;
                            pointer++;
                        }
                    }
                    currState.setVisited(true);
                }
            }
            catch(Exception e) { return false; }
        }
        return false;
    }

    public static void setStatesFalse(ArrayList<FSMstate> fsmsl) {
        for(int i = 0; i < fsmsl.size(); i++) {
            fsmsl.get(i).setVisited(false);
        }
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
    public int possCurrLength() { return possCurr.size(); }
}

class FSMstate {
    private char c;
    private int next1;
    private int next2;
    private boolean visited;

    public FSMstate(char c, int next1, int next2) {
        this.c = c;
        this.next1 = next1;
        this.next2 = next2;
        visited = false;
    }

    // get and set methods
    public char getChar() { return this.c; }
    public int getNext1(){ return this.next1; }
    public int getNext2() { return this.next2; }
    public boolean getVisited() { return visited; }
    public void setVisited(boolean b) { visited = b;}

    public boolean isNextDupe() { return this.next1 == this.next2; } // returns if the next states are the same or not
}