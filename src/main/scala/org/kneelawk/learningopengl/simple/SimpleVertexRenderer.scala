package org.kneelawk.learningopengl.simple

import org.kneelawk.learningopengl.Camera
import org.kneelawk.learningopengl.ModelRenderer

import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL30._

class SimpleVertexRenderer extends ModelRenderer[SimpleVertexModel] {
  private val vertexArrayId = glGenVertexArrays()
  // bind vertex array for setup
  glBindVertexArray(vertexArrayId)

  def render(models: Iterable[AnyRef], camera: Camera) {
    val ms = models.asInstanceOf[Iterable[SimpleVertexModel]]

    // bind vertex array for rendering
    glBindVertexArray(vertexArrayId)

  }

  def destroy() {
    glDeleteVertexArrays(vertexArrayId)
  }
}