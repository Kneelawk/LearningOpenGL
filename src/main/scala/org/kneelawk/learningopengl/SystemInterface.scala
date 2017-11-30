package org.kneelawk.learningopengl

import org.lwjgl.glfw.GLFW.glfwInit
import org.lwjgl.glfw.GLFW.glfwPollEvents
import org.lwjgl.glfw.GLFW.glfwSetErrorCallback
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFWErrorCallback

object SystemInterface {
  
  /**
   * Initializes GLFW.
   * 
   * @return true on a successful initialization, false otherwise.
   */
  def init(): Boolean = {
    glfwSetErrorCallback(GLFWErrorCallback.createPrint(System.err))

    return glfwInit()
  }

  /**
   * Shuts down GLFW.
   */
  def destroy() {
    glfwTerminate()
  }

  /**
   * Refreshes GLFW's event listeners.
   */
  def pollEvents() {
    glfwPollEvents()
  }
}