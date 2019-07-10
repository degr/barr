package org.kurento.tutorial.groupcall.websocket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import org.kurento.client.MediaPipeline;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateRoom extends Room {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
    private static final String AUTHORITIES_KEY = "authorities";
    private static final String SUBJECT_KEY = "sub";
    private final String secretKey;
    private ReentrantLock reentrantLock = new ReentrantLock();

    public PrivateRoom(String roomName, MediaPipeline mediaPipeline, String secretKey) {
        super(roomName, 4, mediaPipeline);
        this.secretKey = DigestUtils.md5Hex(secretKey);
    }

    @Override
    public void join(UserSession participantRoomSession) throws IOException {
        reentrantLock.lock();
        if (!isSecretValid(participantRoomSession.getSecretRoomKey())) {
            return;
        }
        if (!isUserAuthorized(participantRoomSession.getLogin(), participantRoomSession.getToken())) {
            return;
        }
        reentrantLock.unlock();
        super.join(participantRoomSession);
    }

    private boolean isSecretValid(String userSecret) {
        if (userSecret == null) {
            return false;
        }
        return secretKey.equals(DigestUtils.md5Hex(userSecret));
    }

    private boolean isUserAuthorized(String userName, String userToken) {
        final Pattern compile = Pattern.compile(TOKEN_REGEX);
        final Matcher matcher = compile.matcher(userToken);
        boolean matches = matcher.matches();
        if (!matches) {
            return false;
        }
        DecodedJWT decode = JWT.decode(userToken);

        Claim sub = decode.getClaim(SUBJECT_KEY);
        if (sub == null) {
            return false;
        }
        if (!sub.asString().equals(userName)) {
            return false;
        }
        if (decode.getClaim(AUTHORITIES_KEY) == null) {
            return false;
        }
        return true;
    }
}