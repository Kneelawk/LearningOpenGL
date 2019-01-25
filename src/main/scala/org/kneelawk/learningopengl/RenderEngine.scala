package org.kneelawk.learningopengl

trait RenderEngine[Model <: AnyRef] {
  def init(window: Window, camera: Camera)

  def setUpdateCallback(callback: () => Unit)

  def loop()

  def addModel(model: Model)

  def removeModel(model: Model)

  def clearModels()

  def destroy()
}