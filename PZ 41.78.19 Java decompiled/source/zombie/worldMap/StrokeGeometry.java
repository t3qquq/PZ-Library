// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.worldMap;

import java.util.ArrayList;

public class StrokeGeometry {
    static StrokeGeometry.Point s_firstPoint = null;
    static StrokeGeometry.Point s_lastPoint = null;
    static final double EPSILON = 1.0E-4;

    static StrokeGeometry.Point newPoint(double double0, double double1) {
        if (s_firstPoint == null) {
            return new StrokeGeometry.Point(double0, double1);
        } else {
            StrokeGeometry.Point point = s_firstPoint;
            s_firstPoint = s_firstPoint.next;
            if (s_lastPoint == point) {
                s_lastPoint = null;
            }

            point.next = null;
            return point.set(double0, double1);
        }
    }

    static void release(StrokeGeometry.Point point) {
        if (point.next == null && point != s_lastPoint) {
            point.next = s_firstPoint;
            s_firstPoint = point;
            if (s_lastPoint == null) {
                s_lastPoint = point;
            }
        }
    }

    static void release(ArrayList<StrokeGeometry.Point> arrayList) {
        for (int int0 = 0; int0 < arrayList.size(); int0++) {
            release((StrokeGeometry.Point)arrayList.get(int0));
        }
    }

    static ArrayList<StrokeGeometry.Point> getStrokeGeometry(StrokeGeometry.Point[] points, StrokeGeometry.Attrs attrs) {
        if (points.length < 2) {
            return null;
        } else {
            String string0 = attrs.cap;
            String string1 = attrs.join;
            float float0 = attrs.width / 2.0F;
            float float1 = attrs.miterLimit;
            ArrayList arrayList0 = new ArrayList();
            ArrayList arrayList1 = new ArrayList();
            boolean boolean0 = false;
            if (points.length == 2) {
                string1 = "bevel";
                createTriangles(points[0], StrokeGeometry.Point.Middle(points[0], points[1]), points[1], arrayList0, float0, string1, float1);
            } else {
                for (int int0 = 0; int0 < points.length - 1; int0++) {
                    if (int0 == 0) {
                        arrayList1.add(points[0]);
                    } else if (int0 == points.length - 2) {
                        arrayList1.add(points[points.length - 1]);
                    } else {
                        arrayList1.add(StrokeGeometry.Point.Middle(points[int0], points[int0 + 1]));
                    }
                }

                for (int int1 = 1; int1 < arrayList1.size(); int1++) {
                    createTriangles(
                        (StrokeGeometry.Point)arrayList1.get(int1 - 1),
                        points[int1],
                        (StrokeGeometry.Point)arrayList1.get(int1),
                        arrayList0,
                        float0,
                        string1,
                        float1
                    );
                }
            }

            if (!boolean0) {
                if (string0.equals("round")) {
                    StrokeGeometry.Point point0 = (StrokeGeometry.Point)arrayList0.get(0);
                    StrokeGeometry.Point point1 = (StrokeGeometry.Point)arrayList0.get(1);
                    StrokeGeometry.Point point2 = points[1];
                    StrokeGeometry.Point point3 = (StrokeGeometry.Point)arrayList0.get(arrayList0.size() - 1);
                    StrokeGeometry.Point point4 = (StrokeGeometry.Point)arrayList0.get(arrayList0.size() - 3);
                    StrokeGeometry.Point point5 = points[points.length - 2];
                    createRoundCap(points[0], point0, point1, point2, arrayList0);
                    createRoundCap(points[points.length - 1], point3, point4, point5, arrayList0);
                } else if (string0.equals("square")) {
                    StrokeGeometry.Point point6 = (StrokeGeometry.Point)arrayList0.get(arrayList0.size() - 1);
                    StrokeGeometry.Point point7 = (StrokeGeometry.Point)arrayList0.get(arrayList0.size() - 3);
                    createSquareCap(
                        (StrokeGeometry.Point)arrayList0.get(0),
                        (StrokeGeometry.Point)arrayList0.get(1),
                        StrokeGeometry.Point.Sub(points[0], points[1])
                            .normalize()
                            .scalarMult(StrokeGeometry.Point.Sub(points[0], (StrokeGeometry.Point)arrayList0.get(0)).length()),
                        arrayList0
                    );
                    createSquareCap(
                        point6,
                        point7,
                        StrokeGeometry.Point.Sub(points[points.length - 1], points[points.length - 2])
                            .normalize()
                            .scalarMult(StrokeGeometry.Point.Sub(point7, points[points.length - 1]).length()),
                        arrayList0
                    );
                }
            }

            return arrayList0;
        }
    }

    static void createSquareCap(
        StrokeGeometry.Point point0, StrokeGeometry.Point point2, StrokeGeometry.Point point1, ArrayList<StrokeGeometry.Point> arrayList
    ) {
        arrayList.add(point0);
        arrayList.add(StrokeGeometry.Point.Add(point0, point1));
        arrayList.add(StrokeGeometry.Point.Add(point2, point1));
        arrayList.add(point2);
        arrayList.add(StrokeGeometry.Point.Add(point2, point1));
        arrayList.add(point0);
    }

    static void createRoundCap(
        StrokeGeometry.Point point0,
        StrokeGeometry.Point point1,
        StrokeGeometry.Point point2,
        StrokeGeometry.Point point4,
        ArrayList<StrokeGeometry.Point> arrayList
    ) {
        double double0 = StrokeGeometry.Point.Sub(point0, point1).length();
        double double1 = Math.atan2(point2.y - point0.y, point2.x - point0.x);
        double double2 = Math.atan2(point1.y - point0.y, point1.x - point0.x);
        double double3 = double1;
        if (double2 > double1) {
            if (double2 - double1 >= 3.141492653589793) {
                double2 -= Math.PI * 2;
            }
        } else if (double1 - double2 >= 3.141492653589793) {
            double1 -= Math.PI * 2;
        }

        double double4 = double2 - double1;
        if (Math.abs(double4) >= 3.141492653589793 && Math.abs(double4) <= 3.1416926535897933) {
            StrokeGeometry.Point point3 = StrokeGeometry.Point.Sub(point0, point4);
            if (point3.x == 0.0) {
                if (point3.y > 0.0) {
                    double4 = -double4;
                }
            } else if (point3.x >= -1.0E-4) {
                double4 = -double4;
            }
        }

        int int0 = (int)(Math.abs(double4 * double0) / 7.0);
        int0++;
        double double5 = double4 / int0;

        for (int int1 = 0; int1 < int0; int1++) {
            arrayList.add(newPoint(point0.x, point0.y));
            arrayList.add(newPoint(point0.x + double0 * Math.cos(double3 + double5 * int1), point0.y + double0 * Math.sin(double3 + double5 * int1)));
            arrayList.add(
                newPoint(point0.x + double0 * Math.cos(double3 + double5 * (1 + int1)), point0.y + double0 * Math.sin(double3 + double5 * (1 + int1)))
            );
        }
    }

    static double signedArea(StrokeGeometry.Point point0, StrokeGeometry.Point point1, StrokeGeometry.Point point2) {
        return (point1.x - point0.x) * (point2.y - point0.y) - (point2.x - point0.x) * (point1.y - point0.y);
    }

    static StrokeGeometry.Point lineIntersection(
        StrokeGeometry.Point point0, StrokeGeometry.Point point1, StrokeGeometry.Point point2, StrokeGeometry.Point point3
    ) {
        double double0 = point1.y - point0.y;
        double double1 = point0.x - point1.x;
        double double2 = point3.y - point2.y;
        double double3 = point2.x - point3.x;
        double double4 = double0 * double3 - double2 * double1;
        if (double4 > -1.0E-4 && double4 < 1.0E-4) {
            return null;
        } else {
            double double5 = double0 * point0.x + double1 * point0.y;
            double double6 = double2 * point2.x + double3 * point2.y;
            double double7 = (double3 * double5 - double1 * double6) / double4;
            double double8 = (double0 * double6 - double2 * double5) / double4;
            return newPoint(double7, double8);
        }
    }

    static void createTriangles(
        StrokeGeometry.Point point2,
        StrokeGeometry.Point point1,
        StrokeGeometry.Point point4,
        ArrayList<StrokeGeometry.Point> arrayList,
        float float0,
        String string,
        float float1
    ) {
        StrokeGeometry.Point point0 = StrokeGeometry.Point.Sub(point1, point2);
        StrokeGeometry.Point point3 = StrokeGeometry.Point.Sub(point4, point1);
        point0.perpendicular();
        point3.perpendicular();
        if (signedArea(point2, point1, point4) > 0.0) {
            point0.invert();
            point3.invert();
        }

        point0.normalize();
        point3.normalize();
        point0.scalarMult(float0);
        point3.scalarMult(float0);
        StrokeGeometry.Point point5 = lineIntersection(
            StrokeGeometry.Point.Add(point0, point2),
            StrokeGeometry.Point.Add(point0, point1),
            StrokeGeometry.Point.Add(point3, point4),
            StrokeGeometry.Point.Add(point3, point1)
        );
        StrokeGeometry.Point point6 = null;
        double double0 = Double.MAX_VALUE;
        if (point5 != null) {
            point6 = StrokeGeometry.Point.Sub(point5, point1);
            double0 = point6.length();
        }

        double double1 = (int)(double0 / float0);
        StrokeGeometry.Point point7 = StrokeGeometry.Point.Sub(point2, point1);
        double double2 = point7.length();
        StrokeGeometry.Point point8 = StrokeGeometry.Point.Sub(point1, point4);
        double double3 = point8.length();
        if (!(double0 > double2) && !(double0 > double3)) {
            arrayList.add(StrokeGeometry.Point.Add(point2, point0));
            arrayList.add(StrokeGeometry.Point.Sub(point2, point0));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point6));
            arrayList.add(StrokeGeometry.Point.Add(point2, point0));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point6));
            arrayList.add(StrokeGeometry.Point.Add(point1, point0));
            if (string.equals("round")) {
                StrokeGeometry.Point point9 = StrokeGeometry.Point.Add(point1, point0);
                StrokeGeometry.Point point10 = StrokeGeometry.Point.Add(point1, point3);
                StrokeGeometry.Point point11 = StrokeGeometry.Point.Sub(point1, point6);
                arrayList.add(point9);
                arrayList.add(point1);
                arrayList.add(point11);
                createRoundCap(point1, point9, point10, point11, arrayList);
                arrayList.add(point1);
                arrayList.add(point10);
                arrayList.add(point11);
            } else {
                if (string.equals("bevel") || string.equals("miter") && double1 >= float1) {
                    arrayList.add(StrokeGeometry.Point.Add(point1, point0));
                    arrayList.add(StrokeGeometry.Point.Add(point1, point3));
                    arrayList.add(StrokeGeometry.Point.Sub(point1, point6));
                }

                if (string.equals("miter") && double1 < float1) {
                    arrayList.add(point5);
                    arrayList.add(StrokeGeometry.Point.Add(point1, point0));
                    arrayList.add(StrokeGeometry.Point.Add(point1, point3));
                }
            }

            arrayList.add(StrokeGeometry.Point.Add(point4, point3));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point6));
            arrayList.add(StrokeGeometry.Point.Add(point1, point3));
            arrayList.add(StrokeGeometry.Point.Add(point4, point3));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point6));
            arrayList.add(StrokeGeometry.Point.Sub(point4, point3));
        } else {
            arrayList.add(StrokeGeometry.Point.Add(point2, point0));
            arrayList.add(StrokeGeometry.Point.Sub(point2, point0));
            arrayList.add(StrokeGeometry.Point.Add(point1, point0));
            arrayList.add(StrokeGeometry.Point.Sub(point2, point0));
            arrayList.add(StrokeGeometry.Point.Add(point1, point0));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point0));
            if (string.equals("round")) {
                createRoundCap(point1, StrokeGeometry.Point.Add(point1, point0), StrokeGeometry.Point.Add(point1, point3), point4, arrayList);
            } else if (!string.equals("bevel") && (!string.equals("miter") || !(double1 >= float1))) {
                if (string.equals("miter") && double1 < float1 && point5 != null) {
                    arrayList.add(StrokeGeometry.Point.Add(point1, point0));
                    arrayList.add(point1);
                    arrayList.add(point5);
                    arrayList.add(StrokeGeometry.Point.Add(point1, point3));
                    arrayList.add(point1);
                    arrayList.add(point5);
                }
            } else {
                arrayList.add(point1);
                arrayList.add(StrokeGeometry.Point.Add(point1, point0));
                arrayList.add(StrokeGeometry.Point.Add(point1, point3));
            }

            arrayList.add(StrokeGeometry.Point.Add(point4, point3));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point3));
            arrayList.add(StrokeGeometry.Point.Add(point1, point3));
            arrayList.add(StrokeGeometry.Point.Add(point4, point3));
            arrayList.add(StrokeGeometry.Point.Sub(point1, point3));
            arrayList.add(StrokeGeometry.Point.Sub(point4, point3));
        }
    }

    static class Attrs {
        String cap = "butt";
        String join = "bevel";
        float width = 1.0F;
        float miterLimit = 10.0F;
    }

    public static final class Point {
        double x;
        double y;
        StrokeGeometry.Point next;

        Point() {
            this.x = 0.0;
            this.y = 0.0;
        }

        Point(double double0, double double1) {
            this.x = double0;
            this.y = double1;
        }

        StrokeGeometry.Point set(double double0, double double1) {
            this.x = double0;
            this.y = double1;
            return this;
        }

        StrokeGeometry.Point scalarMult(double double0) {
            this.x *= double0;
            this.y *= double0;
            return this;
        }

        StrokeGeometry.Point perpendicular() {
            double double0 = this.x;
            this.x = -this.y;
            this.y = double0;
            return this;
        }

        StrokeGeometry.Point invert() {
            this.x = -this.x;
            this.y = -this.y;
            return this;
        }

        double length() {
            return Math.sqrt(this.x * this.x + this.y * this.y);
        }

        StrokeGeometry.Point normalize() {
            double double0 = this.length();
            this.x /= double0;
            this.y /= double0;
            return this;
        }

        double angle() {
            return this.y / this.x;
        }

        static double Angle(StrokeGeometry.Point point0, StrokeGeometry.Point point1) {
            return Math.atan2(point1.x - point0.x, point1.y - point0.y);
        }

        static StrokeGeometry.Point Add(StrokeGeometry.Point point1, StrokeGeometry.Point point0) {
            return StrokeGeometry.newPoint(point1.x + point0.x, point1.y + point0.y);
        }

        static StrokeGeometry.Point Sub(StrokeGeometry.Point point1, StrokeGeometry.Point point0) {
            return StrokeGeometry.newPoint(point1.x - point0.x, point1.y - point0.y);
        }

        static StrokeGeometry.Point Middle(StrokeGeometry.Point point0, StrokeGeometry.Point point1) {
            return Add(point0, point1).scalarMult(0.5);
        }
    }
}
