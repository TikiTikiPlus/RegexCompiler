import java.util.*;

public class REsearch {
    public static void main(String args[]) {  
        Deque deque = new Deque();
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