package org.kneelawk.learningopengl.simple

import scala.collection.mutable.HashMap

import org.kneelawk.learningopengl.AbstractRenderEngine
import org.kneelawk.learningopengl.GLArrayBuffer
import org.kneelawk.learningopengl.GraphicsInterface
import org.lwjgl.opengl.GL30.glBindVertexArray
import org.lwjgl.opengl.GL30.glDeleteVertexArrays
import org.lwjgl.opengl.GL30.glGenVertexArrays

class SimpleVertexEngine extends AbstractRenderEngine[SimpleVertexModel] {
  // setup clear color
  GraphicsInterface.setBackground(0.2f, 0.2f, 0.2f, 1.0f)

  // generate the vertex arrays
  private val vertexArrayId = glGenVertexArrays()
  // bind vertex array for setup
  glBindVertexArray(vertexArrayId)

  private val vertices = new GLArrayBuffer
  private val matrices = new GLArrayBuffer
  private val indecies = new HashMap[SimpleVertexModel, SimpleVertexModelIndex]

  def onInit() {
  }

  def render() {
    // bind vertex array for rendering
    glBindVertexArray(vertexArrayId)

  }

  def addModel(model: SimpleVertexModel) {
    val index = SimpleVertexModelIndex(vertices.getSize, matrices.getSize)
  }

  def removeModel(model: SimpleVertexModel) {

  }

  def clearModels() {

  }

  def destroy() {
    glDeleteVertexArrays(vertexArrayId)
  }
}
