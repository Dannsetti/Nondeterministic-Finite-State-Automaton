# Exercise

This is the exercise that has been set as an alternative to the test.

## General idea

You probably already know what a *Nondeterministic Finite State Automaton* (NDFA or NFA) is. 
If not, [here's a nice explanation][nfa], and you can find many others on the Web.
Here's a picture taken from that site:

![example][nfa-image]

The arrow pointing to node A indicates that it is the start state. 
The double circle around node D indicates that it is a final state.

In this exercise you are required to:

+ Read in some NFA.
+ For each NFA, read in some inputs
+ For each input to an NFA, determine whether it accepts the input 
  ("accept" = finds a path from the start node to some finish node). Do this in two ways:
  	+ Using backtracking.
	+ Using sets of possible states.
+ For each input, state whether it is accepted or rejected, and if accepted, 
  print one acceptance path.

## Specific requirements

The file `NFA.scala` shows some methods you are required to provide.
For each method, the input is a string containing a sequence of characters to be input to the NFA.
For the user's convenience, input may contain spaces, which the method should ignore. 
The method attempts to find some path from the start node to a finish node and, if successful, 
returns the sequence of states that were visited along this path, including both the start and 
finish node. 

(Special case: if the start node is itself a finish node, the returned list should contain only 
that one node.) 

Note that *all* of the input must be used; it isn't enough to just pass through a final 
state, a solution requires that the NFA end up in a final state.

NFAs may contain empty transitions, where it can go from one state to another without consuming 
a character from the input. To represent an empty transition, use the Unicode character ε (small 
Greek letter epsilon). 

### Testing

Write unit tests for the above methods, and for any other I/O free methods you write.

### Input

Write a companion object `NFA` containing a `main` method. 
The object should read and process NFAs from a file named `nfa.txt`, 
process them, and print the results. 

The format of the input should consist of these parts, in order:

1. A line beginning with the word `START` (all caps) and, on the same line, the name of the 
   start state.
1. A line beginning with the word `FINAL` and, on the same line, the names of all the final states.
1. One or more lines beginning with the word `TRANSITIONS` followed by any number of 
   state character state triples, using whitespace as the only delimiter.
1. One or more lines beginning with the word `INPUT` followed by one or more characters. 
   The characters may, but need not be, separated by whitespace (any whitespace should 
   be discarded). Multiple `INPUT` lines represent multiple independent runs of the NFA.
1. A line beginning with the word `END`, and nothing else.

The above may be repeated, to run different NFAs. Points 1, 2, and 3 are used to define a new NFA. 
You may assume that the above capitalised words do not occur as the names of states.

## How to do it

Here is how backtracking works:
```
def explore(N) {
    If N is a goal node, return "success"
    If N is a leaf node, return "failure"
    For each child C of N,
        If explore(C) return "success"
    Return "failure"
}
```
In this exercise, of course, we want more than just a binary success/failure; 
we want the sequence of nodes visited when successful. 
Therefore, this approach must be augmented with additional code.

### Sets of states

Instead of trying just one path at a time, we can try all paths simultaneously. 
To do this we keep track not of the one state where we "are", but the set of all states 
where we "could be".

In the example above, with the input `011`, we would start in `{A}`, 
that is, a set containing only node `A`. 
Upon reading a `0`, we could stay in `A` or go to `B`, that is, `{A, B}`. 
If the next input is `1`, then from `A` we can stay in `A` or go to `C`; from `B` 
we cannot go anywhere, so this path **"dies"**, and the places we can be are `{A, C}`. 
Then if the next input is another `1`, from `A` we can stay in `A` or go to `C`, 
and from `C` we can go to `D`; so the places we can be are `{A, C, D}`. 
Since we have used all the input and ended in `D`, which is a final state, we can stop.

### Set of paths

Instead of trying just one path at a time, we can try all paths simultaneously. 
To do this we keep track not of the one state where we *"are"*, but the set of all states 
where we *"could be"*, and the path that got us there. Each path will be a list of states.

In the example above, with the input `011`, we would start in `{[A]}`, that is, 
a set containing a list containing `A`. 
Here, `[A]` represents a zero-length path. 
Upon reading a `0`, we could stay in `A` or go to `B`, that is, `{[A,A], [A,B]}`. 
If the next input is `1`, then from path `[A,A]` we can stay in `A` or go to `C` -- so 
the path `[A,A]` splits into `[A,A,A]` and `[A,A,C]`, while from `[A,B]` 
we cannot go anywhere, so this path *"dies"*, and the possible paths so far are 
`{[A,A,A],[A,A,C]}`.  
Then if the next input is another `1`, from `[A,A,A]` we can stay in `A` or go to `C`, 
and from `[A,A,C]` we can go to `D`; so the paths we can follow are 
`{[A,A,A,A], [A,A,A,C], [A,A,C,D]}`. Since we have used all the input and ended in `D`, 
which is a final state, we can stop.

Note that at each step, all path lengths are the same: one more than the number of inputs consumed. 
However, if the NFA contains ε transitions, paths containing these will be longer, and you have to 
be wary of cycles of ε transitions.

This approach uses a somewhat more complex data structure than Set of States, 
but the result directly contains the desired path.

Also, as a reminder, it is easiest to work with the head of a list, and this fact may 
influence your implementation of paths.

## Submission

Your repository will be cloned on the Sunday prior to the start of the Spring term. 
You should ensure you have a rich `commit` history for the repository 
(don't write all the code and then `commit` it).


## Acknowledgements

This exercise was developed from an original idea and specification by David Matuszek.

[nfa]: https://people.cs.clemson.edu/%7Egoddard/texts/theoryOfComputation/3a.pdf
[nfa-image]: fsa.png