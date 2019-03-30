package files

import scala.annotation.tailrec

case class Directory(override val parentPath: String,
                     override val name: String, contents: List[DirEntry])
  extends DirEntry(parentPath, name) {

  def isRoot: Boolean = parentPath.isEmpty

  def asDirectory: Directory = this

  def asFile: File = throw new FileSystemException("A file cannot be converted to a directory")

  def hasEntry(name: String): Boolean = findEntry(name) != null

  /**
    * Example /a/b/c/d => List["a", "b", "c", "d"]
    */
  def getAllFoldersInPath: List[String] =
    path.substring(1).split(Directory.SEPARATOR).toList.filter(!_.isEmpty)

  def findDescendant(path: List[String]): Directory = {
    if (path.isEmpty) this
    else findEntry(path.head).asDirectory.findDescendant(path.tail)
  }

  def findDescendant(path: String): Directory = {
    if (path.isEmpty) this
    else findDescendant(path.split(Directory.SEPARATOR).toList)
  }


  def removeEntry(entryName: String): Directory = {
    if (!hasEntry(entryName)) this
    else new Directory(parentPath, name, contents.filter(x => !x.name.equals(entryName)))
  }

  def replaceEntry(entryName: String, newEntry: DirEntry): Directory =
    new Directory(parentPath, name, contents.filter(e => !e.name.equals(entryName)) :+ newEntry)


  def addEntry(newEntry: DirEntry): Directory =
    new Directory(parentPath, name, contents :+ newEntry)

  /*
  Find entry within one repo (don't search for it recursively...)
   */
  def findEntry(entryName: String): DirEntry = {
    @tailrec
    def helper(name: String, contentList: List[DirEntry]): DirEntry =
      if (contentList.isEmpty) null
      else if (contentList.head.name.equals(name)) contentList.head
      else helper(name, contentList.tail)

    helper(entryName, contents)
  }

  def replaceDirectory(entryName: String, newDir: Directory): Directory =
    new Directory(parentPath, name, contents
      .filter(!_.name.equals(entryName)) :+ newDir)

  override def getType: String = "Directory"

  override def isFile: Boolean = false

  override def isDirectory: Boolean = true
}

object Directory {
  val ROOT_PATH: String = "/"
  val SEPARATOR: String = "/"

  /**
    * Create root folder
    *
    * @return the new root folder
    */
  def ROOT: Directory = Directory.empty("", "")

  /**
    *
    * @param parentPath
    * @param name
    * @return
    */
  def empty(parentPath: String, name: String): Directory = {
    new Directory(parentPath, name, List())
  }
}