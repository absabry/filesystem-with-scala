package commands

import files.Directory
import filesystem.State

class Rm(entryName: String) extends Command {

  override def apply(state: State): State = {

    val absolutePath =
      if (entryName.startsWith(Directory.SEPARATOR)) entryName
      else if (state.workingDirectory.isRoot) state.workingDirectory.path + entryName
      else state.workingDirectory.path + Directory.SEPARATOR + entryName

    if (Directory.ROOT_PATH == absolutePath)
      state.setMessage("You're going to kill us all one day.")
    else run(state, absolutePath)
  }

  def run(state: State, absolutePath: String): State = {

    def rmHelper(currentDirectory: Directory, path: List[String]): Directory = {
      if (path.isEmpty) currentDirectory
      else if (path.tail.isEmpty) currentDirectory.removeEntry(path.head)
      else {
        val nextDirectory = currentDirectory.findEntry(path.head)
        if (!nextDirectory.isDirectory) currentDirectory
        else {
          val newNextDirectory = rmHelper(nextDirectory.asDirectory, path.tail)
          if (newNextDirectory == nextDirectory) currentDirectory
          else currentDirectory.replaceEntry(path.head, newNextDirectory)
        }
      }
    }

    val listOfPath = absolutePath.substring(1).split(Directory.SEPARATOR).toList
    val newRoot: Directory = rmHelper(state.root, listOfPath)
    if (newRoot == state.root)
      state.setMessage(s"$entryName :no such file or directory")
    else
      State(newRoot, newRoot.findDescendant(state.workingDirectory.path.substring(1)))
  }

}
