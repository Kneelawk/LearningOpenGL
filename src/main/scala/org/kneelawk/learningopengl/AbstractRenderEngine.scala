package org.kneelawk.learningopengl

/*
 * I wonder if you could have all the rendering code be in special
 * implicit ModelRenderers and have each one know how to render a specific kind of model?
 * 
 * No, the functionality required to keep track of models requires the ModelRenderer's api
 * to effectively become that of the RenderEngine.
 */

abstract class AbstractRenderEngine[Model <: AnyRef] extends RenderEngine[Model] {
  protected var update: Float => Unit = _
  protected var window: Window = _
  protected var camera: Camera = _
  protected val deltaHelper = new DeltaHelper

  override def init(window: Window, camera: Camera) {
    this.window = window
    this.camera = camera

    onInit()
  }

  override def setUpdateCallback(callback: Float => Unit) {
    update = callback
  }

  override def loop() {
    while (!window.shouldWindowClose()) {
      SystemInterface.pollEvents()

      GraphicsInterface.update()

      deltaHelper.update()

      if (update != null)
        update(deltaHelper.getDelta)

      render()

      window.refresh()
    }
  }

  protected def onInit()

  protected def render()
}
