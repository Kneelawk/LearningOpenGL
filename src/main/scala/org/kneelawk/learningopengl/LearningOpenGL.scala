package org.kneelawk.learningopengl

import org.lwjgl.Version
import org.lwjgl.glfw.Callbacks._
import org.lwjgl.glfw.GLFW._
import org.lwjgl.opengl.GL11._
import org.lwjgl.system.MemoryUtil._
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.glfw.GLFWVidMode
import org.lwjgl.opengl.GL
import org.kneelawk.learningopengl.simple.SimpleVertexModel
import org.joml.Matrix4f
import org.kneelawk.learningopengl.simple.SimpleVertexMain

object LearningOpenGL {
  val mains = Map[String, AppMain](
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