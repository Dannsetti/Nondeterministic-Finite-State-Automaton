package nfaClass

import org.scalatest.{BeforeAndAfter, FunSuite}
import java.nio.file.{Paths, Files}

import scala.collection.mutable.ListBuffer
import scala.language.postfixOps


class NFATest extends FunSuite with BeforeAndAfter {

  var nfa: NFA = _

  val transitionsTest = List(("A", 0.toChar, "A"),("A", 0.toChar, "B"),("B", 1.toChar, "C"), ("C", 1.toChar, "C"))
  val finals = Set("C")
  nfa = new NFA("A", transitionsTest, finals)
  val emptyTransition: Array[Char] = "ε".toCharArray



  test("Start Node read should be equal A") {
    assert(nfa.start == "A")
  }

  test("Finals Nodes should be a set and contain C") {
    assert(nfa.finals == Set("C"))
    assert(nfa.finals.contains("C"))
  }

  test("Input 0 on the backtracking function should return Failure for the TRANSITIONS A0AA0BB1CC1C with START A and FINAL C") {
    val input = "0"
    val transitionsTest1 = "A0AA0BB1CC1C"
    val transitionsList: List[(String, Char, String)] = transitionsTest1.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val nfa1 = new NFA("A", transitionsList, finals)
    assert(nfa1.solveByBacktracking(input).isEmpty)
  }

  test("Input 00 on solveByBacktracking should return Failure for TRANSITIONS A0AA0BB1CC1C with START A and FINAL C") {
    val input1 = "00"
    val transitionsTest2 = "A0AA0BB1CC1C"
    val transitionsList1: List[(String, Char, String)] = transitionsTest2.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val nfa2 = new NFA("A", transitionsList1, finals)
    assert(nfa2.solveByBacktracking(input1).isEmpty)
  }

  test("getPathsForInput method that contain input 0 for the test should return List((A,0,A),(A,0,B))") {
    val inputPathResult = nfa.getPathsForInput(transitionsTest, 0.toChar)
    assert(inputPathResult == List(("A", 0.toChar, "A"), ("A", 0.toChar, "B")))
  }

  test("getPathsForInput method that contain input 1 for the test should return List((B,1,C),(C,1,C))") {
    val inputPathResult = nfa.getPathsForInput(transitionsTest, 1.toChar)
    assert(inputPathResult == List(("B", 1.toChar, "C"), ("C", 1.toChar, "C")))
  }

  test("getPathsForInput method with Empty Transitions that contain ε should return List((C,ε,D),(D,ε,D))") {
    val newTransitions = List(("A", 0.toChar, "A"),("A", 0.toChar, "B"),("B", 1.toChar, "C"), ("C", 1.toChar, "C"), ("C", emptyTransition(0), "D"), ("D", emptyTransition(0), "D"))
    val inputPathResult = nfa.getPathsForInput(newTransitions, emptyTransition(0))
    assert(inputPathResult == List(("C", emptyTransition(0), "D"), ("D", emptyTransition(0), "D")))
  }

  test("getTransitionsForState method for Node equal B should return List((B,1,C))") {
    assert(nfa.getTransitionsForState("B") == List(("B", 1.toChar, "C")))
  }

  test("getTransitionsForState method for node equal C should return List((C,1,C),(C,ε,D))") {
    val newTransitions = List(("A", 0.toChar, "A"),("A", 0.toChar, "B"),("B", 1.toChar, "C"), ("C", 1.toChar, "C"), ("C", emptyTransition(0), "D"), ("D", emptyTransition(0), "D"))
    val nfa3 = new NFA("A",newTransitions,finals)
    assert(nfa3.getTransitionsForState("C") == List(("C", 1.toChar, "C"),("C", emptyTransition(0), "D")))
  }

  test ("solveByBacktracking method with Special case where Start Node and Finish is itself should return Option(List(Start))") {
    val input3 = "000"
    val transitionsTest3 = "A0AA0BB1CC1C"
    val finals1 = Set("A")
    val transitionsList2: List[(String, Char, String)] = transitionsTest3.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val nfa4 = new NFA("A", transitionsList2, finals1)
    assert(nfa4.solveByBacktracking(input3) == Option(List("List(A)")))
  }

  test("solveByBacktracking method with Input 001, TRANSITIONS A0AA0BB1CC1C, START A and FINAL C. Should return Success") {
    val input4 = "001"
    val transitionsTest4 = "A0AA0BB1CC1C"
    val transitionsList3: List[(String, Char, String)] = transitionsTest4.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals2 = Set("C")
    val nfa5 = NFA.apply("A", transitionsList3, finals2)
    assert(nfa5.solveByBacktracking(input4) == Option(List("List(A, A, B, C)")))
  }

  test("solveByBacktracking method with Input aabb, TRANSITIONS 1 ε 3 1 ε 2 2 ε 4 3 b 4, START 1 and FINAL 4. Should return Failure") {
    val input5 = "aabb"
    val transitionsTest5 = "1ε31ε22ε43b4"
    val transitionsList4: List[(String, Char, String)] = transitionsTest5.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals3 = Set("4")
    val nfa6 = NFA.apply("1", transitionsList4, finals3)
    assert(nfa6.solveByBacktracking(input5).isEmpty)
  }

  test("solveByBacktracking method with Input bb, TRANSITIONS 1 ε 3 1 ε 2 2 ε 4 3 b 4 4 b 4, START 1 and FINAL 4. Should return Success") {
    val input6 = "bb"
    val transitionsTest6 = "1ε31ε22ε43b44b4"
    val transitionsList5: List[(String, Char, String)] = transitionsTest6.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals4 = Set("4")
    val nfa7 = NFA.apply("1", transitionsList5, finals4)
    assert(nfa7.solveByBacktracking(input6) == Option(List("List(1, 3, 4, 4)", "List(1, 2, 4, 4, 4)")))
  }

  test("backtrackingRecursive method with Input bb, TRANSITIONS 1ε31ε22ε43b44b4, START 1 and FINAL 4. Should return Success") {
    val input6 = "bb"
    val transitionsTest6 = "1ε31ε22ε43b44b4"
    val transitionsList5: List[(String, Char, String)] = transitionsTest6.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals4 = Set("4")
    val nfa7 = NFA.apply("1", transitionsList5, finals4)
    assert(nfa7.backtrackingRecursive("1", input6, 0, ListBuffer[String]()) == List(List("1", "3", "4", "4"), List("1", "2", "4", "4", "4")))
  }

  test("backtrackingRecursive method with Input bb, TRANSITIONS 1ε31ε22ε43b44b41b1, START 1 and FINAL 1. Should return Success for special case") {
    val input7 = "bb"
    val transitionsTest7 = "1ε31ε22ε43b44b41b1"
    val transitionsList6: List[(String, Char, String)] = transitionsTest7.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals5 = Set("1")
    val nfa7 = NFA.apply("1", transitionsList6, finals5)
    assert(nfa7.backtrackingRecursive("1", input7, 0, ListBuffer[String]()) == List(List("1")))
  }

  test("backtrackingRecursive method with Input 0011, TRANSITIONS AεAAεBBεBA0CCεD, START A and FINAL D. Should return Failure") {
    val input8 = "0011"
    val transitionsTest8 = "AεAAεBBεBA0CCεD"
    val transitionsList7: List[(String, Char, String)] = transitionsTest8.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals5 = Set("D")
    val nfa8 = NFA.apply("A", transitionsList7, finals5)
    assert(nfa8.backtrackingRecursive("1", input8, 0, ListBuffer[String]()) == null)
  }


  test("(Two Finals Nodes)- solveByBacktracking method with Input 00, TRANSITIONS A0A A0B B0C C1C A0D BεD D0D, START A and FINAL D C. Should return Success") {
    val input9 = "00"
    val transitionsTest9 = "A0AA0BB0CC1CA0DBεDD0D"
    val transitionsList8: List[(String, Char, String)] = transitionsTest9.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals6 = "DC"
    var setFinals = new ListBuffer[String]
    for (finalNodes <- finals6) {
      setFinals += finalNodes.toString
    }
    val setOFFinals = setFinals.toSet

    val nfa8 = NFA.apply("A", transitionsList8, setOFFinals)

    assert(nfa8.solveByBacktracking(input9) == Option(List("List(A, A, B, D)", "List(A, A, D)", "List(A, B, C)", "List(A, B, D, D)", "List(A, D, D)")))

  }

  test("Empty Transitions after the last input is read should be accepted and return Successful if it ends in the final node") {
    val input10 = "b"
    val transitionsTest10 = "1ε31b22ε44b4"
    val transitionsList9: List[(String, Char, String)] = transitionsTest10.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals4 = Set("4")
    val nfa7 = NFA.apply("1", transitionsList9, finals4)
    assert(nfa7.solveByBacktracking(input10) == Option(List("List(1, 2, 4)")))
  }

  test("(Three Finals Nodes)- backtrackingRecursive method with Input 101, TRANSITIONS A0A A0B B0C C1C A0D BεD D0D, START B and FINAL D C B. Should return Failure") {
    val input11 = "101"
    val transitionsTest11 = "A0AA0BB0CC1CA0DBεDD0D"
    val transitionsList9: List[(String, Char, String)] = transitionsTest11.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals7 = "DCB"
    var setFinals = new ListBuffer[String]
    for (finalNodes <- finals7) {
      setFinals += finalNodes.toString
    }
    val setOFFinals = setFinals.toSet

    val nfa8 = NFA.apply("B", transitionsList9, setOFFinals)

    assert(nfa8.backtrackingRecursive("B", input11, 0, ListBuffer[String]() ) == null)

  }

  test("(Three Finals Nodes)- backtrackingRecursive method with Input 00, TRANSITIONS A0A A0B B0C C1C A0D BεD D0D, START A and FINAL D C B. Should return Success") {
    val input11 = "00"
    val transitionsTest11 = "A0AA0BB0CC1CA0DBεDD0D"
    val transitionsList9: List[(String, Char, String)] = transitionsTest11.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList
    val finals7 = "DCB"
    var setFinals = new ListBuffer[String]
    for (finalNodes <- finals7) {
      setFinals += finalNodes.toString
    }
    val setOFFinals = setFinals.toSet

    val nfa8 = NFA.apply("A", transitionsList9, setOFFinals)

    assert(nfa8.backtrackingRecursive("A", input11, 0, ListBuffer[String]() ) == List(List("A", "A", "B"), List("A", "A", "D"), List("A", "B", "C"), List("A", "B", "D", "D"), List("A", "D", "D")))
  }
}
