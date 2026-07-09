// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import java.awt.Dimension;
import java.awt.Point;

public final class Vector3 implements Cloneable {
    /**
     * The horizontal part of this vector
     */
    public float x;
    /**
     * The vertical part of this vector
     */
    public float y;
    public float z;

    /**
     * Create a new vector with zero length
     */
    public Vector3() {
        this.x = 0.0F;
        this.y = 0.0F;
        this.z = 0.0F;
    }

    /**
     * Create a new vector which is identical to another vector
     * 
     * @param other The Vector2 to copy
     */
    public Vector3(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     * Create a new vector with specified horizontal and vertical parts
     * 
     * @param _x The horizontal part
     * @param _y The vertical part
     * @param _z
     */
    public Vector3(float _x, float _y, float _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
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

    public void rotate(float rad) {
        double double0 = this.x * Math.cos(rad) - this.y * Math.sin(rad);
        double double1 = this.x * Math.sin(rad) + this.y * Math.cos(rad);
        this.x = (float)double0;
        this.y = (float)double1;
    }

    public void rotatey(float rad) {
        double double0 = this.x * Math.cos(rad) - this.z * Math.sin(rad);
        double double1 = this.x * Math.sin(rad) + this.z * Math.cos(rad);
        this.x = (float)double0;
        this.z = (float)double1;
    }

    /**
     * Add another vector to this one and return as a new vector
     * 
     * @param other The other Vector2 to add to this one
     * @return The result as new Vector2
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /**
     * Add another vector to this one and store the result in this one
     * 
     * @param other The other Vector2 to add to this one
     * @return This vector, with the other vector added
     */
    public Vector3 addToThis(Vector2 other) {
        this.x = this.x + other.x;
        this.y = this.y + other.y;
        return this;
    }

    public Vector3 addToThis(Vector3 other) {
        this.x = this.x + other.x;
        this.y = this.y + other.y;
        this.z = this.z + other.z;
        return this;
    }

    public Vector3 div(float scalar) {
        this.x /= scalar;
        this.y /= scalar;
        this.z /= scalar;
        return this;
    }

    /**
     * Set the direction of this vector to point to another vector, maintaining the length
     * 
     * @param other The Vector2 to point this one at.
     */
    public Vector3 aimAt(Vector2 other) {
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
     * Clone this vector
     */
    public Vector3 clone() {
        return new Vector3(this);
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

    public float dot3d(Vector3 other) {
        return this.x * other.x + this.y * other.y + this.z * other.z;
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

    /**
     * get the direction in which this vector is pointing
     * @return The direction in which this vector is pointing in radians
     */
    public float getDirection() {
        return (float)Math.atan2(this.x, this.y);
    }

    /**
     * Set the direction of this vector, maintaining the length
     * 
     * @param direction The new direction of this vector, in radians
     */
    public Vector3 setDirection(float direction) {
        this.setLengthAndDirection(direction, this.getLength());
        return this;
    }

    /**
     * get the length of this vector
     * @return The length of this vector
     */
    public float getLength() {
        float float0 = this.getLengthSq();
        return (float)Math.sqrt(float0);
    }

    /**
     * get the length squared (L^2) of this vector
     * @return The length squared of this vector
     */
    public float getLengthSq() {
        return this.x * this.x + this.y * this.y + this.z * this.z;
    }

    /**
     * Set the length of this vector, maintaining the direction
     * 
     * @param length The length of this vector
     */
    public Vector3 setLength(float length) {
        this.normalize();
        this.x *= length;
        this.y *= length;
        this.z *= length;
        return this;
    }

    public void normalize() {
        float float0 = this.getLength();
        if (float0 == 0.0F) {
            this.x = 0.0F;
            this.y = 0.0F;
            this.z = 0.0F;
        } else {
            this.x /= float0;
            this.y /= float0;
            this.z /= float0;
        }

        float0 = this.getLength();
    }

    /**
     * Make this vector identical to another vector
     * 
     * @param other The Vector2 to copy
     */
    public Vector3 set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        return this;
    }

    /**
     * Set the horizontal and vertical parts of this vector
     * 
     * @param _x The horizontal part
     * @param _y The vertical part
     * @param _z
     */
    public Vector3 set(float _x, float _y, float _z) {
        this.x = _x;
        this.y = _y;
        this.z = _z;
        return this;
    }

    /**
     * Set the length and direction of this vector
     * 
     * @param direction The direction of this vector, in radians
     * @param length The length of this vector
     */
    public Vector3 setLengthAndDirection(float direction, float length) {
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

    public Vector3 sub(Vector3 val, Vector3 out) {
        return sub(this, val, out);
    }

    public static Vector3 sub(Vector3 a, Vector3 b, Vector3 out) {
        out.set(a.x - b.x, a.y - b.y, a.z - b.z);
        return out;
    }
}
