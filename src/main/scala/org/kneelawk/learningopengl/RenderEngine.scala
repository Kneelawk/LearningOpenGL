package org.kneelawk.learningopengl

import scala.collection.mutable.HashSet
import scala.collection.mutable.HashMap
import scala.reflect.runtime.{ universe => ru }

/*
 * I wonder if you could have all the rendering code be in special
 * implicit ModelRenderers and have each one know how to render a specific kind of model?
 */

class RenderEngine {
  private val models = new HashMap[ModelRenderer[_], HashSet[AnyRef]]

  private var update: () => Unit = null
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

      if (update != null)
        update()

      for ((renderer, set) <- models) {
        renderer.render(set, camera)
      }

      window.refresh()
    }
  }

  def addModel[Model <: AnyRef: ModelRenderer](model: Model) {
    val renderer = implicitly[ModelRenderer[Model]]
    val set: HashSet[AnyRef] = {
      if (!models.contains(renderer)) {
        val s = new HashSet[AnyRef]
        models += ((renderer, s))
        s
      } else {
        models(renderer)
      }
    }

    set += model
  }

  def removeModel[Model <: AnyRef: ModelRenderer](model: Model): Boolean = {
    val renderer = implicitly[ModelRenderer[Model]]
    if (models.contains(renderer)) {
      val set = models(renderer)
      return set.remove(model)
    }
    return false
  }

  def clearModels() {
    models.clear()
  }
}