package org.kneelawk.learningopengl

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR
import org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR
import org.lwjgl.glfw.GLFW.GLFW_FALSE
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT
import org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE
import org.lwjgl.glfw.GLFW.GLFW_TRUE
import org.lwjgl.glfw.GLFW.GLFW_VISIBLE
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwDefaultWindowHints
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwHideWindow
import org.lwjgl.glfw.GLFW.glfwMakeContextCurrent
import org.lwjgl.glfw.GLFW.glfwSetKeyCallback
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwShowWindow
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFW.glfwWindowShouldClose
import org.lwjgl.glfw.GLFWKeyCallbackI
import org.lwjgl.system.MemoryUtil.NULL

/**
 * A wrapper around GLFW's window functions.
 */
class Window(title: String, width: Int, height: Int) {
  private var window: Long = NULL

  /**
   * Sometimes you might be handed a window pointer by one of GLFW's functions.
   * Here you can tell what this window's pointer is for comparison.
   */
  def windowPtr = window

  /**
   * The window's width.
   */
  def getWidth = width

  /**
   * The window's height.
   */
  def getHeight = height

  /**
   * Destroys the window and frees its resources.
   * Once it has been destroyed, it cannot be reopened.
   */
  def destroy() {
    glfwFreeCallbacks(window)

    glfwDestroyWindow(window)
  }

  /**
   * Sets up the window.
   * Allocates the window pointer and resources.
   * Sets window hints.
   *
   * @return true if the setup was successful, false if otherwise.
   */
  def init(): Boolean = {
    glfwDefaultWindowHints()
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4)
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 5)
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE)
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
    glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)

    window = glfwCreateWindow(width, height, title, NULL, NULL)

    if (window == NULL) {
      Console.err.println("Unable to create GLFW window!")
      return false
    }

    return true
  }

  /**
   * Sets the window's key callback.<br>
   * Listener args: window-ptr: Long, key: Int, scancode: Int, actions: Int, mods: Int.
   */
  def setKeyCallback(listener: (Long, Int, Int, Int, Int) => Unit) {
    glfwSetKeyCallback(window, new GLFWKeyCallbackI {
      def invoke(window: Long, key: Int, scancode: Int, action: Int, mods: Int) {
        listener(window, key, scancode, action, mods)
      }
    })
  }

  /**
   * Makes the window visible.
   */
  def show() {
    glfwShowWindow(window)
  }

  /**
   * Hides the window.
   */
  def hide() {
    glfwHideWindow(window)
  }

  /**
   * Selects this window's context for rendering for this thread.
   */
  def selectContext() {
    glfwMakeContextCurrent(window)
  }

  /**
   * Requests the window begin deallocation and send appropriate events to the engine.
   * Calling this likely begins the shutdown of the application.
   */
  def requestClose() {
    glfwSetWindowShouldClose(window, true)
  }

  /**
   * How the engine tells whether to keep going or to begin shutdown.
   */
  def shouldWindowClose(): Boolean = {
    return glfwWindowShouldClose(window)
  }

  /**
   * Swaps the window's buffers.
   * Sends what has been drawn on the current buffer out to be displayed.
   */
  def refresh() {
    glfwSwapBuffers(window)
  }
}
