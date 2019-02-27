package org.kneelawk.learningopengl

import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW._
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
  def windowPtr: Long = window

  /**
   * The window's width.
   */
  def getWidth: Int = width

  /**
   * The window's height.
   */
  def getHeight: Int = height

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

    true
  }

  /**
   * Sets the window's key callback.<br>
   * Listener args: window-ptr: Long, key: Int, scancode: Int, actions: Int, mods: Int.
   */
  def setKeyCallback(listener: (Long, Int, Int, Int, Int) => Unit) {
    glfwSetKeyCallback(window, (window: Long, key: Int, scancode: Int, action: Int, mods: Int) => {
      listener(window, key, scancode, action, mods)
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
    glfwWindowShouldClose(window)
  }

  /**
   * Swaps the window's buffers.
   * Sends what has been drawn on the current buffer out to be displayed.
   */
  def refresh() {
    glfwSwapBuffers(window)
  }
}
