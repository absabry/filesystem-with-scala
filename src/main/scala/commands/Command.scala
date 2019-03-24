package commands

import filesystem.State

trait Command {
  def apply(state:State):State
  // TODO find a way to implement a help function
  // TODO refactor Command, checks soudlnt be in front of everyone like that
  // TODO "did you mean X" functionality
}


object Command {

  val MKDIR = "mkdir"
  val LS = "ls"

  def emptyCommand: Command = (state: State) => state

  def incompleteCommand(name:String): Command =
    (state:State) => state.setMessage(s"[$name] is an incomplete command")

  def ToManyArgumentCommand(name:String): Command =
    (state:State) => state.setMessage(s"[$name] have too many arguments")

  // method that takes the input, and execute the command
   def from(input:String): Command = {

     val tokens: Array[String] = input.trim.split(" ").filterNot(_ == "")

     if(tokens.isEmpty) emptyCommand
     else if (MKDIR.equals(tokens(0).toLowerCase)){
       if (tokens.length < 2) incompleteCommand(MKDIR)
       else if (tokens.length > 3) ToManyArgumentCommand(MKDIR)
       else new Mkdir(tokens(1))
     }
     else if (LS.equals(tokens(0).toLowerCase))
       new Ls
     else new UnkownCommand


   }
}


