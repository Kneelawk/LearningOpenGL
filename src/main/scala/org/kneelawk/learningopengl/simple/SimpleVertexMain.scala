package org.kneelawk.learningopengl.simple

import org.joml.Matrix4f
import org.kneelawk.learningopengl._
import org.lwjgl.glfw.GLFW.{GLFW_KEY_ESCAPE, GLFW_RELEASE}

object SimpleVertexMain extends AppMain {
  val width = 1280
  val height = 720
  val cube = SimpleVertexModel(Array(
    -1, -1, -1,
    1, 1, -1,
    1, -1, -1,
    -1, -1, -1,
    -1, 1, -1,
    1, 1, -1,
    -1, -1, -1,
    1, -1, -1,
    1, -1, 1,
    -1, -1, -1,
    1, -1, 1,
    -1, -1, 1,
    -1, -1, -1,
    -1, -1, 1,
    -1, 1, 1,
    -1, -1, -1,
    -1, 1, 1,
    -1, 1, -1,
    1, 1, 1,
    -1, 1, 1,
    -1, -1, 1,
    1, 1, 1,
    -1, -1, 1,
    1, -1, 1,
    1, 1, 1,
    1, 1, -1,
    -1, 1, -1,
    1, 1, 1,
    -1, 1, -1,
    -1, 1, 1,
    1, 1, 1,
    1, -1, 1,
    1, -1, -1,
    1, 1, 1,
    1, -1, -1,
    1, 1, -1
  ), new Matrix4f)

  val camera = new Camera()
  var rotation: Double = 0

  def run(args: Array[String]) {
    try {
      SystemInterface.init()

      val window = new Window("Learning OpenGL - SimpleVertex", 1280, 720)
      window.init()
      window.setKeyCallback((_, key, _, action, _) =>
        if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) window.requestClose())
      window.selectContext()
      window.show()

      GraphicsInterface.setupContext()

      camera.setProjection(45, window.getWidth.toFloat / window.getHeight.toFloat, 0.01f, 100)
      camera.setView(0, 3, 5, 0, 0, 0, 0, 1, 0)
      // Z+ is towards the viewer
      camera.update()

      val engine = new SimpleVertexEngine()
      engine.init(window, camera)
      engine.setUpdateCallback(update)
      engine.addModel(cube)
      engine.loop()
      engine.destroy()

      window.destroy()
    } finally {
      SystemInterface.destroy()
    }
  }

  def update(delta: Float): Unit = {
    camera.setView(math.sin(rotation).toFloat * 5, 3, math.cos(rotation).toFloat * 5, 0, 0, 0, 0, 1, 0)
    camera.update()

    rotation += 0.25 * delta
    if (rotation > math.Pi * 2) {
      rotation -= math.Pi * 2
    }
  }
}