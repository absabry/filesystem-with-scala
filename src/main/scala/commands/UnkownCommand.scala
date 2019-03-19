package commands

import filesystem.State

class UnkownCommand extends Command {

  override def apply(state: State): State = {
    state.setMessage("Command not found")
  }
}
