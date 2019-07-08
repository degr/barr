package org.kurento.tutorial.groupcall.websocket;

import lombok.Getter;
import org.kurento.client.MediaPipeline;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateRoom extends Room {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
    private AtomicInteger roomSize;
    private ReentrantLock reentrantLock = new ReentrantLock();

    @Getter
    private final String secretKey;

    public PrivateRoom(String roomName, MediaPipeline mediaPipeline, String secretKey) {
        super(roomName, mediaPipeline);
        roomSize = new AtomicInteger(0);
        this.secretKey = secretKey;
    }

    @Override
    void join(UserSession participantRoomSession) throws IOException {
        reentrantLock.lock();
        if (!isAuthorized(participantRoomSession.getToken())) {
            return;
        }
        if (!secretKey.equals(participantRoomSession.getSecretRoomKey())) {
            return;
        }
        if (roomSize.getAndIncrement() > 4) {
            return;
        }
        reentrantLock.unlock();
        super.join(participantRoomSession);
    }

    private boolean isAuthorized(String userToken) {
        final Pattern compile = Pattern.compile(TOKEN_REGEX);
        final Matcher matcher = compile.matcher(userToken);
        return matcher.matches();
    }
}
