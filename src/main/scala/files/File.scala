package files

class File(override val parentPath: String,
           override val name: String,
           val contents: String)
  extends DirEntry(parentPath, name) {

  def setContents(newContents: String): File =
    new File(parentPath, name, newContents)

  def appendContents(newContents: String): File =
    setContents(contents + "\n" + newContents)


  override def asDirectory: Directory =
    throw new FileSystemException("A file cannot be converted to a directory")

  override def getType: String = "File"

  override def asFile: File = this

  override def isFile: Boolean = true

  override def isDirectory: Boolean = false
}

object File {
  def empty(parentPath: String, name: String): File =
    new File(parentPath, name, "")
}
