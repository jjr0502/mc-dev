package net.minecraft.server;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class EntityTrackerEntry {

    public Entity tracker;
    public int b;
    public int c;
    public int d;
    public int e;
    public int f;
    public int g;
    public int h;
    public double i;
    public double j;
    public double k;
    public int l = 0;
    private double o;
    private double p;
    private double q;
    private boolean r = false;
    private boolean isMoving;
    private int t = 0;
    public boolean m = false;
    public Set trackedPlayers = new HashSet();

    public EntityTrackerEntry(Entity entity, int i, int j, boolean flag) {
        this.tracker = entity;
        this.b = i;
        this.c = j;
        this.isMoving = flag;
        this.d = MathHelper.floor(entity.locX * 32.0D);
        this.e = MathHelper.floor(entity.locY * 32.0D);
        this.f = MathHelper.floor(entity.locZ * 32.0D);
        this.g = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.h = MathHelper.d(entity.pitch * 256.0F / 360.0F);
    }

    public boolean equals(Object object) {
        return object instanceof EntityTrackerEntry ? ((EntityTrackerEntry) object).tracker.id == this.tracker.id : false;
    }

    public int hashCode() {
        return this.tracker.id;
    }

    public void track(List list) {
        this.m = false;
        if (!this.r || this.tracker.e(this.o, this.p, this.q) > 16.0D) {
            this.o = this.tracker.locX;
            this.p = this.tracker.locY;
            this.q = this.tracker.locZ;
            this.r = true;
            this.m = true;
            this.scanPlayers(list);
        }

        ++this.t;
        if (++this.l % this.c == 0 || this.tracker.cb) {
            int i = MathHelper.floor(this.tracker.locX * 32.0D);
            int j = MathHelper.floor(this.tracker.locY * 32.0D);
            int k = MathHelper.floor(this.tracker.locZ * 32.0D);
            int l = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
            int i1 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
            int j1 = i - this.d;
            int k1 = j - this.e;
            int l1 = k - this.f;
            Object object = null;
            boolean flag = Math.abs(j1) >= 4 || Math.abs(k1) >= 4 || Math.abs(l1) >= 4;
            boolean flag1 = Math.abs(l - this.g) >= 4 || Math.abs(i1 - this.h) >= 4;

            if (j1 >= -128 && j1 < 128 && k1 >= -128 && k1 < 128 && l1 >= -128 && l1 < 128 && this.t <= 400) {
                if (flag && flag1) {
                    object = new Packet33RelEntityMoveLook(this.tracker.id, (byte) j1, (byte) k1, (byte) l1, (byte) l, (byte) i1);
                } else if (flag) {
                    object = new Packet31RelEntityMove(this.tracker.id, (byte) j1, (byte) k1, (byte) l1);
                } else if (flag1) {
                    object = new Packet32EntityLook(this.tracker.id, (byte) l, (byte) i1);
                }
            } else {
                this.t = 0;
                this.tracker.locX = (double) i / 32.0D;
                this.tracker.locY = (double) j / 32.0D;
                this.tracker.locZ = (double) k / 32.0D;
                object = new Packet34EntityTeleport(this.tracker.id, i, j, k, (byte) l, (byte) i1);
            }

            if (this.isMoving) {
                double d0 = this.tracker.motX - this.i;
                double d1 = this.tracker.motY - this.j;
                double d2 = this.tracker.motZ - this.k;
                double d3 = 0.02D;
                double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d4 > d3 * d3 || d4 > 0.0D && this.tracker.motX == 0.0D && this.tracker.motY == 0.0D && this.tracker.motZ == 0.0D) {
                    this.i = this.tracker.motX;
                    this.j = this.tracker.motY;
                    this.k = this.tracker.motZ;
                    this.a((Packet) (new Packet28EntityVelocity(this.tracker.id, this.i, this.j, this.k)));
                }
            }

            if (object != null) {
                this.a((Packet) object);
            }

            DataWatcher datawatcher = this.tracker.getDataWatcher();

            if (datawatcher.a()) {
                this.b((Packet) (new Packet40EntityMetadata(this.tracker.id, datawatcher)));
            }

            if (flag) {
                this.d = i;
                this.e = j;
                this.f = k;
            }

            if (flag1) {
                this.g = l;
                this.h = i1;
            }
        }

        this.tracker.cb = false;
        if (this.tracker.velocityChanged) {
            this.b((Packet) (new Packet28EntityVelocity(this.tracker)));
            this.tracker.velocityChanged = false;
        }
    }

    public void a(Packet packet) {
        Iterator iterator = this.trackedPlayers.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.netServerHandler.sendPacket(packet);
        }
    }

    public void b(Packet packet) {
        this.a(packet);
        if (this.tracker instanceof EntityPlayer) {
            ((EntityPlayer) this.tracker).netServerHandler.sendPacket(packet);
        }
    }

    public void a() {
        this.a((Packet) (new Packet29DestroyEntity(this.tracker.id)));
    }

    public void a(EntityPlayer entityplayer) {
        if (this.trackedPlayers.contains(entityplayer)) {
            this.trackedPlayers.remove(entityplayer);
        }
    }

    public void b(EntityPlayer entityplayer) {
        if (entityplayer != this.tracker) {
            double d0 = entityplayer.locX - (double) (this.d / 32);
            double d1 = entityplayer.locZ - (double) (this.f / 32);

            if (d0 >= (double) (-this.b) && d0 <= (double) this.b && d1 >= (double) (-this.b) && d1 <= (double) this.b) {
                if (!this.trackedPlayers.contains(entityplayer)) {
                    this.trackedPlayers.add(entityplayer);
                    entityplayer.netServerHandler.sendPacket(this.b());
                    if (this.isMoving) {
                        entityplayer.netServerHandler.sendPacket(new Packet28EntityVelocity(this.tracker.id, this.tracker.motX, this.tracker.motY, this.tracker.motZ));
                    }

                    ItemStack[] aitemstack = this.tracker.getEquipment();

                    if (aitemstack != null) {
                        for (int i = 0; i < aitemstack.length; ++i) {
                            entityplayer.netServerHandler.sendPacket(new Packet5EntityEquipment(this.tracker.id, i, aitemstack[i]));
                        }
                    }

                    if (this.tracker instanceof EntityHuman) {
                        EntityHuman entityhuman = (EntityHuman) this.tracker;

                        if (entityhuman.isSleeping()) {
                            entityplayer.netServerHandler.sendPacket(new Packet17EntityLocationAction(this.tracker, 0, MathHelper.floor(this.tracker.locX), MathHelper.floor(this.tracker.locY), MathHelper.floor(this.tracker.locZ)));
                        }
                    }

                    if (this.tracker instanceof EntityLiving) {
                        EntityLiving entityliving = (EntityLiving) this.tracker;
                        Iterator iterator = entityliving.getEffects().iterator();

                        while (iterator.hasNext()) {
                            MobEffect mobeffect = (MobEffect) iterator.next();

                            entityplayer.netServerHandler.sendPacket(new Packet41MobEffect(this.tracker.id, mobeffect));
                        }
                    }
                }
            } else if (this.trackedPlayers.contains(entityplayer)) {
                this.trackedPlayers.remove(entityplayer);
                entityplayer.netServerHandler.sendPacket(new Packet29DestroyEntity(this.tracker.id));
            }
        }
    }

    public void scanPlayers(List list) {
        for (int i = 0; i < list.size(); ++i) {
            this.b((EntityPlayer) list.get(i));
        }
    }

    private Packet b() {
        if (this.tracker.dead) {
            System.out.println("Fetching addPacket for removed entity");
        }

        if (this.tracker instanceof EntityItem) {
            EntityItem entityitem = (EntityItem) this.tracker;
            Packet21PickupSpawn packet21pickupspawn = new Packet21PickupSpawn(entityitem);

            entityitem.locX = (double) packet21pickupspawn.b / 32.0D;
            entityitem.locY = (double) packet21pickupspawn.c / 32.0D;
            entityitem.locZ = (double) packet21pickupspawn.d / 32.0D;
            return packet21pickupspawn;
        } else if (this.tracker instanceof EntityPlayer) {
            return new Packet20NamedEntitySpawn((EntityHuman) this.tracker);
        } else {
            if (this.tracker instanceof EntityMinecart) {
                EntityMinecart entityminecart = (EntityMinecart) this.tracker;

                if (entityminecart.type == 0) {
                    return new Packet23VehicleSpawn(this.tracker, 10);
                }

                if (entityminecart.type == 1) {
                    return new Packet23VehicleSpawn(this.tracker, 11);
                }

                if (entityminecart.type == 2) {
                    return new Packet23VehicleSpawn(this.tracker, 12);
                }
            }

            if (this.tracker instanceof EntityBoat) {
                return new Packet23VehicleSpawn(this.tracker, 1);
            } else if (this.tracker instanceof IAnimal) {
                return new Packet24MobSpawn((EntityLiving) this.tracker);
            } else if (this.tracker instanceof EntityEnderDragon) {
                return new Packet24MobSpawn((EntityLiving) this.tracker);
            } else if (this.tracker instanceof EntityFishingHook) {
                return new Packet23VehicleSpawn(this.tracker, 90);
            } else if (this.tracker instanceof EntityArrow) {
                Entity entity = ((EntityArrow) this.tracker).shooter;

                return new Packet23VehicleSpawn(this.tracker, 60, entity != null ? entity.id : this.tracker.id);
            } else if (this.tracker instanceof EntitySnowball) {
                return new Packet23VehicleSpawn(this.tracker, 61);
            } else if (this.tracker instanceof EntityPotion) {
                return new Packet23VehicleSpawn(this.tracker, 73, ((EntityPotion) this.tracker).f());
            } else if (this.tracker instanceof EntityEnderPearl) {
                return new Packet23VehicleSpawn(this.tracker, 65);
            } else if (this.tracker instanceof EntityEnderSignal) {
                return new Packet23VehicleSpawn(this.tracker, 72);
            } else {
                Packet23VehicleSpawn packet23vehiclespawn;

                if (this.tracker instanceof EntitySmallFireball) {
                    EntitySmallFireball entitysmallfireball = (EntitySmallFireball) this.tracker;

                    packet23vehiclespawn = null;
                    if (entitysmallfireball.shooter != null) {
                        packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 64, entitysmallfireball.shooter.id);
                    } else {
                        packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 64, 0);
                    }

                    packet23vehiclespawn.e = (int) (entitysmallfireball.dirX * 8000.0D);
                    packet23vehiclespawn.f = (int) (entitysmallfireball.dirY * 8000.0D);
                    packet23vehiclespawn.g = (int) (entitysmallfireball.dirZ * 8000.0D);
                    return packet23vehiclespawn;
                } else if (this.tracker instanceof EntityFireball) {
                    EntityFireball entityfireball = (EntityFireball) this.tracker;

                    packet23vehiclespawn = null;
                    if (entityfireball.shooter != null) {
                        packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 63, ((EntityFireball) this.tracker).shooter.id);
                    } else {
                        packet23vehiclespawn = new Packet23VehicleSpawn(this.tracker, 63, 0);
                    }

                    packet23vehiclespawn.e = (int) (entityfireball.dirX * 8000.0D);
                    packet23vehiclespawn.f = (int) (entityfireball.dirY * 8000.0D);
                    packet23vehiclespawn.g = (int) (entityfireball.dirZ * 8000.0D);
                    return packet23vehiclespawn;
                } else if (this.tracker instanceof EntityEgg) {
                    return new Packet23VehicleSpawn(this.tracker, 62);
                } else if (this.tracker instanceof EntityTNTPrimed) {
                    return new Packet23VehicleSpawn(this.tracker, 50);
                } else if (this.tracker instanceof EntityEnderCrystal) {
                    return new Packet23VehicleSpawn(this.tracker, 51);
                } else {
                    if (this.tracker instanceof EntityFallingBlock) {
                        EntityFallingBlock entityfallingblock = (EntityFallingBlock) this.tracker;

                        if (entityfallingblock.a == Block.SAND.id) {
                            return new Packet23VehicleSpawn(this.tracker, 70);
                        }

                        if (entityfallingblock.a == Block.GRAVEL.id) {
                            return new Packet23VehicleSpawn(this.tracker, 71);
                        }

                        if (entityfallingblock.a == Block.DRAGON_EGG.id) {
                            return new Packet23VehicleSpawn(this.tracker, 74);
                        }
                    }

                    if (this.tracker instanceof EntityPainting) {
                        return new Packet25EntityPainting((EntityPainting) this.tracker);
                    } else if (this.tracker instanceof EntityExperienceOrb) {
                        return new Packet26AddExpOrb((EntityExperienceOrb) this.tracker);
                    } else {
                        throw new IllegalArgumentException("Don\'t know how to add " + this.tracker.getClass() + "!");
                    }
                }
            }
        }
    }

    public void c(EntityPlayer entityplayer) {
        if (this.trackedPlayers.contains(entityplayer)) {
            this.trackedPlayers.remove(entityplayer);
            entityplayer.netServerHandler.sendPacket(new Packet29DestroyEntity(this.tracker.id));
        }
    }
}
