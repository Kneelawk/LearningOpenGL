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

  def setBackground(r: Float, g: Float, b: Float, a: Float) {
    glClearColor(r, g, b, a)
  }

  def update() {
    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
  }
}