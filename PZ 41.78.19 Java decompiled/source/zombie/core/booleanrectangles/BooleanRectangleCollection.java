// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.core.booleanrectangles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.lwjgl.util.Rectangle;

public class BooleanRectangleCollection extends ArrayList<Rectangle> {
    static boolean[][] donemap = new boolean[400][400];
    private static BooleanRectangleCollection.Point intersection = new BooleanRectangleCollection.Point();
    static int retWidth = 0;
    static int retHeight = 0;

    public void doIt(ArrayList<Rectangle> arrayList1, Rectangle rectangle1) {
        ArrayList arrayList0 = new ArrayList();

        for (Rectangle rectangle0 : arrayList1) {
            ArrayList arrayList2 = this.doIt(rectangle0, rectangle1);
            arrayList0.addAll(arrayList2);
        }

        this.clear();
        this.addAll(arrayList0);
        this.optimize();
    }

    public void cutRectangle(Rectangle rectangle) {
        ArrayList arrayList = new ArrayList();
        arrayList.addAll(this);
        this.doIt(arrayList, rectangle);
    }

    public ArrayList<Rectangle> doIt(Rectangle rectangle1, Rectangle rectangle3) {
        ArrayList arrayList0 = new ArrayList();
        ArrayList arrayList1 = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        Rectangle rectangle0 = rectangle1;
        Rectangle rectangle2 = rectangle3;
        ArrayList arrayList4 = new ArrayList();
        ArrayList arrayList5 = new ArrayList();
        arrayList4.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle1.getX(), rectangle1.getY()),
                new BooleanRectangleCollection.Point(rectangle1.getX() + rectangle1.getWidth(), rectangle1.getY())
            )
        );
        arrayList4.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle1.getX() + rectangle1.getWidth(), rectangle1.getY()),
                new BooleanRectangleCollection.Point(rectangle1.getX() + rectangle1.getWidth(), rectangle1.getY() + rectangle1.getHeight())
            )
        );
        arrayList4.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle1.getX() + rectangle1.getWidth(), rectangle1.getY() + rectangle1.getHeight()),
                new BooleanRectangleCollection.Point(rectangle1.getX(), rectangle1.getY() + rectangle1.getHeight())
            )
        );
        arrayList4.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle1.getX(), rectangle1.getY() + rectangle1.getHeight()),
                new BooleanRectangleCollection.Point(rectangle1.getX(), rectangle1.getY())
            )
        );
        arrayList5.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY()),
                new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY())
            )
        );
        arrayList5.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY()),
                new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY() + rectangle3.getHeight())
            )
        );
        arrayList5.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY() + rectangle3.getHeight()),
                new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY() + rectangle3.getHeight())
            )
        );
        arrayList5.add(
            new BooleanRectangleCollection.Line(
                new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY() + rectangle3.getHeight()),
                new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY())
            )
        );

        for (int int0 = 0; int0 < arrayList4.size(); int0++) {
            for (int int1 = 0; int1 < arrayList5.size(); int1++) {
                if (this.IntesectsLine((BooleanRectangleCollection.Line)arrayList4.get(int0), (BooleanRectangleCollection.Line)arrayList5.get(int1)) != 0
                    && this.IsPointInRect(intersection.X, intersection.Y, rectangle0)) {
                    arrayList1.add(new BooleanRectangleCollection.Point(intersection.X, intersection.Y));
                }
            }
        }

        if (this.IsPointInRect(rectangle3.getX(), rectangle3.getY(), rectangle0)) {
            arrayList1.add(new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY()));
        }

        if (this.IsPointInRect(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY(), rectangle0)) {
            arrayList1.add(new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY()));
        }

        if (this.IsPointInRect(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY() + rectangle3.getHeight(), rectangle0)) {
            arrayList1.add(new BooleanRectangleCollection.Point(rectangle3.getX() + rectangle3.getWidth(), rectangle3.getY() + rectangle3.getHeight()));
        }

        if (this.IsPointInRect(rectangle3.getX(), rectangle3.getY() + rectangle3.getHeight(), rectangle0)) {
            arrayList1.add(new BooleanRectangleCollection.Point(rectangle3.getX(), rectangle3.getY() + rectangle3.getHeight()));
        }

        arrayList1.add(new BooleanRectangleCollection.Point(rectangle0.getX(), rectangle0.getY()));
        arrayList1.add(new BooleanRectangleCollection.Point(rectangle0.getX() + rectangle0.getWidth(), rectangle0.getY()));
        arrayList1.add(new BooleanRectangleCollection.Point(rectangle0.getX() + rectangle0.getWidth(), rectangle0.getY() + rectangle0.getHeight()));
        arrayList1.add(new BooleanRectangleCollection.Point(rectangle0.getX(), rectangle0.getY() + rectangle0.getHeight()));
        Collections.sort(arrayList1, new Comparator<BooleanRectangleCollection.Point>() {
            public int compare(BooleanRectangleCollection.Point point1, BooleanRectangleCollection.Point point0) {
                return point1.Y != point0.Y ? point1.Y - point0.Y : point1.X - point0.X;
            }
        });
        int int2 = ((BooleanRectangleCollection.Point)arrayList1.get(0)).X;
        int int3 = ((BooleanRectangleCollection.Point)arrayList1.get(0)).Y;
        arrayList2.add(int2);
        arrayList3.add(int3);

        for (BooleanRectangleCollection.Point point : arrayList1) {
            if (point.X > int2) {
                int2 = point.X;
                arrayList2.add(int2);
            }

            if (point.Y > int3) {
                int3 = point.Y;
                arrayList3.add(int3);
            }
        }

        for (int int4 = 0; int4 < arrayList3.size() - 1; int4++) {
            for (int int5 = 0; int5 < arrayList2.size() - 1; int5++) {
                int int6 = (Integer)arrayList2.get(int5);
                int int7 = (Integer)arrayList3.get(int4);
                int int8 = (Integer)arrayList2.get(int5 + 1) - int6;
                int int9 = (Integer)arrayList3.get(int4 + 1) - int7;
                Rectangle rectangle4 = new Rectangle(int6, int7, int8, int9);
                if (!this.Intersects(rectangle4, rectangle2)) {
                    arrayList0.add(rectangle4);
                }
            }
        }

        return arrayList0;
    }

    public void optimize() {
        ArrayList arrayList = new ArrayList();
        int int0 = 1000000;
        int int1 = 1000000;
        int int2 = -1000000;
        int int3 = -1000000;

        for (int int4 = 0; int4 < this.size(); int4++) {
            Rectangle rectangle0 = this.get(int4);
            if (rectangle0.getX() < int0) {
                int0 = rectangle0.getX();
            }

            if (rectangle0.getY() < int1) {
                int1 = rectangle0.getY();
            }

            if (rectangle0.getX() + rectangle0.getWidth() > int2) {
                int2 = rectangle0.getX() + rectangle0.getWidth();
            }

            if (rectangle0.getY() + rectangle0.getHeight() > int3) {
                int3 = rectangle0.getY() + rectangle0.getHeight();
            }
        }

        int int5 = int2 - int0;
        int int6 = int3 - int1;

        for (int int7 = 0; int7 < int5; int7++) {
            for (int int8 = 0; int8 < int6; int8++) {
                donemap[int7][int8] = true;
            }
        }

        for (int int9 = 0; int9 < this.size(); int9++) {
            Rectangle rectangle1 = this.get(int9);
            int int10 = rectangle1.getX() - int0;
            int int11 = rectangle1.getY() - int1;

            for (int int12 = 0; int12 < rectangle1.getWidth(); int12++) {
                for (int int13 = 0; int13 < rectangle1.getHeight(); int13++) {
                    donemap[int12 + int10][int13 + int11] = false;
                }
            }
        }

        for (int int14 = 0; int14 < int5; int14++) {
            for (int int15 = 0; int15 < int6; int15++) {
                if (!donemap[int14][int15]) {
                    int int16 = this.DoHeight(int14, int15, int6);
                    int int17 = this.DoWidth(int14, int15, int16, int5);

                    for (int int18 = 0; int18 < int17; int18++) {
                        for (int int19 = 0; int19 < int16; int19++) {
                            donemap[int18 + int14][int19 + int15] = true;
                        }
                    }

                    arrayList.add(new Rectangle(int14 + int0, int15 + int1, int17, int16));
                }
            }
        }

        this.clear();
        this.addAll(arrayList);
    }

    public boolean IsPointInRect(int int1, int int0, Rectangle rectangle) {
        return int1 >= rectangle.getX()
            && int1 <= rectangle.getX() + rectangle.getWidth()
            && int0 >= rectangle.getY()
            && int0 <= rectangle.getY() + rectangle.getHeight();
    }

    public int IntesectsLine(BooleanRectangleCollection.Line line0, BooleanRectangleCollection.Line line1) {
        intersection.X = 0;
        intersection.Y = 0;
        int int0 = line0.End.X - line0.Start.X;
        int int1 = line0.End.Y - line0.Start.Y;
        int int2 = line1.End.X - line1.Start.X;
        int int3 = line1.End.Y - line1.Start.Y;
        if (int0 == int2 || int1 == int3) {
            return 0;
        } else if (int1 == 0) {
            int int4 = Math.min(line0.Start.X, line0.End.X);
            int int5 = Math.max(line0.Start.X, line0.End.X);
            int int6 = Math.min(line1.Start.Y, line1.End.Y);
            int int7 = Math.max(line1.Start.Y, line1.End.Y);
            intersection.X = line1.Start.X;
            intersection.Y = line0.Start.Y;
            return 1;
        } else {
            int int8 = Math.min(line1.Start.X, line1.End.X);
            int int9 = Math.max(line1.Start.X, line1.End.X);
            int int10 = Math.min(line0.Start.Y, line0.End.Y);
            int int11 = Math.max(line0.Start.Y, line0.End.Y);
            intersection.X = line0.Start.X;
            intersection.Y = line1.Start.Y;
            return -1;
        }
    }

    public boolean Intersects(Rectangle rectangle0, Rectangle rectangle1) {
        int int0 = rectangle0.getX() + rectangle0.getWidth();
        int int1 = rectangle0.getX();
        int int2 = rectangle0.getY() + rectangle0.getHeight();
        int int3 = rectangle0.getY();
        int int4 = rectangle1.getX() + rectangle1.getWidth();
        int int5 = rectangle1.getX();
        int int6 = rectangle1.getY() + rectangle1.getHeight();
        int int7 = rectangle1.getY();
        return int0 > int5 && int2 > int7 && int1 < int4 && int3 < int6;
    }

    private int DoHeight(int int4, int int2, int int3) {
        int int0 = 0;

        for (int int1 = int2; int1 < int3; int1++) {
            if (donemap[int4][int1]) {
                return int0;
            }

            int0++;
        }

        return int0;
    }

    private int DoWidth(int int2, int int5, int int6, int int3) {
        int int0 = 0;

        for (int int1 = int2; int1 < int3; int1++) {
            for (int int4 = int5; int4 < int6; int4++) {
                if (donemap[int1][int4]) {
                    return int0;
                }
            }

            int0++;
        }

        return int0;
    }

    private void DoRect(int var1, int var2) {
    }

    public class Line {
        public BooleanRectangleCollection.Point Start = new BooleanRectangleCollection.Point();
        public BooleanRectangleCollection.Point End = new BooleanRectangleCollection.Point();

        public Line(BooleanRectangleCollection.Point point0, BooleanRectangleCollection.Point point1) {
            this.Start.X = point0.X;
            this.Start.Y = point0.Y;
            this.End.X = point1.X;
            this.End.Y = point1.Y;
        }
    }

    public static class Point {
        public int X;
        public int Y;

        public Point() {
        }

        public Point(int int0, int int1) {
            this.X = int0;
            this.Y = int1;
        }
    }
}
