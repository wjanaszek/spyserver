package pl.edu.pw.elka.tin.spy.server.infrastructure.encryption;

import pl.edu.pw.elka.tin.spy.server.domain.Encryptor;

import java.nio.ByteBuffer;

public class XOREncryptor implements Encryptor {

    @Override
    public byte[] decrypt(byte[] message, byte[] secret) {
        if (secret != null) {
            ByteBuffer buffer = ByteBuffer.allocate(message.length);

            int secretLength = secret.length;
            for (int i = 0; i < message.length; i++) {
                byte b = (byte)(message[i] ^ secret[8 % secretLength]);
                buffer.put(b);
            }
            return buffer.array();
        }
        return message;
    }
}
