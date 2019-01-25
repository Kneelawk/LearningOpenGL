package org.kneelawk.learningopengl.simple

import org.kneelawk.learningopengl.buffers.GLArrayBuffer
import org.kneelawk.learningopengl.{AbstractRenderEngine, GraphicsInterface}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL30.{glBindVertexArray, glDeleteVertexArrays, glGenVertexArrays}

import scala.collection.mutable
import scala.collection.mutable.{HashMap, ListBuffer}

class SimpleVertexEngine extends AbstractRenderEngine[SimpleVertexModel] {
  // setup clear color
  GraphicsInterface.setBackground(0.2f, 0.2f, 0.2f, 1.0f)

  // generate the vertex arrays
  private val vertexArrayId = glGenVertexArrays()
  // bind vertex array for setup
  glBindVertexArray(vertexArrayId)

  private val vertices = new GLArrayBuffer
  private val matrices = new GLArrayBuffer
  private val indecies = new mutable.HashMap[SimpleVertexModel, SimpleVertexModelIndex]
  private val indexList = new ListBuffer[SimpleVertexModelIndex]

  def onInit() {
  }

  def render() {
    // bind vertex array for rendering
    glBindVertexArray(vertexArrayId)

  }

  def addModel(model: SimpleVertexModel) {
    if (indecies.contains(model)) {
      return
    }

    val index = SimpleVertexModelIndex(vertices.getSize, matrices.getSize)

    val vertbuf = BufferUtils.createFloatBuffer(model.vertexData.length)
    vertbuf.put(model.vertexData)
    vertbuf.flip()
    vertices.append(vertbuf)

    val matBuf = BufferUtils.createFloatBuffer(16)
    model.model.get(matBuf)
    matBuf.flip()
    matrices.append(matBuf)

    indecies += ((model, index))
    indexList += index
  }

  def removeModel(model: SimpleVertexModel) {
    if (!indecies.contains(model)) {
      return
    }

    val index = indecies(model)
    val vertLen = model.vertexData.length << 2
    val matLen = 16 << 2

    vertices.remove(index.vertex, vertLen)
    matrices.remove(index.matrix, matLen)

    indexList.slice(indexList.indexOf(index) + 1, indexList.size).foreach({ f =>
      f.vertex -= vertLen
      f.matrix -= matLen
    })

    indexList -= index
  }

  def clearModels() {
    vertices.clear()
    matrices.clear()
    indecies.clear()
    indexList.clear()
  }

  def destroy() {
    glDeleteVertexArrays(vertexArrayId)
    vertices.destroy()
    matrices.destroy()
  }
}
