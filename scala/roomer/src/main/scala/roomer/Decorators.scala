package roomer

import swing._

class CenteredGridBagPanel(comp: Component*) extends GridBagPanel {
  layout(new BoxPanel(Orientation.Vertical) {
    contents ++= comp
  }) = new Constraints {
    anchor = GridBagPanel.Anchor.Center
  }
}

class TitledTextField(title: String, cols: Int = 0) extends TextField(cols) {
  border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), title)
}

class TitledFormattedTextField(title: String, format: String) extends
    FormattedTextField(new java.text.MessageFormat(format)) {
  border = Swing.TitledBorder(Swing.EtchedBorder(Swing.Raised), title)
}

object ErrorDialog {
  def apply(msg: String) =
    Dialog.showMessage(message = msg, messageType = Dialog.Message.Error)
}
