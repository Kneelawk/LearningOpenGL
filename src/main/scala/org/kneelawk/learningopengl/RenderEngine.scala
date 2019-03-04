package org.kneelawk.learningopengl

trait RenderEngine[Model <: AnyRef] {
  def loop()

  def addModel(model: Model)

  def removeModel(model: Model)

  def clearModels()

  def destroy()
}