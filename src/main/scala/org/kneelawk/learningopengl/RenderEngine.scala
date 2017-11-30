package org.kneelawk.learningopengl

/*
 * I wonder if you could have all the rendering code be in special
 * implicit ModelRenderers and have each one know how to render a specific kind of model?
 */

class RenderEngine {
  private var update: () => Unit = () => {}
  private var window: Window = null
  private var camera: Camera = null

  def init(window: Window, camera: Camera) {
    this.window = window
    this.camera = camera

    GraphicsInterface.setupContext()

    GraphicsInterface.setBackground(0.2f, 0.2f, 0.2f, 1.0f)
  }

  def setUpdateCallback(callback: () => Unit) {
    update = callback
  }

  def loop() {
    while (!window.shouldWindowClose()) {
      SystemInterface.pollEvents()

      GraphicsInterface.update()

      update()

      window.refresh()
    }
  }

  def addModel[Model: ModelRenderer](model: Model): Unit = ???
  def removeModel[Model: ModelRenderer](model: Model): Unit = ???
  def clearModels(): Unit = ???
}