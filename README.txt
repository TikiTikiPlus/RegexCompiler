1. Grammar (E = Expression, T = Term, F = Factor, V = Vocab):
E -> T
E -> T|E
T -> F*
T -> F+
T -> F?
T -> FT
F -> V
F -> .
F -> \
F -> (E)

Square brackets get replaced with an equivalent expression in the compiler.
E.g. [AB] => (\A|\B)

2. Compiler deems two or more quantifiers in a row illegal. (e.g. ?*, +?, +*?, etc.)
   However, if there are two quanitifiers in a row but is preceded by '\', then it is valid. (e.g. \**, \*?, etc.)
   As well, quantifiers following a '|' is illegal unless the '|' is preceded by '\'.

3. How to run:
java REcompile "<regex>"|java REsearch <txt-file>