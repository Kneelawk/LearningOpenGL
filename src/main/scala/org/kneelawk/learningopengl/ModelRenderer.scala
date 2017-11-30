package org.kneelawk.learningopengl

trait ModelRenderer[Model] {
  def initialized: Boolean
  
  def render(model: Model)
}