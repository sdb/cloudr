import sbt._

object Idea {
  // quick 'n dirty way to add Android Facet to IDEA projects
  val command: Command = Command.command("gen-idea-android") { state =>
    val base = Project.extract (state).currentProject.base
    transform(base / ".." / ".idea_modules" / "cloudr.iml", "app")
    transform(base / ".." / ".idea_modules" / "tests.iml", "tests")
    state
  }

  import xml._
  import xml.transform._

  object ReplaceJdk extends RewriteRule {
    override def transform(n: Node): Seq[Node] = n match {
      case e @ Elem(prefix, "orderEntry", attribs, scope, children @ _*) if (e \ "@type").text == "inheritedJdk" =>
          <orderEntry type="jdk" jdkName="Android 3.2 Platform" jdkType="Android SDK" />
      case other => other
    }
  }

  object ReplaceJdkTransformer extends RuleTransformer(ReplaceJdk)

  case class AddFacet(module: String) extends RewriteRule {
    def path(p: String) = "/../" + module + "/" + p
    override def transform(n: Node): Seq[Node] = n match {
      case e @ Elem(prefix, "component", attribs, scope, children @ _*) if (e \ "@name").text == "FacetManager" =>
        <component name="FacetManager">
          { children }
          <facet type="android" name="Android">
            <configuration>
                <option name="GEN_FOLDER_RELATIVE_PATH_APT" value={path("gen")} />
                <option name="GEN_FOLDER_RELATIVE_PATH_AIDL" value={path("gen")} />
                <option name="MANIFEST_FILE_RELATIVE_PATH" value={path("AndroidManifest.xml")} />
                <option name="RES_FOLDER_RELATIVE_PATH" value={path("res")} />
                <option name="ASSETS_FOLDER_RELATIVE_PATH" value={path("assets")} />
                <option name="LIBS_FOLDER_RELATIVE_PATH" value={path("libs")} />
                <option name="REGENERATE_R_JAVA" value="true" />
                <option name="REGENERATE_JAVA_BY_AIDL" value="true" />
                <option name="USE_CUSTOM_APK_RESOURCE_FOLDER" value="false" />
                <option name="CUSTOM_APK_RESOURCE_FOLDER" value="" />
                <option name="USE_CUSTOM_COMPILER_MANIFEST" value="false" />
                <option name="CUSTOM_COMPILER_MANIFEST" value="" />
                <option name="APK_PATH" value="" />
                <option name="LIBRARY_PROJECT" value="false" />
                <option name="RUN_PROCESS_RESOURCES_MAVEN_TASK" value="true" />
                <option name="GENERATE_UNSIGNED_APK" value="false" />
            </configuration>
          </facet>
        </component>
      case e @ Elem(prefix, "component", attribs, scope, children @ _*) if (e \ "@name").text == "NewModuleRootManager" =>
        ReplaceJdkTransformer(e)
      case other => other
    }
  }

  case class AddFacetTransformer(module: String) extends RuleTransformer(AddFacet(module))

  def transform(f: java.io.File, module: String) = {
    val x = XML.loadFile(f)
    val t = AddFacetTransformer(module)(x)
    XML.save(f.getAbsolutePath, t)
  }
}