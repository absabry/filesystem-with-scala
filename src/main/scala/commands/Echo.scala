package commands

import java.util.concurrent.atomic.DoubleAccumulator

import files.{Directory, File}
import filesystem.State

import scala.annotation.tailrec

class Echo(args: Array[String]) extends Command {

  def createContent(args: Array[String], topIndex: Int): String = {
    @tailrec
    def helper(currentIndex: Int, accumulator: String): String = {
      if (currentIndex >= topIndex) accumulator
      else helper(currentIndex + 1, accumulator + " " + args(currentIndex))
    }

    helper(0, "")
  }

  def getRootAfterEcho(currentDirectory: Directory, path: List[String],
                       contents: String, append: Boolean): Directory = {
    if (path.isEmpty) currentDirectory
    else if (path.tail.isEmpty) {
      val dirEntry = currentDirectory.findEntry(path.head)
      if (dirEntry == null)
        currentDirectory.addEntry(new File(currentDirectory.path, path.head, contents))
      else if (dirEntry.isDirectory) currentDirectory
      else if (append) currentDirectory.replaceEntry(path.head, dirEntry.asFile.appendContents(contents))
      else currentDirectory.replaceEntry(path.head, dirEntry.asFile.setContents(contents))
    }
    else {
      val nextDir = currentDirectory.findEntry(path.head).asDirectory
      val newNextDir = getRootAfterEcho(nextDir, path.tail, contents, append)
      if (nextDir == newNextDir) currentDirectory
      else currentDirectory.replaceEntry(path.head, newNextDir)
    }
  }

  def runEcho(state: State, contents: String, filename: String, append: Boolean): State = {
    if (filename.contains(Directory.SEPARATOR))
      state.setMessage("Echo: filename must not contains separators")
    else {
      val newRoot: Directory = getRootAfterEcho(state.root,
        state.workingDirectory.getAllFoldersInPath :+ filename,
        contents, append)
      if (newRoot == state.root)
        state.setMessage(filename + ": no such file")
      else
        State(newRoot, newRoot.findDescendant(state.workingDirectory.getAllFoldersInPath))
    }
  }

  override def apply(state: State): State = {
    if (args.isEmpty) state
    else if (args.length == 1) state.setMessage(args(0))
    else {
      val operator = args(args.length - 2)
      val filename = args(args.length - 1)
      val contents = createContent(args, args.length - 2)

      if (">>".equals(operator))
        runEcho(state, contents, filename, append = true)
      else if (">".equals(operator))
        runEcho(state, contents, filename, append = false)
      else
        state.setMessage(createContent(args, args.length))
    }
  }
}
