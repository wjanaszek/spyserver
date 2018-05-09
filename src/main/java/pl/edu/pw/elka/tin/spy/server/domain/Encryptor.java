package pl.edu.pw.elka.tin.spy.server.domain;

public interface Encryptor {
    byte[] decrypt(byte[] message, byte[] secret);
}
