package org.kneelawk.learningopengl.shaders

import org.lwjgl.opengl.GL20

object ShaderType {
  object Vertex extends ShaderType(GL20.GL_VERTEX_SHADER)
  object Fragment extends ShaderType(GL20.GL_FRAGMENT_SHADER)
}

sealed abstract class ShaderType(typeId: Int) {
  def getTypeId = typeId
}