package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet16BlockItemSwitch extends Packet {

    public int itemInHandIndex;

    public Packet16BlockItemSwitch() {}

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.itemInHandIndex = datainputstream.readShort();
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeShort(this.itemInHandIndex);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }

    public int a() {
        return 2;
    }
}
