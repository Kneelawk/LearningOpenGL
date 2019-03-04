package org.kneelawk.learningopengl

import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11._

object GraphicsInterface {
  def setupContext() {
    GL.createCapabilities(true)

    val renderer = glGetString(GL_RENDERER)
    val version = glGetString(GL_VERSION)
    val vendor = glGetString(GL_VENDOR)

    println(s"GL_RENDERER: $renderer")
    println(s"GL_VERSION: $version")
    println(s"GL_VENDOR: $vendor")
  }
}