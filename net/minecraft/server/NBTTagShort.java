package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class NBTTagShort extends NBTBase {

    public short a;

    public NBTTagShort() {}

    public NBTTagShort(short short1) {
        this.a = short1;
    }

    void a(DataOutput dataoutput) throws IOException { // CraftBukkit - add throws declaration
        dataoutput.writeShort(this.a);
    }

    void a(DataInput datainput) throws IOException { // CraftBukkit - add throws declaration
        this.a = datainput.readShort();
    }

    public byte a() {
        return (byte) 2;
    }

    public String toString() {
        return "" + this.a;
    }
}
