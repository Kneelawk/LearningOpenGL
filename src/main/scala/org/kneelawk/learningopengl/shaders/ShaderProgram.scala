package org.kneelawk.learningopengl.shaders

import scala.collection.mutable.HashSet

import org.lwjgl.opengl.GL20._
import org.lwjgl.opengl.GL11._
import org.lwjgl.BufferUtils
import scala.collection.TraversableOnce

class UnlinkedShaderProgram(name: String) {
  private val components = new HashSet[ShaderComponent]

  def +=(component: ShaderComponent) = add(component)

  def ++=(componentSet: TraversableOnce[ShaderComponent]) = add(componentSet)

  def -=(component: ShaderComponent) = remove(component)

  def add(component: ShaderComponent) = components += component

  def add(componentSet: TraversableOnce[ShaderComponent]) = components ++= componentSet

  def remove(component: ShaderComponent) = components -= component

  @throws[ProgramLinkException]("if there is an error while linking this shader program")
  def link(): ShaderProgram = {
    val id = glCreateProgram()

    components.foreach(s => glAttachShader(id, s.id))

    val res = BufferUtils.createIntBuffer(1)

    println(s"Linking shader program: $name")
    glLinkProgram(id)

    components.foreach(s => glDetachShader(id, s.id))

    glGetProgramiv(id, GL_LINK_STATUS, res)
    val log = glGetProgramInfoLog(id)
    if (log != null && log != "") {
      println(log)
    }
    val status = res.get
    if (status != GL_TRUE) {
      glDeleteProgram(id)
      Console.err.println(s"Error linking shader program: $name")
      throw new ProgramLinkException(s"Error linking shader program: $name, log: $log, status: $status")
    }

    return new ShaderProgram(id, name)
  }
}

case class ShaderProgram(id: Int, name: String) {
  def delete() = glDeleteProgram(id)

  override protected def finalize() = delete()
}
