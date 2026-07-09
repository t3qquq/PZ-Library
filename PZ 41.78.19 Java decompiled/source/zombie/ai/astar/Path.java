// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie.ai.astar;

import java.util.ArrayList;
import java.util.Stack;

/**
 * A path determined by some path finding algorithm. A series of steps from  the starting location to the target location. This includes a step for the  initial location.
 */
public class Path {
    private ArrayList<Path.Step> steps = new ArrayList<>();
    public float cost = 0.0F;
    public static Stack<Path.Step> stepstore = new Stack<>();
    static Path.Step containsStep = new Path.Step();

    public float costPerStep() {
        return this.steps.isEmpty() ? this.cost : this.cost / this.steps.size();
    }

    /**
     * Append a step to the path.
     * 
     * @param x The x coordinate of the new step
     * @param y The y coordinate of the new step
     * @param z
     */
    public void appendStep(int x, int y, int z) {
        Path.Step step = null;
        step = new Path.Step();
        step.x = x;
        step.y = y;
        step.z = z;
        this.steps.add(step);
    }

    public boolean contains(int x, int y, int z) {
        containsStep.x = x;
        containsStep.y = y;
        containsStep.z = z;
        return this.steps.contains(containsStep);
    }

    /**
     * get the length of the path, i.e. the number of steps
     * @return The number of steps in this path
     */
    public int getLength() {
        return this.steps.size();
    }

    /**
     * get the step at a given index in the path
     * 
     * @param index The index of the step to retrieve. Note this should  be >= 0 and < getLength();
     * @return The step information, the position on the map.
     */
    public Path.Step getStep(int index) {
        return this.steps.get(index);
    }

    /**
     * get the x coordinate for the step at the given index
     * 
     * @param index The index of the step whose x coordinate should be retrieved
     * @return The x coordinate at the step
     */
    public int getX(int index) {
        return this.getStep(index).x;
    }

    /**
     * get the y coordinate for the step at the given index
     * 
     * @param index The index of the step whose y coordinate should be retrieved
     * @return The y coordinate at the step
     */
    public int getY(int index) {
        return this.getStep(index).y;
    }

    public int getZ(int index) {
        return this.getStep(index).z;
    }

    public static Path.Step createStep() {
        if (stepstore.isEmpty()) {
            for (int int0 = 0; int0 < 200; int0++) {
                Path.Step step = new Path.Step();
                stepstore.push(step);
            }
        }

        return stepstore.push(containsStep);
    }

    /**
     * Prepend a step to the path.
     * 
     * @param x The x coordinate of the new step
     * @param y The y coordinate of the new step
     * @param z
     */
    public void prependStep(int x, int y, int z) {
        Path.Step step = null;
        step = new Path.Step();
        step.x = x;
        step.y = y;
        step.z = z;
        this.steps.add(0, step);
    }

    /**
     * A single step within the path
     */
    public static class Step {
        /**
         * The x coordinate at the given step
         */
        public int x;
        /**
         * The y coordinate at the given step
         */
        public int y;
        public int z;

        /**
         * Create a new step
         * 
         * @param _x The x coordinate of the new step
         * @param _y The y coordinate of the new step
         * @param _z
         */
        public Step(int _x, int _y, int _z) {
            this.x = _x;
            this.y = _y;
            this.z = _z;
        }

        public Step() {
        }

        @Override
        public boolean equals(Object other) {
            return !(other instanceof Path.Step step) ? false : step.x == this.x && step.y == this.y && step.z == this.z;
        }

        /**
         * get the x coordinate of the new step
         * @return The x coodindate of the new step
         */
        public int getX() {
            return this.x;
        }

        /**
         * get the y coordinate of the new step
         * @return The y coodindate of the new step
         */
        public int getY() {
            return this.y;
        }

        public int getZ() {
            return this.z;
        }

        @Override
        public int hashCode() {
            return this.x * this.y * this.z;
        }
    }
}
