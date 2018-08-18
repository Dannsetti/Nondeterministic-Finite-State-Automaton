package nfaClass

import scala.io.Source
import scala.collection.mutable.ListBuffer



case class NFA(start: String, transitions: List[(String, Char, String)], finals: Set[String]) {


  def getTransitionsForState(currentNode: String): List[(String, Char, String)] = {
    var possibleTransitionsForTheCurrentNode = new ListBuffer[(String, Char, String)]
    for (transitionsStartingWithCurrentNode <- transitions) {
      if (transitionsStartingWithCurrentNode._1 == currentNode) {
        possibleTransitionsForTheCurrentNode += transitionsStartingWithCurrentNode
      }
    }
    possibleTransitionsForTheCurrentNode.toList
  }

  def getPathsForInput(transitionsForCurrentNode: List[(String, Char, String)], currentInput: Char): List[(String, Char, String)] = {
    val emptyTransition = "ε".toCharArray // (U+03B5)
    var possiblePathsUsingTheCurrentInput = new ListBuffer[(String, Char, String)]
    for (theActualInput <- transitionsForCurrentNode) {
      if (theActualInput._2 == currentInput || theActualInput._2 == emptyTransition(0)) {
        possiblePathsUsingTheCurrentInput += theActualInput
      }
    }
    possiblePathsUsingTheCurrentInput.toList
  }


  def backtrackingRecursive(currentNode: String, input: String, currentIndex: Int, currentPath: ListBuffer[String]): List[List[String]] = {
    val pathFromNode = currentPath ++ List(currentNode)
    val emptyTransition = "ε".toCharArray // (U+03B5)
    // check if it is an end state
    if (finals.contains(currentNode)) {
      if (currentIndex == input.length) {
        return List(pathFromNode.toList)
      }
    }

    var currentInput: Char = '0'
    // get each transition for the current node
    var transitionsForCurrentNode: List[(String, Char, String)] = null
    // get each possible path from the current node that accepts the currentInput
    var pathsForInput: List[(String, Char, String)] = null

    if (currentIndex >= input.length) {
      currentInput = input(input.length - 1)
      // get each transition for the current node
      transitionsForCurrentNode = getTransitionsForState(currentNode)
      // get each possible path from the current node that accepts the currentInput
      pathsForInput = getPathsForInput(transitionsForCurrentNode, currentInput)
      val emptyPathsForInput = getPathsForInput(transitionsForCurrentNode, currentInput)
      var emptyValidPaths = List[List[String]]()

      for (nextNode <- emptyPathsForInput) {
        if (nextNode._2 == emptyTransition(0)) {
          val _resultForEmptyTransition = backtrackingRecursive(nextNode._3, input, currentIndex, pathFromNode)
          if (_resultForEmptyTransition != null) {
            emptyValidPaths = emptyValidPaths ++ _resultForEmptyTransition
          }
        }
      }
      if (emptyValidPaths.nonEmpty) {
        return emptyValidPaths
      }
      return null
    }

    currentInput = input(currentIndex)
    // get each transition for the current node
    transitionsForCurrentNode = getTransitionsForState(currentNode)
    // get each possible path from the current node that accepts the currentInput
    pathsForInput = getPathsForInput(transitionsForCurrentNode, currentInput)
    var validPaths = List[List[String]]()

    for (nextNode <- pathsForInput) {
      // check if start node is the same as the final
      // Special case where the the final node is the start itself
      if (finals.size == 1 && finals.contains(start)) {
        if (nextNode._1 == nextNode._3) {
          //println("Returning Special")
          return List(List(start))
        }
      }

      if (nextNode._2 == emptyTransition(0)) {
        val resultForEmptyTransition = backtrackingRecursive(nextNode._3, input, currentIndex, pathFromNode)
        //println("empty ", resultForEmptyTransition)
        if (resultForEmptyTransition != null) {
          validPaths = validPaths ++ resultForEmptyTransition
        }
      }
      else {
        val result = backtrackingRecursive(nextNode._3, input, currentIndex + 1, pathFromNode)
        if (result != null) {
          validPaths = validPaths ++ result
        }
      }
    }
    if (validPaths.nonEmpty) {

      return validPaths
    }
    else {
      return null
    }
  }


  def solveByBacktracking(input: String): Option[List[String]] = {
    val currentPath = ListBuffer[String]()
    val result = backtrackingRecursive(start, input, 0, currentPath)
    if (result != null) {
      val backTrackingResult: Option[List[String]] = Option(result.map { s => s.toString })

      return backTrackingResult

    }
    None: Option[List[String]]
  }


  // As clarified with Keith we are required to do only one of the solutions.
  //def solveBySetOfStates(input: String): Option[List[String]] = ???

  //or def solveBySetOfPaths(input: String): Option[List[String]] = ???

}

// Write a companion object NFA containing a main method

object NFA {

  // The object should read and process NFAs from a file named nfa.txt , process them, and print the results.
  def main () {
    val filename = Source.fromFile("nfa.txt")
    val lines = filename.getLines.toList
    var runsList = new ListBuffer[String]
    var finalNode = ""
    var startNode = ""
    var transitions = ""

    for (line <- lines) {
      val firstWord = line.split(" ")
      if (firstWord(0) == "START") {
        startNode = line.stripPrefix("START")
        startNode = startNode.replaceAll("""[\p{Punct}&&[^.]]""", "")
        startNode = startNode.replaceAll("\\s+", "")
      }

      else if (firstWord(0) == "FINAL") {
        finalNode = line.stripPrefix("FINAL")
        finalNode = finalNode.replaceAll("""[\p{Punct}&&[^.]]""", "")
        finalNode = finalNode.replaceAll("\\s+", "")
        finalNode = finalNode

      }
      else if (firstWord(0) == "TRANSITIONS") {
        transitions = line.stripPrefix("TRANSITIONS")
        transitions = transitions.replaceAll("""[\p{Punct}&&[^.]]""", "")
        transitions = transitions.replaceAll("\\s+", "")

      }

      else if (firstWord(0) == "INPUT") {
        var inputRun = line.stripPrefix("INPUT")
        inputRun = inputRun.replaceAll("""[\p{Punct}&&[^.]]""", "")
        inputRun = inputRun.replaceAll("\\s+", "")
        runsList += inputRun
      }

    }
    filename.close()

    val inputList = runsList.toList

    // Get all the possibles final nodes
    var setFinals = new ListBuffer[String]
    for (finalNodes <- finalNode) {
      setFinals += finalNodes.toString
    }

    val setOFFinals = setFinals.toSet

    val transitionsList: List[(String, Char, String)] = transitions.grouped(3).map{s => (s(0).toString, s(1), s(2).toString)}.toList


    for (i <- inputList) {
      val newNFA = new NFA(startNode, transitionsList, setOFFinals)

      val createNFA = newNFA.solveByBacktracking(i)

      if (createNFA.isDefined && createNFA.exists(_.nonEmpty)) {
        //loop to print each of the paths found for the current input
        for (j <- createNFA) {
          for (k <- j) {

            if(k.length > 1) {

              var cut1 = k.substring(4)
              cut1 = cut1.replaceAll("""[\p{Punct}&&[^.]]""", "")
              cut1 = cut1.replaceAll("\\s+", "")
              val cut2: List[String] = cut1.map{s => s.toString}.toList
              if (!cut2.forall(_ == cut2.head)) {
                val optionList: Option[List[String]] = Option(cut2)
                println("Success")
                println("Input " + i + " found this valid Path " + optionList)
              }
              else {
                val specialCase = Option(List(startNode))
                println("Success")
                println("Special case Start and Finish node were the same. Input " + i + " Found this valid Path " + specialCase)
              }
            }
          }
        }
      }
      else {
        println("Failure")
        println("No valid path using the Input " + i)
      }
    }
  }
}


//NFA.main()
