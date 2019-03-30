package commands

import files.Directory
import filesystem.State

trait Command extends (State => State) {
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

  def from(input: String): Command = { // method that takes the input, and execute the command
    val tokens: Array[String] = input.trim.split(" ").filterNot(_ == "")
    if (tokens.isEmpty) emptyCommand
    tokens(0).toLowerCase match {
      case MKDIR =>
        if (tokens.length < 2) incompleteCommand(MKDIR)
        else if (tokens.length > 3) ToManyArgumentCommand(MKDIR)
        else new Mkdir(tokens(1))
      case LS => new Ls
      case PWD => new Pwd
      case TOUCH =>
        if (tokens.length < 2) incompleteCommand(TOUCH)
        else if (tokens.length > 3) ToManyArgumentCommand(TOUCH)
        else new Touch(tokens(1))
      case CLEAR => new Clear
      case CD =>
        if (tokens.length < 2) new Cd(Directory.SEPARATOR)
        else if (tokens.length > 3) ToManyArgumentCommand(CD)
        else new Cd(tokens(1))
      case RM =>
        if (tokens.length < 2) incompleteCommand(RM)
        else if (tokens.length > 3) ToManyArgumentCommand(RM)
        else new Rm(tokens(1))
      case ECHO =>
        if (tokens.length < 2) incompleteCommand(RM)
        else new Echo(tokens.tail)
      case CAT =>
        if (tokens.length < 2) incompleteCommand(CAT)
        else new Cat(tokens(1))
      case _ => new UnkownCommand
    }
  }
}


