package org.kneelawk.learningopengl

import org.kneelawk.learningopengl.util.DeltaHelper

/*
 * I wonder if you could have all the rendering code be in special
 * implicit ModelRenderers and have each one know how to render a specific kind of model?
 * 
 * No, the functionality required to keep track of models requires the ModelRenderer's api
 * to effectively become that of the RenderEngine.
 */

abstract class AbstractRenderEngine[Model <: AnyRef](protected val window: Window, protected val camera: Camera, protected val update: Float => Unit) extends RenderEngine[Model] {
  protected val deltaHelper = new DeltaHelper

  override def loop() {
    while (!window.shouldWindowClose()) {
      SystemInterface.pollEvents()

      deltaHelper.update()

      update(deltaHelper.getDelta)

      render()

      window.refresh()
    }
  }

  protected def render()
}
