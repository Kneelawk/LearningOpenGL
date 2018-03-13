package org.kneelawk.learningopengl.simple

import org.kneelawk.learningopengl.Camera
import org.kneelawk.learningopengl.AbstractRenderEngine

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._
import org.kneelawk.learningopengl.GraphicsInterface

class SimpleVertexEngine extends AbstractRenderEngine[SimpleVertexModel] {
  private val vertexArrayId = glGenVertexArrays()
  // bind vertex array for setup
  glBindVertexArray(vertexArrayId)

  def onInit() {
    GraphicsInterface.setBackground(0.2f, 0.2f, 0.2f, 1.0f)
  }

  def render() {
    // bind vertex array for rendering
    glBindVertexArray(vertexArrayId)

  }

  def addModel(model: SimpleVertexModel) {

  }

  def removeModel(model: SimpleVertexModel) {

  }

  def clearModels() {

  }

  def destroy() {
    glDeleteVertexArrays(vertexArrayId)
  }
}