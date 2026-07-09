// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.awt.Dimension;
import java.awt.Point;
import zombie.core.math.PZMath;

public final class Vector2 implements Cloneable {
    /**
     * The horizontal part of this vector
     */
    public float x;
    /**
     * The vertical part of this vector
     */
    public float y;

    /**
     * Create a new vector with zero length
     */
    public Vector2() {
        this.x = 0.0F;
        this.y = 0.0F;
    }

    /**
     * Create a new vector which is identical to another vector
     * 
     * @param other The Vector2 to copy
     */
    public Vector2(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }

    /**
     * Create a new vector with specified horizontal and vertical parts
     * 
     * @param _x The horizontal part
     * @param _y The vertical part
     */
    public Vector2(float _x, float _y) {
        this.x = _x;
        this.y = _y;
    }

    /**
     * Create a new vector from an AWT Point
     * 
     * @param p The java.awt.Point to convert
     * @return A new Vector2 representing the Point
     */
    public static Vector2 fromAwtPoint(Point p) {
        return new Vector2(p.x, p.y);
    }

    /**
     * Create a new vector with a specified length and direction
     * 
     * @param length The length of the new vector
     * @param direction The direction of the new vector, in radians
     */
    public static Vector2 fromLengthDirection(float length, float direction) {
        Vector2 vector = new Vector2();
        vector.setLengthAndDirection(direction, length);
        return vector;
    }

    public static float dot(float _x, float _y, float tx, float ty) {
        return _x * tx + _y * ty;
    }

    /**
     * Result = a + b * scale
     * @return The supplied result vector.
     */
    public static Vector2 addScaled(Vector2 a, Vector2 b, float scale, Vector2 result) {
        result.set(a.x + b.x * scale, a.y + b.y * scale);
        return result;
    }

    public void rotate(float rad) {
        double double0 = this.x * Math.cos(rad) - this.y * Math.sin(rad);
        double double1 = this.x * Math.sin(rad) + this.y * Math.cos(rad);
        this.x = (float)double0;
        this.y = (float)double1;
    }

    /**
     * Add another vector to this one and return this
     * 
     * @param other The other Vector2 to add to this one
     * @return this
     */
    public Vector2 add(Vector2 other) {
        this.x = this.x + other.x;
        this.y = this.y + other.y;
        return this;
    }

    /**
     * Set the direction of this vector to point to another vector, maintaining the length
     * 
     * @param other The Vector2 to point this one at.
     */
    public Vector2 aimAt(Vector2 other) {
        this.setLengthAndDirection(this.angleTo(other), this.getLength());
        return this;
    }

    /**
     * Calculate the angle between this point and another
     * 
     * @param other The second point as vector
     * @return The angle between them, in radians
     */
    public float angleTo(Vector2 other) {
        return (float)Math.atan2(other.y - this.y, other.x - this.x);
    }

    /**
     * Calculate angle between this and other vectors
     * 
     * @param other The other vector
     * @return The angle in radians in the range [0,PI]
     */
    public float angleBetween(Vector2 other) {
        float float0 = this.dot(other) / (this.getLength() * other.getLength());
        if (float0 < -1.0F) {
            float0 = -1.0F;
        }

        if (float0 > 1.0F) {
            float0 = 1.0F;
        }

        return (float)Math.acos(float0);
    }

    /**
     * Clone this vector
     */
    public Vector2 clone() {
        return new Vector2(this);
    }

    /**
     * Calculate the distance between this point and another
     * 
     * @param other The second point as vector
     * @return The distance between them
     */
    public float distanceTo(Vector2 other) {
        return (float)Math.sqrt(Math.pow(other.x - this.x, 2.0) + Math.pow(other.y - this.y, 2.0));
    }

    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * See if this vector is equal to another
     * 
     * @param other A Vector2 to compare this one to
     * @return true if other is a Vector2 equal to this one
     */
    @Override
    public boolean equals(Object other) {
        return !(other instanceof Vector2 vector) ? false : vector.x == this.x && vector.y == this.y;
    }

    public float getDirection() {
        float float0 = (float)Math.atan2(this.y, this.x);
        return PZMath.wrap(float0, (float) -Math.PI, (float) Math.PI);
    }

    public static float getDirection(float _x, float _y) {
        float float0 = (float)Math.atan2(_y, _x);
        return PZMath.wrap(float0, (float) -Math.PI, (float) Math.PI);
    }

    /**
     * get the direction in which this vector is pointing
     * @return The direction in which this vector is pointing in radians
     */
    @Deprecated
    public float getDirectionNeg() {
        return (float)Math.atan2(this.x, this.y);
    }

    /**
     * Set the direction of this vector, maintaining the length
     * 
     * @param direction The new direction of this vector, in radians
     */
    public Vector2 setDirection(float direction) {
        this.setLengthAndDirection(direction, this.getLength());
        return this;
    }

    /**
     * get the length of this vector
     * @return The length of this vector
     */
    public float getLength() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /**
     * get the squared length of this vector
     * @return The squared length of this vector
     */
    public float getLengthSquared() {
        return this.x * this.x + this.y * this.y;
    }

    /**
     * Set the length of this vector, maintaining the direction
     * 
     * @param length The length of this vector
     */
    public Vector2 setLength(float length) {
        this.normalize();
        this.x *= length;
        this.y *= length;
        return this;
    }

    public float normalize() {
        float float0 = this.getLength();
        if (float0 == 0.0F) {
            this.x = 0.0F;
            this.y = 0.0F;
        } else {
            this.x /= float0;
            this.y /= float0;
        }

        return float0;
    }

    /**
     * Make this vector identical to another vector
     * 
     * @param other The Vector2 to copy
     */
    public Vector2 set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
        return this;
    }

    /**
     * Set the horizontal and vertical parts of this vector
     * 
     * @param _x The horizontal part
     * @param _y The vertical part
     */
    public Vector2 set(float _x, float _y) {
        this.x = _x;
        this.y = _y;
        return this;
    }

    /**
     * Set the length and direction of this vector
     * 
     * @param direction The direction of this vector, in radians
     * @param length The length of this vector
     */
    public Vector2 setLengthAndDirection(float direction, float length) {
        this.x = (float)(Math.cos(direction) * length);
        this.y = (float)(Math.sin(direction) * length);
        return this;
    }

    /**
     * Convert this vector to an AWT Dimension
     * @return a java.awt.Dimension
     */
    public Dimension toAwtDimension() {
        return new Dimension((int)this.x, (int)this.y);
    }

    /**
     * Convert this vector to an AWT Point
     * @return a java.awt.Point
     */
    public Point toAwtPoint() {
        return new Point((int)this.x, (int)this.y);
    }

    @Override
    public String toString() {
        return String.format("Vector2 (X: %f, Y: %f) (L: %f, D:%f)", this.x, this.y, this.getLength(), this.getDirection());
    }

    /**
     * @return the x
     */
    public float getX() {
        return this.x;
    }

    /**
     * 
     * @param _x the x to set
     */
    public void setX(float _x) {
        this.x = _x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return this.y;
    }

    /**
     * 
     * @param _y the y to set
     */
    public void setY(float _y) {
        this.y = _y;
    }

    public void tangent() {
        double double0 = this.x * Math.cos(Math.toRadians(90.0)) - this.y * Math.sin(Math.toRadians(90.0));
        double double1 = this.x * Math.sin(Math.toRadians(90.0)) + this.y * Math.cos(Math.toRadians(90.0));
        this.x = (float)double0;
        this.y = (float)double1;
    }

    public void scale(float scale) {
        scale(this, scale);
    }

    public static Vector2 scale(Vector2 val, float scale) {
        val.x *= scale;
        val.y *= scale;
        return val;
    }
}
