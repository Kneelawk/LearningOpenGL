package org.kneelawk.learningopengl.shaders

import java.io.InputStream

import org.kneelawk.learningopengl.ResourceUtil
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11.GL_TRUE
import org.lwjgl.opengl.GL20.GL_COMPILE_STATUS
import org.lwjgl.opengl.GL20.glCompileShader
import org.lwjgl.opengl.GL20.glCreateShader
import org.lwjgl.opengl.GL20.glDeleteShader
import org.lwjgl.opengl.GL20.glGetShaderInfoLog
import org.lwjgl.opengl.GL20.glGetShaderiv
import org.lwjgl.opengl.GL20.glShaderSource

class ShaderComponentSource(source: String, name: String, tpe: ShaderType) {
  def this(is: InputStream, name: String, tpe: ShaderType) = {
    this(ResourceUtil.readString(is), name, tpe)
  }

  @throws[FileCompileException]("if there is an error while compiling this shader file")
  def compile(): ShaderComponent = {
    val id = glCreateShader(tpe.getTypeId)

    val res = BufferUtils.createIntBuffer(1)

    glShaderSource(id, source)

    println(s"Compiling shader: $name")
    glCompileShader(id)

    glGetShaderiv(id, GL_COMPILE_STATUS, res)
    val log = glGetShaderInfoLog(id)
    if (log != null && log != "") {
      println(log)
    }
    val status = res.get
    if (status != GL_TRUE) {
      glDeleteShader(id)
      Console.err.println(s"Error compiling shader: $name")
      throw new FileCompileException(s"Error compiling shader: $name, log: $log, status: $status")
    }

    return new ShaderComponent(id, name, tpe)
  }
}

case class ShaderComponent(id: Int, name: String, tpe: ShaderType) {
  def delete() = glDeleteShader(id)
  
  override protected def finalize() = delete()
}
