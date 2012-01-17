import sbt._

// Shell prompt which shows the current project,
// git branch and build version
object ShellPrompt {
  object devnull extends ProcessLogger {
    def info (s: => String) {}
    def error (s: => String) { }
    def buffer[T] (f: => T): T = f
  }
  def currBranch = (
    ("git branch" lines_! devnull filter (_ startsWith "*") headOption)
      getOrElse "-" stripPrefix "* "
    )

  def buildShellPrompt(buildVersion: String) = {
    (state: State) => {
      val currProject = Project.extract (state).currentProject.id
      "%s:%s:%s> ".format (
        currProject, currBranch, buildVersion
      )
    }
  }
}