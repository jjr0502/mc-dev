package net.minecraft.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet39AttachEntity extends Packet {

    public int a;
    public int b;

    public Packet39AttachEntity() {}

    public Packet39AttachEntity(Entity entity, Entity entity1) {
        this.a = entity.id;
        this.b = entity1 != null ? entity1.id : -1;
    }

    public int a() {
        return 8;
    }

    public void a(DataInputStream datainputstream) throws IOException { // CraftBukkit - add throws declaration
        this.a = datainputstream.readInt();
        this.b = datainputstream.readInt();
    }

    public void a(DataOutputStream dataoutputstream) throws IOException { // CraftBukkit - add throws declaration
        dataoutputstream.writeInt(this.a);
        dataoutputstream.writeInt(this.b);
    }

    public void a(NetHandler nethandler) {
        nethandler.a(this);
    }
}
