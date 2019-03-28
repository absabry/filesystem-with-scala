package commands

import files.{DirEntry, Directory}
import filesystem.State

abstract class CreateEntry(name:String) extends Command {


  def checkIllegal(name: String): Boolean

  def createSpecificEntry(state:State): DirEntry

  def run(state: State, name: String): State = {
    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      if (path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        val oldEntry = currentDirectory.findEntry(path.head).asDirectory
        currentDirectory.replaceDirectory(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
      }
    }

    val wd = state.workingDirectory

    // 1. all the dirs in the full path
    val dirs = wd.getAllFoldersInPath

    // 2. create new directory entry in the wd
    val newEntry :DirEntry = createSpecificEntry(state)

    // 3. update the whole directory structure starting from the root
    val newRoot = updateStructure(state.root, dirs, newEntry)

    // 4.
    val newWd = newRoot.findDescendant(dirs)

    State(newRoot, newWd)
  }

  override def apply(state: State): State = {
    val wd = state.workingDirectory

    if(wd.hasEntry(name))
      state.setMessage(s"Entry $name already exists")
    else if (name.contains(Directory.SEPARATOR))
      state.setMessage(s"Entry $name already exists")
    else if (checkIllegal(name))
      state.setMessage(s"$name : illegale entry name")
    else
      run(state, name)

  }

}
