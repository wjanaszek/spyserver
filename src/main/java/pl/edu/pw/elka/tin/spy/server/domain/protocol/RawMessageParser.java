package pl.edu.pw.elka.tin.spy.server.domain.protocol;

import pl.edu.pw.elka.tin.spy.server.domain.protocol.message.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class RawMessageParser {
    public static Message parse(byte[] message) {
        ByteBuffer bb = ByteBuffer.wrap(message);

        Header messageHeader = readHeader(bb);

        switch (messageHeader) {
            case PHOTO: {
                int photoSize = bb.remaining();
                byte[] photo = new byte[photoSize];
                bb.get(photo, 0, photoSize);
                return new PhotoMessage(photo);
            }

            case REGISTRATION_REQUEST: {
                String name = readString(bb);
                String password = readString(bb);
                return new RegistrationRequest(name, password);
            }

            case AUTHENTICATION_REQUEST: {
                int userID = bb.getInt();
                String password = readString(bb);
                return new AuthRequest(userID, password);
            }

            default:
                return SimpleMessage.UnrecognisedHeader;
        }
    }

    private static String readString(ByteBuffer bb) {
        int nameSize = bb.getInt();
        byte[] rawName = new byte[nameSize];
        bb.get(rawName, 0, nameSize);

        return new String(rawName);
    }

    private static Header readHeader(ByteBuffer bb) {
        byte[] rawHeader = new byte[3];
        bb.get(rawHeader, 0, 3);
        return Header.fromString(new String(rawHeader, StandardCharsets.UTF_8));
    }
}
