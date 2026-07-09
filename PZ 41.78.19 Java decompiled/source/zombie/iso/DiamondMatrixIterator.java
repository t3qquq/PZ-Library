// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.iso;

import org.joml.Vector2i;

public class DiamondMatrixIterator {
    private int size;
    private int lineSize;
    private int line;
    private int column;

    public DiamondMatrixIterator(int _size) {
        this.size = _size;
        this.lineSize = 1;
        this.line = 0;
        this.column = 0;
    }

    public DiamondMatrixIterator reset(int _size) {
        this.size = _size;
        this.lineSize = 1;
        this.line = 0;
        this.column = 0;
        return this;
    }

    public void reset() {
        this.lineSize = 1;
        this.line = 0;
        this.column = 0;
    }

    public boolean next(Vector2i vec) {
        if (this.lineSize == 0) {
            vec.x = 0;
            vec.y = 0;
            return false;
        } else if (this.line == 0 && this.column == 0) {
            vec.set(0, 0);
            this.column++;
            return true;
        } else {
            if (this.column < this.lineSize) {
                vec.x++;
                vec.y--;
                this.column++;
            } else {
                this.column = 1;
                this.line++;
                if (this.line < this.size) {
                    this.lineSize++;
                    vec.x = 0;
                    vec.y = this.line;
                } else {
                    this.lineSize--;
                    vec.x = this.line - this.size + 1;
                    vec.y = this.size - 1;
                }
            }

            if (this.lineSize == 0) {
                vec.x = 0;
                vec.y = 0;
                return false;
            } else {
                return true;
            }
        }
    }

    public Vector2i i2line(int a) {
        int int0 = 0;

        for (int int1 = 1; int1 < this.size + 1; int1++) {
            int0 += int1;
            if (a + 1 <= int0) {
                return new Vector2i(a - int0 + int1, int1 - 1);
            }
        }

        for (int int2 = this.size + 1; int2 < this.size * 2; int2++) {
            int0 += this.size * 2 - int2;
            if (a + 1 <= int0) {
                return new Vector2i(a - int0 + this.size * 2 - int2, int2 - 1);
            }
        }

        return null;
    }

    public Vector2i line2coord(Vector2i a) {
        if (a == null) {
            return null;
        } else if (a.y < this.size) {
            Vector2i vector2i0 = new Vector2i(0, a.y);

            for (int int0 = 0; int0 < a.x; int0++) {
                vector2i0.x++;
                vector2i0.y--;
            }

            return vector2i0;
        } else {
            Vector2i vector2i1 = new Vector2i(a.y - this.size + 1, this.size - 1);

            for (int int1 = 0; int1 < a.x; int1++) {
                vector2i1.x++;
                vector2i1.y--;
            }

            return vector2i1;
        }
    }
}
