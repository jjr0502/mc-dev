package net.minecraft.server;

import java.io.IOException;

public interface IChunkLoader {

    Chunk a(World world, int i, int j) throws IOException; // CraftBukkit - add throws declaration

    void a(World world, Chunk chunk) throws IOException; // CraftBukkit - add throws declaration

    void b(World world, Chunk chunk) throws IOException; // CraftBukkit - add throws declaration

    void a();

    void b();
}
