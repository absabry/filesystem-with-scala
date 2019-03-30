package commands


import files.Directory
import filesystem.State

import scala.annotation.tailrec

class Cd(dir: String) extends Command {
  override def apply(state: State): State = {

    val newDir = findEntryRecursively(state)

    if (newDir == null || !newDir.isDirectory)
      State(state.root, state.workingDirectory, s"$dir: directory not found")
    else
      State(state.root, newDir.asDirectory)
  }

  def findEntryRecursively(state: State): Directory = {

    def createListFromAbsolutePath(): List[String] = {
      val absolutePath =
        if (dir.startsWith(Directory.SEPARATOR)) dir
        else if (state.workingDirectory.isRoot) state.workingDirectory.path + dir
        else state.workingDirectory.path + Directory.SEPARATOR + dir

      absolutePath.substring(1) //remove first separator
        .split(Directory.SEPARATOR).toList // convert to list of tokens
    }

    def removeDots(path: List[String]): List[String] = {
      @tailrec
      def convertDotsInPath(currentPath: List[String], result: List[String]): List[String] = {
        if (currentPath.isEmpty) result
        else if (currentPath.head.equals("."))
          convertDotsInPath(currentPath.tail, result) // the dot is ignored
        else if (currentPath.head.equals("..")) {
          if (result.isEmpty) null // we want to go before ROOT, impossible
          else convertDotsInPath(currentPath.tail, result.init)
          // do not add currentPath.head, and ignore the last added one (using init function)
          // ['a', 'b', 'c', '..'] should return ==> ['a', 'b']
        }
        else // normal case without any points
          convertDotsInPath(currentPath.tail, result :+ currentPath.head)
      }

      convertDotsInPath(path, List())
    }

    @tailrec
    def findEntryHelper(parentPath: Directory, pathList: List[String]): Directory = {
      if (pathList.isEmpty || pathList.head.isEmpty) parentPath
      else {
        // search for the entry in the current dir
        val nextDir = parentPath.findEntry(pathList.head)
        if (nextDir == null || !nextDir.isDirectory) null
        else findEntryHelper(nextDir.asDirectory, pathList.tail)
      }
    }


    val listOfPath = createListFromAbsolutePath()
    val elementsInPath = removeDots(listOfPath)
    if (elementsInPath == null) null else findEntryHelper(state.root, elementsInPath)
  }
}