import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.w3c.dom.Node;

public class REcompile {
    ArrayList<state> FiniteStateMachine;
    String input = "";
    int globalInt = 0;
    ArrayList<Character> nonLiterals = new ArrayList<Character>();
    state fsm;
    int nextPhrase1;
    int nextPhrase2;
    public void main(String[] args)
    {
        
    FiniteStateMachine = new ArrayList<state>();
    String nonLiteralString = "?|()\\[]";
    for(int i = 0; i < nonLiteralString.length();i++)
    {
        nonLiterals.add(nonLiteralString.charAt(i));
    }
    //accept character from a string
    input = "";
    for(int argsIndex = 0; argsIndex <args.length; argsIndex++)
    {
        if(argsIndex!= args.length-1)
        {
            input += args[argsIndex] + " ";
        }
        else
        {
            input += args[argsIndex];
        }
    }
        parse(input); 
    } 
    //find an expression
    //output a Term first before expression

    public void expression(String expressionS)
    {
        term(expressionS);
        if(expressionS.charAt(globalInt)!= '\0');
        {
            globalInt++;
            expression(expressionS);
        }
    }
    public void term(String s)
    {
        factor(s);
        if(s.charAt(globalInt)=='*')
        {
            nextPhrase1 = globalInt - 1;
            nextPhrase2 = globalInt + 1;
            fsm = new state(s.charAt(globalInt), nextPhrase1,nextPhrase2);
            FiniteStateMachine.add(fsm);
            globalInt++;
        }
        else if(s.charAt(globalInt) == '|')
        {
            globalInt++;
            term(s);
        }
    }
    public void factor(String s)
    {
        if(isVocab(s.charAt(globalInt)))
        {
            int nextPhrases =  globalInt+ 1;
            fsm = new state(s.charAt(globalInt), nextPhrases, nextPhrases);
            FiniteStateMachine.add(fsm);
            globalInt++;
        }
        else
        {
            if(s.charAt(globalInt)=='('){
            globalInt++;
            expression(s);
            if(s.charAt(globalInt)==')') 
              globalInt++;

              else 
              {
                  error();
              }
            }
        }
    }
    public void parse(String s)
    {
        expression(s);
        if(s.charAt(globalInt)!='\0')
        {
           error();
        }
    }
    //if the character is not a symbol
    public boolean isVocab(char c)
    {
        if(nonLiterals.contains(c))
        {
            return false;
        }
        return true;
    }
    public void error()
    {
        System.err.print("Invalid expression");
    }
}
