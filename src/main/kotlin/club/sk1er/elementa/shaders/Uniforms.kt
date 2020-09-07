package club.sk1er.elementa.shaders

import org.lwjgl.opengl.ARBShaderObjects
import org.lwjgl.opengl.GL20
import org.lwjgl.util.vector.Vector2f
import org.lwjgl.util.vector.Vector4f

abstract class ShaderUniform<T>(val location: Int) {
    abstract fun setValue(value: T)
}

class FloatUniform(location: Int) : ShaderUniform<Float>(location) {
    override fun setValue(value: Float) {
        if (Shaders.newShaders) GL20.glUniform1f(location, value)
        else ARBShaderObjects.glUniform1fARB(location, value)
    }
}

class Vec4Uniform(location: Int) : ShaderUniform<Vector4f>(location) {
    override fun setValue(value: Vector4f) {
        if (Shaders.newShaders) GL20.glUniform4f(location, value.x, value.y, value.z, value.w)
        else ARBShaderObjects.glUniform4fARB(location, value.x, value.y, value.z, value.w)
    }
}

class Vec2Uniform(location: Int) : ShaderUniform<Vector2f>(location) {
    override fun setValue(value: Vector2f) {
        if (Shaders.newShaders) GL20.glUniform2f(location, value.x, value.y)
        else ARBShaderObjects.glUniform2fARB(location, value.x, value.y)
    }
}