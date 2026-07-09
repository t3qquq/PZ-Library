// Decompiled on 월 6월 15 10:24:39 KST 2026 with Zomboid Decompiler v0.2.3 using Vineflower.
package zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import zombie.characters.IsoSurvivor;
import zombie.characters.ZombieFootstepManager;
import zombie.characters.ZombieThumpManager;
import zombie.characters.ZombieVocalsManager;
import zombie.core.collision.Polygon;
import zombie.core.profiling.PerformanceProfileProbe;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;

public final class CollisionManager {
    static Vector2 temp = new Vector2();
    static Vector2 axis = new Vector2();
    static Polygon polygonA = new Polygon();
    static Polygon polygonB = new Polygon();
    float minA = 0.0F;
    float minB = 0.0F;
    float maxA = 0.0F;
    float maxB = 0.0F;
    CollisionManager.PolygonCollisionResult result = new CollisionManager.PolygonCollisionResult();
    public ArrayList<CollisionManager.Contact> ContactMap = new ArrayList<>();
    Long[] longArray = new Long[1000];
    Stack<CollisionManager.Contact> contacts = new Stack<>();
    public static final CollisionManager instance = new CollisionManager();

    private void ProjectPolygonA(Vector2 vector, Polygon polygon) {
        float float0 = vector.dot(polygon.points.get(0));
        this.minA = float0;
        this.maxA = float0;

        for (int int0 = 0; int0 < polygon.points.size(); int0++) {
            float0 = polygon.points.get(int0).dot(vector);
            if (float0 < this.minA) {
                this.minA = float0;
            } else if (float0 > this.maxA) {
                this.maxA = float0;
            }
        }
    }

    private void ProjectPolygonB(Vector2 vector, Polygon polygon) {
        float float0 = vector.dot(polygon.points.get(0));
        this.minB = float0;
        this.maxB = float0;

        for (int int0 = 0; int0 < polygon.points.size(); int0++) {
            float0 = polygon.points.get(int0).dot(vector);
            if (float0 < this.minB) {
                this.minB = float0;
            } else if (float0 > this.maxB) {
                this.maxB = float0;
            }
        }
    }

    public CollisionManager.PolygonCollisionResult PolygonCollision(Vector2 vector2) {
        this.result.Intersect = true;
        this.result.WillIntersect = true;
        this.result.MinimumTranslationVector.x = 0.0F;
        this.result.MinimumTranslationVector.y = 0.0F;
        int int0 = polygonA.edges.size();
        int int1 = polygonB.edges.size();
        float float0 = Float.POSITIVE_INFINITY;
        Vector2 vector0 = new Vector2();

        for (int int2 = 0; int2 < int0 + int1; int2++) {
            Vector2 vector1;
            if (int2 < int0) {
                vector1 = polygonA.edges.get(int2);
            } else {
                vector1 = polygonB.edges.get(int2 - int0);
            }

            axis.x = -vector1.y;
            axis.y = vector1.x;
            axis.normalize();
            this.minA = 0.0F;
            this.minB = 0.0F;
            this.maxA = 0.0F;
            this.maxB = 0.0F;
            this.ProjectPolygonA(axis, polygonA);
            this.ProjectPolygonB(axis, polygonB);
            if (this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB) > 0.0F) {
                this.result.Intersect = false;
            }

            float float1 = axis.dot(vector2);
            if (float1 < 0.0F) {
                this.minA += float1;
            } else {
                this.maxA += float1;
            }

            float float2 = this.IntervalDistance(this.minA, this.maxA, this.minB, this.maxB);
            if (float2 > 0.0F) {
                this.result.WillIntersect = false;
            }

            if (!this.result.Intersect && !this.result.WillIntersect) {
                break;
            }

            float2 = Math.abs(float2);
            if (float2 < float0) {
                float0 = float2;
                vector0.x = axis.x;
                vector0.y = axis.y;
                temp.x = polygonA.Center().x - polygonB.Center().x;
                temp.y = polygonA.Center().y - polygonB.Center().y;
                if (temp.dot(vector0) < 0.0F) {
                    vector0.x = -vector0.x;
                    vector0.y = -vector0.y;
                }
            }
        }

        if (this.result.WillIntersect) {
            this.result.MinimumTranslationVector.x = vector0.x * float0;
            this.result.MinimumTranslationVector.y = vector0.y * float0;
        }

        return this.result;
    }

    public float IntervalDistance(float float0, float float3, float float2, float float1) {
        return float0 < float2 ? float2 - float3 : float0 - float1;
    }

    public void initUpdate() {
        if (this.longArray[0] == null) {
            for (int int0 = 0; int0 < this.longArray.length; int0++) {
                this.longArray[int0] = new Long(0L);
            }
        }

        for (int int1 = 0; int1 < this.ContactMap.size(); int1++) {
            this.ContactMap.get(int1).a = null;
            this.ContactMap.get(int1).b = null;
            this.contacts.push(this.ContactMap.get(int1));
        }

        this.ContactMap.clear();
    }

    public void AddContact(IsoMovingObject movingObject1, IsoMovingObject movingObject0) {
        if (!(movingObject1 instanceof IsoSurvivor) && !(movingObject0 instanceof IsoSurvivor)
            || !(movingObject1 instanceof IsoPushableObject) && !(movingObject0 instanceof IsoPushableObject)) {
            if (movingObject1.getID() < movingObject0.getID()) {
                this.ContactMap.add(this.contact(movingObject1, movingObject0));
            }
        }
    }

    CollisionManager.Contact contact(IsoMovingObject movingObject0, IsoMovingObject movingObject1) {
        if (this.contacts.isEmpty()) {
            for (int int0 = 0; int0 < 50; int0++) {
                this.contacts.push(new CollisionManager.Contact(null, null));
            }
        }

        CollisionManager.Contact contact = this.contacts.pop();
        contact.a = movingObject0;
        contact.b = movingObject1;
        return contact;
    }

    public void ResolveContacts() {
        CollisionManager.s_performance.profile_ResolveContacts.invokeAndMeasure(this, CollisionManager::resolveContactsInternal);
    }

    private void resolveContactsInternal() {
        Vector2 vector0 = CollisionManager.l_ResolveContacts.vel;
        Vector2 vector1 = CollisionManager.l_ResolveContacts.vel2;
        List list = CollisionManager.l_ResolveContacts.pushables;
        ArrayList arrayList0 = IsoWorld.instance.CurrentCell.getPushableObjectList();
        int int0 = arrayList0.size();

        for (int int1 = 0; int1 < int0; int1++) {
            IsoPushableObject pushableObject0 = (IsoPushableObject)arrayList0.get(int1);
            if (pushableObject0.getImpulsex() != 0.0F || pushableObject0.getImpulsey() != 0.0F) {
                if (pushableObject0.connectList != null) {
                    list.add(pushableObject0);
                } else {
                    pushableObject0.setNx(pushableObject0.getNx() + pushableObject0.getImpulsex());
                    pushableObject0.setNy(pushableObject0.getNy() + pushableObject0.getImpulsey());
                    pushableObject0.setImpulsex(pushableObject0.getNx() - pushableObject0.getX());
                    pushableObject0.setImpulsey(pushableObject0.getNy() - pushableObject0.getY());
                    pushableObject0.setNx(pushableObject0.getX());
                    pushableObject0.setNy(pushableObject0.getY());
                }
            }
        }

        int int2 = list.size();

        for (int int3 = 0; int3 < int2; int3++) {
            IsoPushableObject pushableObject1 = (IsoPushableObject)list.get(int3);
            float float0 = 0.0F;
            float float1 = 0.0F;

            for (int int4 = 0; int4 < pushableObject1.connectList.size(); int4++) {
                float0 += pushableObject1.connectList.get(int4).getImpulsex();
                float1 += pushableObject1.connectList.get(int4).getImpulsey();
            }

            float0 /= pushableObject1.connectList.size();
            float1 /= pushableObject1.connectList.size();

            for (int int5 = 0; int5 < pushableObject1.connectList.size(); int5++) {
                pushableObject1.connectList.get(int5).setImpulsex(float0);
                pushableObject1.connectList.get(int5).setImpulsey(float1);
                int int6 = list.indexOf(pushableObject1.connectList.get(int5));
                list.remove(pushableObject1.connectList.get(int5));
                if (int6 <= int3) {
                    int3--;
                }
            }

            if (int3 < 0) {
                int3 = 0;
            }
        }

        list.clear();
        int int7 = this.ContactMap.size();

        for (int int8 = 0; int8 < int7; int8++) {
            CollisionManager.Contact contact = this.ContactMap.get(int8);
            if (!(Math.abs(contact.a.getZ() - contact.b.getZ()) > 0.3F)) {
                vector0.x = contact.a.getNx() - contact.a.getX();
                vector0.y = contact.a.getNy() - contact.a.getY();
                vector1.x = contact.b.getNx() - contact.b.getX();
                vector1.y = contact.b.getNy() - contact.b.getY();
                if (vector0.x != 0.0F
                    || vector0.y != 0.0F
                    || vector1.x != 0.0F
                    || vector1.y != 0.0F
                    || contact.a.getImpulsex() != 0.0F
                    || contact.a.getImpulsey() != 0.0F
                    || contact.b.getImpulsex() != 0.0F
                    || contact.b.getImpulsey() != 0.0F) {
                    float float2 = contact.a.getX() - contact.a.getWidth();
                    float float3 = contact.a.getX() + contact.a.getWidth();
                    float float4 = contact.a.getY() - contact.a.getWidth();
                    float float5 = contact.a.getY() + contact.a.getWidth();
                    float float6 = contact.b.getX() - contact.b.getWidth();
                    float float7 = contact.b.getX() + contact.b.getWidth();
                    float float8 = contact.b.getY() - contact.b.getWidth();
                    float float9 = contact.b.getY() + contact.b.getWidth();
                    polygonA.Set(float2, float4, float3, float5);
                    polygonB.Set(float6, float8, float7, float9);
                    CollisionManager.PolygonCollisionResult polygonCollisionResult = this.PolygonCollision(vector0);
                    if (polygonCollisionResult.WillIntersect) {
                        contact.a.collideWith(contact.b);
                        contact.b.collideWith(contact.a);
                        float float10 = 1.0F
                            - contact.a.getWeight(polygonCollisionResult.MinimumTranslationVector.x, polygonCollisionResult.MinimumTranslationVector.y)
                                / (
                                    contact.a.getWeight(polygonCollisionResult.MinimumTranslationVector.x, polygonCollisionResult.MinimumTranslationVector.y)
                                        + contact.b
                                            .getWeight(polygonCollisionResult.MinimumTranslationVector.x, polygonCollisionResult.MinimumTranslationVector.y)
                                );
                        if (contact.a instanceof IsoPushableObject && contact.b instanceof IsoSurvivor) {
                            ((IsoSurvivor)contact.b).bCollidedWithPushable = true;
                            ((IsoSurvivor)contact.b).collidePushable = (IsoPushableObject)contact.a;
                        } else if (contact.b instanceof IsoPushableObject && contact.a instanceof IsoSurvivor) {
                            ((IsoSurvivor)contact.a).bCollidedWithPushable = true;
                            ((IsoSurvivor)contact.a).collidePushable = (IsoPushableObject)contact.b;
                        }

                        if (contact.a instanceof IsoPushableObject) {
                            ArrayList arrayList1 = ((IsoPushableObject)contact.a).connectList;
                            if (arrayList1 != null) {
                                int int9 = arrayList1.size();

                                for (int int10 = 0; int10 < int9; int10++) {
                                    IsoPushableObject pushableObject2 = (IsoPushableObject)arrayList1.get(int10);
                                    pushableObject2.setImpulsex(pushableObject2.getImpulsex() + polygonCollisionResult.MinimumTranslationVector.x * float10);
                                    pushableObject2.setImpulsey(pushableObject2.getImpulsey() + polygonCollisionResult.MinimumTranslationVector.y * float10);
                                }
                            }
                        } else {
                            contact.a.setImpulsex(contact.a.getImpulsex() + polygonCollisionResult.MinimumTranslationVector.x * float10);
                            contact.a.setImpulsey(contact.a.getImpulsey() + polygonCollisionResult.MinimumTranslationVector.y * float10);
                        }

                        if (contact.b instanceof IsoPushableObject) {
                            ArrayList arrayList2 = ((IsoPushableObject)contact.b).connectList;
                            if (arrayList2 != null) {
                                int int11 = arrayList2.size();

                                for (int int12 = 0; int12 < int11; int12++) {
                                    IsoPushableObject pushableObject3 = (IsoPushableObject)arrayList2.get(int12);
                                    pushableObject3.setImpulsex(
                                        pushableObject3.getImpulsex() - polygonCollisionResult.MinimumTranslationVector.x * (1.0F - float10)
                                    );
                                    pushableObject3.setImpulsey(
                                        pushableObject3.getImpulsey() - polygonCollisionResult.MinimumTranslationVector.y * (1.0F - float10)
                                    );
                                }
                            }
                        } else {
                            contact.b.setImpulsex(contact.b.getImpulsex() - polygonCollisionResult.MinimumTranslationVector.x * (1.0F - float10));
                            contact.b.setImpulsey(contact.b.getImpulsey() - polygonCollisionResult.MinimumTranslationVector.y * (1.0F - float10));
                        }
                    }
                }
            }
        }

        ArrayList arrayList3 = IsoWorld.instance.CurrentCell.getObjectList();
        int int13 = arrayList3.size();
        MovingObjectUpdateScheduler.instance.postupdate();
        IsoMovingObject.treeSoundMgr.update();
        ZombieFootstepManager.instance.update();
        ZombieThumpManager.instance.update();
        ZombieVocalsManager.instance.update();
    }

    public class Contact {
        public IsoMovingObject a;
        public IsoMovingObject b;

        public Contact(IsoMovingObject movingObject0, IsoMovingObject movingObject1) {
            this.a = movingObject0;
            this.b = movingObject1;
        }
    }

    public class PolygonCollisionResult {
        public boolean WillIntersect;
        public boolean Intersect;
        public Vector2 MinimumTranslationVector = new Vector2();
    }

    private static class l_ResolveContacts {
        static final Vector2 vel = new Vector2();
        static final Vector2 vel2 = new Vector2();
        static final List<IsoPushableObject> pushables = new ArrayList<>();
        static IsoMovingObject[] objectListInvoking = new IsoMovingObject[1024];
    }

    private static class s_performance {
        static final PerformanceProfileProbe profile_ResolveContacts = new PerformanceProfileProbe("CollisionManager.ResolveContacts");
        static final PerformanceProfileProbe profile_MovingObjectPostUpdate = new PerformanceProfileProbe("IsoMovingObject.postupdate");
    }
}
