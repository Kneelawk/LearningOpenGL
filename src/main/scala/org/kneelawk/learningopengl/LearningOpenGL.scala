package org.kneelawk.learningopengl

import org.kneelawk.learningopengl.simple.SimpleVertexMain
import org.lwjgl.Version

object LearningOpenGL {
  val mains: Map[String, AppMain] = Map[String, AppMain](
    "simple-vertex" -> SimpleVertexMain)

  def main(args: Array[String]) {
    println("Hello LWJGL " + Version.getVersion + "!")

    if (args.length < 1 || !mains.contains(args(0))) {
      println("Mains:")
      println(mains.keySet.mkString("\n"))
      return
    }

    val main = mains(args(0))

    main.run(args)
  }
}