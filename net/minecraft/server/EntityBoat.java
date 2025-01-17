package net.minecraft.server;

import java.util.List;

public class EntityBoat extends Entity {

    private int a;
    private double b;
    private double c;
    private double d;
    private double e;
    private double f;

    public EntityBoat(World world) {
        super(world);
        this.bc = true;
        this.b(1.5F, 0.6F);
        this.height = this.length / 2.0F;
    }

    protected boolean g_() {
        return false;
    }

    protected void b() {
        this.datawatcher.a(17, new Integer(0));
        this.datawatcher.a(18, new Integer(1));
        this.datawatcher.a(19, new Integer(0));
    }

    public AxisAlignedBB a_(Entity entity) {
        return entity.boundingBox;
    }

    public AxisAlignedBB h_() {
        return this.boundingBox;
    }

    public boolean f_() {
        return true;
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1 + (double) this.height, d2);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    public double q() {
        return (double) this.length * 0.0D - 0.30000001192092896D;
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        if (!this.world.isStatic && !this.dead) {
            this.d(-this.l());
            this.c(10);
            this.setDamage(this.getDamage() + i * 10);
            this.aB();
            if (this.getDamage() > 40) {
                if (this.passenger != null) {
                    this.passenger.mount(this);
                }

                int j;

                for (j = 0; j < 3; ++j) {
                    this.a(Block.WOOD.id, 1, 0.0F);
                }

                for (j = 0; j < 2; ++j) {
                    this.a(Item.STICK.id, 1, 0.0F);
                }

                this.die();
            }

            return true;
        } else {
            return true;
        }
    }

    public boolean e_() {
        return !this.dead;
    }

    public void w_() {
        super.w_();
        if (this.k() > 0) {
            this.c(this.k() - 1);
        }

        if (this.getDamage() > 0) {
            this.setDamage(this.getDamage() - 1);
        }

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        byte b0 = 5;
        double d0 = 0.0D;

        for (int i = 0; i < b0; ++i) {
            double d1 = this.boundingBox.b + (this.boundingBox.e - this.boundingBox.b) * (double) (i + 0) / (double) b0 - 0.125D;
            double d2 = this.boundingBox.b + (this.boundingBox.e - this.boundingBox.b) * (double) (i + 1) / (double) b0 - 0.125D;
            AxisAlignedBB axisalignedbb = AxisAlignedBB.b(this.boundingBox.a, d1, this.boundingBox.c, this.boundingBox.d, d2, this.boundingBox.f);

            if (this.world.b(axisalignedbb, Material.WATER)) {
                d0 += 1.0D / (double) b0;
            }
        }

        double d3 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
        double d4;
        double d5;

        if (d3 > 0.15D) {
            d4 = Math.cos((double) this.yaw * 3.141592653589793D / 180.0D);
            d5 = Math.sin((double) this.yaw * 3.141592653589793D / 180.0D);

            for (int j = 0; (double) j < 1.0D + d3 * 60.0D; ++j) {
                double d6 = (double) (this.random.nextFloat() * 2.0F - 1.0F);
                double d7 = (double) (this.random.nextInt(2) * 2 - 1) * 0.7D;
                double d8;
                double d9;

                if (this.random.nextBoolean()) {
                    d8 = this.locX - d4 * d6 * 0.8D + d5 * d7;
                    d9 = this.locZ - d5 * d6 * 0.8D - d4 * d7;
                    this.world.a("splash", d8, this.locY - 0.125D, d9, this.motX, this.motY, this.motZ);
                } else {
                    d8 = this.locX + d4 + d5 * d6 * 0.7D;
                    d9 = this.locZ + d5 - d4 * d6 * 0.7D;
                    this.world.a("splash", d8, this.locY - 0.125D, d9, this.motX, this.motY, this.motZ);
                }
            }
        }

        double d10;
        double d11;

        if (this.world.isStatic) {
            if (this.a > 0) {
                d4 = this.locX + (this.b - this.locX) / (double) this.a;
                d5 = this.locY + (this.c - this.locY) / (double) this.a;
                d10 = this.locZ + (this.d - this.locZ) / (double) this.a;

                for (d11 = this.e - (double) this.yaw; d11 < -180.0D; d11 += 360.0D) {
                    ;
                }

                while (d11 >= 180.0D) {
                    d11 -= 360.0D;
                }

                this.yaw = (float) ((double) this.yaw + d11 / (double) this.a);
                this.pitch = (float) ((double) this.pitch + (this.f - (double) this.pitch) / (double) this.a);
                --this.a;
                this.setPosition(d4, d5, d10);
                this.c(this.yaw, this.pitch);
            } else {
                d4 = this.locX + this.motX;
                d5 = this.locY + this.motY;
                d10 = this.locZ + this.motZ;
                this.setPosition(d4, d5, d10);
                if (this.onGround) {
                    this.motX *= 0.5D;
                    this.motY *= 0.5D;
                    this.motZ *= 0.5D;
                }

                this.motX *= 0.9900000095367432D;
                this.motY *= 0.949999988079071D;
                this.motZ *= 0.9900000095367432D;
            }
        } else {
            if (d0 < 1.0D) {
                d4 = d0 * 2.0D - 1.0D;
                this.motY += 0.03999999910593033D * d4;
            } else {
                if (this.motY < 0.0D) {
                    this.motY /= 2.0D;
                }

                this.motY += 0.007000000216066837D;
            }

            if (this.passenger != null) {
                this.motX += this.passenger.motX * 0.2D;
                this.motZ += this.passenger.motZ * 0.2D;
            }

            d4 = 0.4D;
            if (this.motX < -d4) {
                this.motX = -d4;
            }

            if (this.motX > d4) {
                this.motX = d4;
            }

            if (this.motZ < -d4) {
                this.motZ = -d4;
            }

            if (this.motZ > d4) {
                this.motZ = d4;
            }

            if (this.onGround) {
                this.motX *= 0.5D;
                this.motY *= 0.5D;
                this.motZ *= 0.5D;
            }

            this.move(this.motX, this.motY, this.motZ);
            if (this.positionChanged && d3 > 0.2D) {
                if (!this.world.isStatic) {
                    this.die();

                    int k;

                    for (k = 0; k < 3; ++k) {
                        this.a(Block.WOOD.id, 1, 0.0F);
                    }

                    for (k = 0; k < 2; ++k) {
                        this.a(Item.STICK.id, 1, 0.0F);
                    }
                }
            } else {
                this.motX *= 0.9900000095367432D;
                this.motY *= 0.949999988079071D;
                this.motZ *= 0.9900000095367432D;
            }

            this.pitch = 0.0F;
            d5 = (double) this.yaw;
            d10 = this.lastX - this.locX;
            d11 = this.lastZ - this.locZ;
            if (d10 * d10 + d11 * d11 > 0.0010D) {
                d5 = (double) ((float) (Math.atan2(d11, d10) * 180.0D / 3.141592653589793D));
            }

            double d12;

            for (d12 = d5 - (double) this.yaw; d12 >= 180.0D; d12 -= 360.0D) {
                ;
            }

            while (d12 < -180.0D) {
                d12 += 360.0D;
            }

            if (d12 > 20.0D) {
                d12 = 20.0D;
            }

            if (d12 < -20.0D) {
                d12 = -20.0D;
            }

            this.yaw = (float) ((double) this.yaw + d12);
            this.c(this.yaw, this.pitch);
            List list = this.world.b((Entity) this, this.boundingBox.b(0.20000000298023224D, 0.0D, 0.20000000298023224D));
            int l;

            if (list != null && list.size() > 0) {
                for (l = 0; l < list.size(); ++l) {
                    Entity entity = (Entity) list.get(l);

                    if (entity != this.passenger && entity.f_() && entity instanceof EntityBoat) {
                        entity.collide(this);
                    }
                }
            }

            for (l = 0; l < 4; ++l) {
                int i1 = MathHelper.floor(this.locX + ((double) (l % 2) - 0.5D) * 0.8D);
                int j1 = MathHelper.floor(this.locY);
                int k1 = MathHelper.floor(this.locZ + ((double) (l / 2) - 0.5D) * 0.8D);

                if (this.world.getTypeId(i1, j1, k1) == Block.SNOW.id) {
                    this.world.setTypeId(i1, j1, k1, 0);
                }
            }

            if (this.passenger != null && this.passenger.dead) {
                this.passenger = null;
            }
        }
    }

    public void i() {
        if (this.passenger != null) {
            double d0 = Math.cos((double) this.yaw * 3.141592653589793D / 180.0D) * 0.4D;
            double d1 = Math.sin((double) this.yaw * 3.141592653589793D / 180.0D) * 0.4D;

            this.passenger.setPosition(this.locX + d0, this.locY + this.q() + this.passenger.R(), this.locZ + d1);
        }
    }

    protected void b(NBTTagCompound nbttagcompound) {}

    protected void a(NBTTagCompound nbttagcompound) {}

    public boolean b(EntityHuman entityhuman) {
        if (this.passenger != null && this.passenger instanceof EntityHuman && this.passenger != entityhuman) {
            return true;
        } else {
            if (!this.world.isStatic) {
                entityhuman.mount(this);
            }

            return true;
        }
    }

    public void setDamage(int i) {
        this.datawatcher.watch(19, Integer.valueOf(i));
    }

    public int getDamage() {
        return this.datawatcher.getInt(19);
    }

    public void c(int i) {
        this.datawatcher.watch(17, Integer.valueOf(i));
    }

    public int k() {
        return this.datawatcher.getInt(17);
    }

    public void d(int i) {
        this.datawatcher.watch(18, Integer.valueOf(i));
    }

    public int l() {
        return this.datawatcher.getInt(18);
    }
}
