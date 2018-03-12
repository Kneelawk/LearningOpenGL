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

object LearningOpenGL {
  val width = 1280
  val height = 720
  
  val triangle = new SimpleVertexModel(Array(
      -1, -1, 0,
      1, -1, 0,
      0, 1, 0
      ), new Matrix4f)

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

      val camera = new Camera
      camera.setProjection(45, width.toFloat / height.toFloat, 0.01f, 100)
      camera.setView(0, 0, 5, 0, 0, 0, 0, 1, 0)
      // Z+ is towards the viewer

//      val engine = new RenderEngine
//      engine.init(window, camera)
//      engine.setUpdateCallback(update _)
//      engine.loop()

      window.destroy()
    } finally {
      SystemInterface.destroy()
    }
  }

  def update() {

  }
}