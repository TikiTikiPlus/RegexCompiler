/**
 * @author: Stuart Ussher 1060184,
 */

class REcompile {
    private class State {
        int n1, n2, st;
        char ch;
        public State(char ch, int n1) {
            this.ch = ch;
            this.n1 = n1;
            n2 = n1;
            st = state++;
        }
        public void print(){ System.out.println(st+"\t"+ch+"\t"+n1+"\t"+n2); }
        public void update(int state) { if (n2 == n1){ n2=state; } n1 = state; }
    }

    char[] regex;
    State[] states;
    int i, s, state = 0;
    public REcompile(String regex){
        this.regex = ('\0'+regex.substring(1, regex.length()-1)).toCharArray();
        states= new State[this.regex.length];
    }

    public void expression(){
        term();
        if (i < regex.length && !"*?|)".contains(regex[i]+"")) { expression(); }
    }

    public void term(){
        int r = factor(), ref = state;
        if (i >= regex.length || !"*?|".contains(regex[i]+"")) { return; }
        states[state] = new State('\0', state+1);
        states[state-1].n2 = r;
        states[r-1].update(state-1);
        for (int j = 0; j < r; j++) { if(states[j].n1 == r) { states[j].update(state-1); } }
        if (regex[i] == '*') {
            states[s].n1 = state; i++;
            states[s].n2 = r < s && states[r+1].ch == '\0' ? r+1 : r;
        }
        else if (regex[i] == '?') { states[s].update(state); i++; }
        else {
            int t = s; i++;
            term();
            states[t].update(state);
        }
        while (r < ref) { if (states[r].n1 == ref) { states[r].update(ref); } r++; }
    }

    /**
     * @return first index of last expression
     */
    public int factor(){
        if ("*?|".contains(regex[i]+"")) { throw new Error("Misplaced operator"); }
        switch (regex[i]) {
            case '\\':
                s = state; states[state] = new State(regex[i+1], state+1);
                i+=2; break;
            case '(':
                int r = state; i++;
                expression();
                if (i >= regex.length || regex[i] != ')') { throw new Error("Missing ')'"); }
                i++; return r;
            case ')': break;
            // case '.': s = state; states[state] = new State('.', state+1); i++; break;
            default: s = state; states[state] = new State(regex[i], state+1); i++;
        }
        return state-1;
    }

    public State[] parse() {
        i = 0;
        expression();
        if (i != regex.length){ throw new Error("Illegal Expression"); }
        return states;
    }

    public void print() {
        System.out.println("state\tch\tn1\tn2\n---------------------------");
        for (State c: states) { if (c != null) { c.print(); } }
    }

    public static void main(String[] args) {
        if (args.length == 0 || !args[0].startsWith("\"") || !args[0].endsWith("\"")) {
            System.out.println("Usage: java REcompile \\\" <expression> \\\"");
            // args = new String[] {"\""+"a|(j*m?b(yn)*)|u|c*"+"\""};
            return;
        }
        REcompile RE = new REcompile(args[0]);
        try { RE.parse(); RE.print(); } catch (Error e) { System.err.println(e); }
    }
}