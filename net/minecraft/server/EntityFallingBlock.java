package net.minecraft.server;

public class EntityFallingBlock extends Entity {

    public int a;
    public int b = 0;

    public EntityFallingBlock(World world) {
        super(world);
    }

    public EntityFallingBlock(World world, double d0, double d1, double d2, int i) {
        super(world);
        this.a = i;
        this.bc = true;
        this.b(0.98F, 0.98F);
        this.height = this.length / 2.0F;
        this.setPosition(d0, d1, d2);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    protected boolean g_() {
        return false;
    }

    protected void b() {}

    public boolean e_() {
        return !this.dead;
    }

    public void w_() {
        if (this.a == 0) {
            this.die();
        } else {
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            ++this.b;
            this.motY -= 0.03999999910593033D;
            this.move(this.motX, this.motY, this.motZ);
            this.motX *= 0.9800000190734863D;
            this.motY *= 0.9800000190734863D;
            this.motZ *= 0.9800000190734863D;
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY);
            int k = MathHelper.floor(this.locZ);

            if (this.b == 1 && this.world.getTypeId(i, j, k) == this.a) {
                this.world.setTypeId(i, j, k, 0);
            } else if (!this.world.isStatic && this.b == 1) {
                this.die();
            }

            if (this.onGround) {
                this.motX *= 0.699999988079071D;
                this.motZ *= 0.699999988079071D;
                this.motY *= -0.5D;
                if (this.world.getTypeId(i, j, k) != Block.PISTON_MOVING.id) {
                    this.die();
                    if ((!this.world.a(this.a, i, j, k, true, 1) || BlockSand.g(this.world, i, j - 1, k) || !this.world.setTypeId(i, j, k, this.a)) && !this.world.isStatic) {
                        this.b(this.a, 1);
                    }
                }
            } else if (this.b > 100 && !this.world.isStatic) {
                this.b(this.a, 1);
                this.die();
            }
        }
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Tile", (byte) this.a);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.a = nbttagcompound.getByte("Tile") & 255;
    }
}
