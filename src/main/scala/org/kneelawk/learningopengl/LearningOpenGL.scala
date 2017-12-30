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

object LearningOpenGL {
  def main(args: Array[String]) {
    println("Hello LWJGL " + Version.getVersion + "!")

    try {
      SystemInterface.init()

      val window = new Window("KLines", 1280, 720)
      window.init()
      window.setKeyCallback((windowPtr, key, scancode, action, mods) =>
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) window.requestClose())
      window.selectContext()
      window.show()

      val engine = new RenderEngine
      engine.init(window, null)
      engine.setUpdateCallback(update)
      engine.loop()

      window.destroy()
    } finally {
      SystemInterface.destroy()
    }
  }

  def update() {

  }
}