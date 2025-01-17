package net.minecraft.server;

import java.util.Random;

public class BlockFire extends Block {

    private int[] a = new int[256];
    private int[] b = new int[256];

    protected BlockFire(int i, int j) {
        super(i, j, Material.FIRE);
        this.a(true);
    }

    public void j() {
        this.a(Block.WOOD.id, 5, 20);
        this.a(Block.FENCE.id, 5, 20);
        this.a(Block.WOOD_STAIRS.id, 5, 20);
        this.a(Block.LOG.id, 5, 5);
        this.a(Block.LEAVES.id, 30, 60);
        this.a(Block.BOOKSHELF.id, 30, 20);
        this.a(Block.TNT.id, 15, 100);
        this.a(Block.LONG_GRASS.id, 60, 100);
        this.a(Block.WOOL.id, 30, 60);
        this.a(Block.VINE.id, 15, 100);
    }

    private void a(int i, int j, int k) {
        this.a[i] = j;
        this.b[i] = k;
    }

    public AxisAlignedBB e(World world, int i, int j, int k) {
        return null;
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public int c() {
        return 3;
    }

    public int a(Random random) {
        return 0;
    }

    public int d() {
        return 40;
    }

    public void a(World world, int i, int j, int k, Random random) {
        boolean flag = world.getTypeId(i, j - 1, k) == Block.NETHERRACK.id;

        if (world.worldProvider instanceof WorldProviderTheEnd && world.getTypeId(i, j - 1, k) == Block.BEDROCK.id) {
            flag = true;
        }

        if (!this.canPlace(world, i, j, k)) {
            world.setTypeId(i, j, k, 0);
        }

        if (!flag && world.w() && (world.v(i, j, k) || world.v(i - 1, j, k) || world.v(i + 1, j, k) || world.v(i, j, k - 1) || world.v(i, j, k + 1))) {
            world.setTypeId(i, j, k, 0);
        } else {
            int l = world.getData(i, j, k);

            if (l < 15) {
                world.setRawData(i, j, k, l + random.nextInt(3) / 2);
            }

            world.c(i, j, k, this.id, this.d());
            if (!flag && !this.g(world, i, j, k)) {
                if (!world.e(i, j - 1, k) || l > 3) {
                    world.setTypeId(i, j, k, 0);
                }
            } else if (!flag && !this.b(world, i, j - 1, k) && l == 15 && random.nextInt(4) == 0) {
                world.setTypeId(i, j, k, 0);
            } else {
                this.a(world, i + 1, j, k, 300, random, l);
                this.a(world, i - 1, j, k, 300, random, l);
                this.a(world, i, j - 1, k, 250, random, l);
                this.a(world, i, j + 1, k, 250, random, l);
                this.a(world, i, j, k - 1, 300, random, l);
                this.a(world, i, j, k + 1, 300, random, l);

                for (int i1 = i - 1; i1 <= i + 1; ++i1) {
                    for (int j1 = k - 1; j1 <= k + 1; ++j1) {
                        for (int k1 = j - 1; k1 <= j + 4; ++k1) {
                            if (i1 != i || k1 != j || j1 != k) {
                                int l1 = 100;

                                if (k1 > j + 1) {
                                    l1 += (k1 - (j + 1)) * 100;
                                }

                                int i2 = this.h(world, i1, k1, j1);

                                if (i2 > 0) {
                                    int j2 = (i2 + 40) / (l + 30);

                                    if (j2 > 0 && random.nextInt(l1) <= j2 && (!world.w() || !world.v(i1, k1, j1)) && !world.v(i1 - 1, k1, k) && !world.v(i1 + 1, k1, j1) && !world.v(i1, k1, j1 - 1) && !world.v(i1, k1, j1 + 1)) {
                                        int k2 = l + random.nextInt(5) / 4;

                                        if (k2 > 15) {
                                            k2 = 15;
                                        }

                                        world.setTypeIdAndData(i1, k1, j1, this.id, k2);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void a(World world, int i, int j, int k, int l, Random random, int i1) {
        int j1 = this.b[world.getTypeId(i, j, k)];

        if (random.nextInt(l) < j1) {
            boolean flag = world.getTypeId(i, j, k) == Block.TNT.id;

            if (random.nextInt(i1 + 10) < 5 && !world.v(i, j, k)) {
                int k1 = i1 + random.nextInt(5) / 4;

                if (k1 > 15) {
                    k1 = 15;
                }

                world.setTypeIdAndData(i, j, k, this.id, k1);
            } else {
                world.setTypeId(i, j, k, 0);
            }

            if (flag) {
                Block.TNT.postBreak(world, i, j, k, 1);
            }
        }
    }

    private boolean g(World world, int i, int j, int k) {
        return this.b(world, i + 1, j, k) ? true : (this.b(world, i - 1, j, k) ? true : (this.b(world, i, j - 1, k) ? true : (this.b(world, i, j + 1, k) ? true : (this.b(world, i, j, k - 1) ? true : this.b(world, i, j, k + 1)))));
    }

    private int h(World world, int i, int j, int k) {
        byte b0 = 0;

        if (!world.isEmpty(i, j, k)) {
            return 0;
        } else {
            int l = this.f(world, i + 1, j, k, b0);

            l = this.f(world, i - 1, j, k, l);
            l = this.f(world, i, j - 1, k, l);
            l = this.f(world, i, j + 1, k, l);
            l = this.f(world, i, j, k - 1, l);
            l = this.f(world, i, j, k + 1, l);
            return l;
        }
    }

    public boolean v_() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, int i, int j, int k) {
        return this.a[iblockaccess.getTypeId(i, j, k)] > 0;
    }

    public int f(World world, int i, int j, int k, int l) {
        int i1 = this.a[world.getTypeId(i, j, k)];

        return i1 > l ? i1 : l;
    }

    public boolean canPlace(World world, int i, int j, int k) {
        return world.e(i, j - 1, k) || this.g(world, i, j, k);
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        if (!world.e(i, j - 1, k) && !this.g(world, i, j, k)) {
            world.setTypeId(i, j, k, 0);
        }
    }

    public void onPlace(World world, int i, int j, int k) {
        if (world.worldProvider.dimension > 0 || world.getTypeId(i, j - 1, k) != Block.OBSIDIAN.id || !Block.PORTAL.b_(world, i, j, k)) {
            if (!world.e(i, j - 1, k) && !this.g(world, i, j, k)) {
                world.setTypeId(i, j, k, 0);
            } else {
                world.c(i, j, k, this.id, this.d());
            }
        }
    }
}
