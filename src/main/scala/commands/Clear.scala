package commands

import filesystem.State

class Clear extends Command {
  override def apply(state: State): State = {
    println("\n" * 1000)
    state.setMessage("")
  }
}
