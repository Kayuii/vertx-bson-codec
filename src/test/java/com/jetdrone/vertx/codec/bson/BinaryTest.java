package com.jetdrone.vertx.codec.bson;

import io.vertx.core.buffer.Buffer;
import org.junit.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BinaryTest {

  private static final BSONMessageCodec BSON = new BSONMessageCodec();

  @Test
  public void testUUID() {
    BSONDocument json = new BSONDocument();
    json.put("_", UUID.fromString("797ff043-11eb-11e1-80d6-510998755d10"));

    Buffer buffer = Buffer.buffer();
    BSON.encodeToWire(buffer, json);

    byte[] bson = buffer.getBytes();

    byte[] expected = new byte[]{
        // length
        0x1d, 0x00, 0x00, 0x00,
        // data
        0x05, '_', 0x00, 0x10, 0x00, 0x00, 0x00, 0x04, 0x79, 0x7f, (byte) 0xf0, 0x43, 0x11, (byte) 0xeb, 0x11, (byte) 0xe1, (byte) 0x80, (byte) 0xd6, 0x51, 0x09, (byte) 0x98, 0x75, 0x5d, 0x10,
        // end
        0x00
    };

    assertArrayEquals(expected, bson);

    // reverse
    Map document = BSON.decodeFromWire(0, Buffer.buffer(expected));
    assertEquals(json, document);
  }

  @Test
  public void testUserDefinedBinary() {
    BSONDocument json = new BSONDocument();
    json.put("_", Buffer.buffer("udef"));

    Buffer buffer = Buffer.buffer();
    BSON.encodeToWire(buffer, json);
    byte[] bson = buffer.getBytes();

    byte[] expected = new byte[]{
        // length
        0x11, 0x00, 0x00, 0x00,
        // data
        0x05, '_', 0x00, 0x04, 0x00, 0x00, 0x00, (byte) 0x80, 'u', 'd', 'e', 'f',
        // end
        0x00
    };

    assertArrayEquals(expected, bson);

    // reverse
    Map document = BSON.decodeFromWire(0, Buffer.buffer(expected));
    assertArrayEquals(new byte[]{'u', 'd', 'e', 'f'}, ((Buffer) document.get("_")).getBytes());
  }

  @Test
  public void testMD5Binary() {
    BSONDocument json = new BSONDocument();
    json.put("_", (MD5) () -> "udef".getBytes());

    Buffer buffer = Buffer.buffer();
    BSON.encodeToWire(buffer, json);
    byte[] bson = buffer.getBytes();

    byte[] expected = new byte[]{
        // length
        0x11, 0x00, 0x00, 0x00,
        // data
        0x05, '_', 0x00, 0x04, 0x00, 0x00, 0x00, 0x05, 'u', 'd', 'e', 'f',
        // end
        0x00
    };

    assertArrayEquals(expected, bson);

    // reverse
    Map document = BSON.decodeFromWire(0, Buffer.buffer(expected));
    assertArrayEquals(new byte[]{'u', 'd', 'e', 'f'}, ((MD5) document.get("_")).getHash());
  }
}
