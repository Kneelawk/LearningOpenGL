package org.kneelawk.learningopengl.shaders

import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL20._

import scala.collection.{TraversableOnce, mutable}
import scala.collection.mutable.HashSet

class UnlinkedShaderProgram(name: String) {
  private val components = new mutable.HashSet[ShaderComponent]

  def +=(component: ShaderComponent): components.type = add(component)

  def ++=(componentSet: TraversableOnce[ShaderComponent]): components.type = add(componentSet)

  def -=(component: ShaderComponent): components.type = remove(component)

  def add(component: ShaderComponent): components.type = components += component

  def add(componentSet: TraversableOnce[ShaderComponent]): components.type = components ++= componentSet

  def remove(component: ShaderComponent): components.type = components -= component

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

    ShaderProgram(id, name)
  }
}

case class ShaderProgram(id: Int, name: String) {
  private var deleted = false

  def delete(): Unit = if (!deleted) {
    glDeleteProgram(id)
    deleted = true
  }

  override protected def finalize(): Unit = delete()
}
