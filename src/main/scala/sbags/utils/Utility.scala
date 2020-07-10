package sbags.utils

object Utility {
  def isActionInvoked(condition: => Boolean)(action: => Unit): Boolean = {
    if (condition) {
      action
      true
    } else false
  }
}
