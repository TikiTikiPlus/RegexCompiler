import java.util.*;

import java.io.*;

public class REsearch {
    public static void main(String args[]) {  
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));   // Buffered reader for reading input
        ArrayList<FSMstate> fsmStateList = new ArrayList<FSMstate>();
        ArrayList<String> stringArr = new ArrayList<String>();
        String input = "";

        try {   // try catch that reads the text file from the command line
            File file = new File(args[0]);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                stringArr.add(sc.nextLine());
            }
            sc.close();
        }
        catch(Exception e) {
            System.out.println("Error, file does not exist.");
            return;
        }

        fsmStateList.add(new FSMstate('\0', -1, -1));


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

        //for(int i = 0; i < fsmStateList.size(); i++) {
            //fsmStateList.get(i).printState();
        //}
        for(int i = 0; i < stringArr.size(); i++) {
            if(checkLine(fsmStateList, stringArr.get(i))) System.out.println(stringArr.get(i));
        }
    }  

    public static boolean checkLine(ArrayList<FSMstate> fsmsl, String s) {
        Deque deque = new Deque(fsmsl.get(1));
        char[] chArr = s.toCharArray();
        int mark = 0;
        int pointer = 0;
        //while(mark < chArr.length) {
            try {
                while(true) {
                    deque.printStack();
                    int stateNum = deque.popCurr();
                    FSMstate currState = fsmsl.get(stateNum);
                    
                    if(currState.getChar() == chArr[mark+pointer] || currState.getChar() == '.') {
                        if(currState.getNext1() == 0 && currState.getNext2() == 0) return true;
                        pointer++;
                        if(!currState.getVisited()) deque.insertNext(currState.getNext1());
                        if(!currState.isNextDupe() && !currState.getVisited()) deque.insertNext(currState.getNext2());
                    }
                    else if (currState.getChar() == '+') {
                        if(currState.getNext1() == 0 && currState.getNext2() == 0) return true;
                        deque.insertNext(currState.getNext1());
                        if(!currState.isNextDupe() && !currState.getVisited()) deque.insertNext(currState.getNext2());
                    }
                    
                    if(deque.isPossCurrEmpty() && deque.swap()) {
                        setStatesFalse(fsmsl);
                        mark++;
                        pointer = 0;
                        deque = new Deque(fsmsl.get(1));
                        break;
                    }
                    currState.setVisited(true);
                }
                
            }
            catch(Exception e) { System.out.println("BOOM"); return false; }
        //}
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

    public int popCurr() { 
        try { return possCurr.pop(); }
        catch(Exception e) { return -1; }
    }

    public void insertNext(int i) { possNext.add(i); }

    public boolean isPossCurrEmpty() { return possCurr.isEmpty(); }

    public void printStack() {
        String values = Arrays.toString(possCurr.toArray());
        System.out.println(values);
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
    public void printState() { System.out.println(c + ", " + next1 + ", " + next2);}
    public char getChar() { return this.c; }
    public int getNext1(){ return this.next1; }
    public int getNext2() { return this.next2; }
    public boolean getVisited() { return visited; }
    public void setVisited(boolean b) { visited = b;}
    public boolean isNextDupe() { return this.next1 == this.next2; }
}