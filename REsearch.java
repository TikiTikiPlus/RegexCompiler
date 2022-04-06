import java.util.*;
import java.io.*;

public class REsearch {
    public static void main(String args[]) {  
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   // Buffered reader for reading input
        ArrayList<FSMstate> fsmStateList = new ArrayList<FSMstate>();
        Deque deque = new Deque();

        String input = "";

        while(input != null) {  // loop that reads the ouput from the compiler and stores it
            try {
                input = br.readLine();
                String[] inputArr = input.split(",");
                if(inputArr.length == 3) {
                    fsmStateList.add(new FSMstate(inputArr[0].charAt(0), Integer.parseInt(inputArr[1]), Integer.parseInt(inputArr[2])));
                }
            }
            catch(Exception e) {}
        }
    }  
}

class Deque {       
    private Stack<Integer> possCurr = new Stack<Integer>();
    private Queue<Integer> possNext = new LinkedList<Integer>();

    public Deque() {
        possCurr.add(1);
    }

    public int popCurr() { 
        try { return possCurr.pop(); }
        catch(Exception e) { return -1; }
    }

    public void insertNext(int i) { possNext.add(i); }

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

    public boolean matchChar(char c) { return (this.c == c); }
    public int getNext1(){ return this.next1; }
    public int getNext2() { return this.next2; }
}