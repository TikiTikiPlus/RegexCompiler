import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.w3c.dom.Node;

public class REcompile {
    ArrayList<Character> nodeArray;
    String input = "";
    int globalInt = 0;
    ArrayList<Character> nonLiterals = new ArrayList<Character>();
    public void main(String[] args)
    {
    nodeArray = new ArrayList<Character>();
    String nonLiteralString = ".*+?|()\\[]";
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
            globalInt++;
        }
        else if(s.charAt(globalInt) == '|')
        {
            globalInt++;
            factor(s);
        }
    }
    public void factor(String s)
    {
        if(isVocab(s.charAt(globalInt)))
        {
            globalInt++;
        }
        else
        {
            if(s.charAt(globalInt)=='('){
            globalInt++;
            expression(s);
            if(s.charAt(globalInt)==')') 
              globalInt++;
              else error();
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
        System.err.print("Wrong compiler");
    }
}
