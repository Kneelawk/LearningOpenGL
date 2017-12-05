package org.kneelawk.learningopengl

trait ModelRenderer[Model] {
  /**
   * Setup OpenGL stuff (vertex arrays, etc.)
   */
  def init()
  
  /**
   * Is this renderer initialized?
   */
  def initialized: Boolean
  
  /**
   * Called before rendering a batch of models.
   */
  def preRender()
  
  /**
   * Called for each model to render it.
   */
  def render(model: AnyRef)
  
  /**
   * Called after rendering a batch of models.
   */
  def postRender()
}