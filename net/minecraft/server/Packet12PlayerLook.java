package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet12PlayerLook extends Packet10Flying {

    public Packet12PlayerLook() {
        this.hasLook = true;
    }

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.yaw = datainputstream.readFloat();
        this.pitch = datainputstream.readFloat();
        super.a(datainputstream);
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeFloat(this.yaw);
        dataoutputstream.writeFloat(this.pitch);
        super.a(dataoutputstream);
    }

    public int a() {
        return 9;
    }
}
