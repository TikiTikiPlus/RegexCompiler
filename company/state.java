package com.company;

public class state {
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
    public int nextPhrase2Index()
    {
        return this._nextPhrase2;
    }
    public int stateIndex(){return this._stateIndex;}
}
