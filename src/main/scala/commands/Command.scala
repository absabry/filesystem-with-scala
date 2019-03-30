package commands

import files.Directory
import filesystem.State

trait Command {
  def apply(state: State): State
  // todo remove apply from here

  // TODO
  //  MKDIR
  //    implement mkdir nested command
  //    implement commands with file spaces (should be between quotes or single quotes")
  //  RM
  //    rm directory, should work? warniing? working with yes & no like linux?
  //    rm detect when i try to kill a parent, not worth it man
  //    not working with absolute paths from another dir...
  //  LS
  //    ls file functionalty
  //    implement ls recursively (come with an argument) command
  //  BONUS
  //    cat and echo relative & absolute paths (refactor cd logic and make it in amother class)
  //    find a way (by refactoring) to implement a help function
  //    "did you mean X" functionality
  //    file and folders with different colors in ls !!!!!
}


object Command {

  val MKDIR = "mkdir"
  val LS: String = "ls"
  val PWD: String = "pwd"
  val TOUCH: String = "touch"
  val CLEAR: String = "clear"
  val CD: String = "cd"
  val RM: String = "rm"
  val ECHO: String = "echo"
  val CAT: String = "cat"

  def emptyCommand: Command = (state: State) => state.setMessage("")

  def incompleteCommand(name: String): Command =
    (state: State) => state.setMessage(s"[$name] is an incomplete command")

  def ToManyArgumentCommand(name: String): Command =
    (state: State) => state.setMessage(s"[$name] have too many arguments")


  // method that takes the input, and execute the command
  def from(input: String): Command = {

    val tokens: Array[String] = input.trim.split(" ").filterNot(_ == "")
// todo remove th pattern matching
    if (tokens.isEmpty) emptyCommand
    else if (MKDIR.equals(tokens(0).toLowerCase)) {
      if (tokens.length < 2) incompleteCommand(MKDIR)
      else if (tokens.length > 3) ToManyArgumentCommand(MKDIR)
      else new Mkdir(tokens(1))
    }
    else if (LS.equals(tokens(0).toLowerCase))
      new Ls
    else if (PWD.equals(tokens(0).toLowerCase()))
      new Pwd
    else if (TOUCH.equals(tokens(0).toLowerCase)) {
      if (tokens.length < 2) incompleteCommand(TOUCH)
      else if (tokens.length > 3) ToManyArgumentCommand(TOUCH)
      else new Touch(tokens(1))
    }
    else if (CLEAR.equals(tokens(0).toLowerCase))
      new Clear
    else if (CD.equals(tokens(0).toLowerCase)) {
      if (tokens.length < 2) new Cd(Directory.SEPARATOR)
      else if (tokens.length > 3) ToManyArgumentCommand(CD)
      else new Cd(tokens(1))
    }
    else if (RM.equals(tokens(0).toLowerCase)) {
      if (tokens.length < 2) incompleteCommand(RM)
      else if (tokens.length > 3) ToManyArgumentCommand(RM)
      else new Rm(tokens(1))
    }
    else if (ECHO.equals(tokens(0).toLowerCase)) {
      if (tokens.length < 2) incompleteCommand(RM)
      else new Echo(tokens.tail)
    }
    else if (CAT.equals(tokens(0).toLowerCase)) {
      if (tokens.length < 2) incompleteCommand(CAT)
      else new Cat(tokens(1))
    }
    else new UnkownCommand


  }
}


