package org.kurento.tutorial.groupcall.websocket;

import org.apache.commons.codec.digest.DigestUtils;
import org.kurento.client.MediaPipeline;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateRoom extends Room {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
    private final String secretKey;
    private int roomSize;
    private ReentrantLock reentrantLock = new ReentrantLock();

    public PrivateRoom(String roomName, MediaPipeline mediaPipeline, String secretKey) {
        super(roomName, mediaPipeline);
        roomSize = 0;
        this.secretKey = DigestUtils.md5Hex(secretKey);
    }

    @Override
    void join(UserSession participantRoomSession) throws IOException {
        reentrantLock.lock();
        if (!isAuthorized(participantRoomSession.getToken())) {
            return;
        }
        if (!secretKey.equals(DigestUtils.md5Hex(participantRoomSession.getSecretRoomKey()))) {
            return;
        }
        if (roomSize > 4) {
            return;
        }
        roomSize++;
        reentrantLock.unlock();
        super.join(participantRoomSession);
    }

    private boolean isAuthorized(String userToken) {
        final Pattern compile = Pattern.compile(TOKEN_REGEX);
        final Matcher matcher = compile.matcher(userToken);
        return matcher.matches();
    }
}
