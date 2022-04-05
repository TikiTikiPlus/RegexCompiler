public class REcompile {

    private int _index;
    private char _symbol;
    private int _nextPhrase1;
    private int _nextPhrase2;

    public FiniteState(char symbol, int nextPhrase1, int nextPhrase2)
    {
        this._symbol = symbol;
        this._nextPhrase1 = nextPhrase1;
        this._nextPhrase2 = nextPhrase2;
    }
    public int index()
    {
        return this._index;
    }
    public char symbol()
    {
        return this._symbol;
    }
    public int nextPhrase1()
    {
        return this._nextPhrase1;
    }
    public int nextPhrase2()
    {
        return this._nextPhrase2;
    }
    public void symbolInteraction()
    {
        switch(this._symbol)
        {
            case '.':
                break;
            case '*':
                break;
            case'+':
                break;
            case '?':
                break;
            case '|':
                break;
            case '(':
                break;
            case'\\':
                break;
            case '[':
                break;

        }
    }
}
