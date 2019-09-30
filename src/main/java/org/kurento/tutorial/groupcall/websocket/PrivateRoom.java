package org.kurento.tutorial.groupcall.websocket;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.util.Strings;
import org.kurento.client.MediaPipeline;

import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrivateRoom extends Room {
    private static final String TOKEN_REGEX = "^[A-Za-z0-9-_=]+\\.[A-Za-z0-9-_=]+\\.?[A-Za-z0-9-_.+/=]*$";
    private static final String AUTHORITIES_KEY = "authorities";
    private static final String SUBJECT_KEY = "sub";
    private ReentrantLock reentrantLock = new ReentrantLock();

    public PrivateRoom(String roomKey, MediaPipeline mediaPipeline) {
        super(DigestUtils.md5Hex(roomKey.getBytes()), 4, mediaPipeline);
    }

    @Override
    public void join(SitPosition sitPosition, UserSession participantRoomSession) throws IOException {
        reentrantLock.lock();
        if (!isSecretValid(participantRoomSession.getRoomKey())) {
            return;
        }
        if (!isUserAuthorized(participantRoomSession.getLogin(), participantRoomSession.getToken())) {
            return;
        }
        reentrantLock.unlock();
        super.join(sitPosition, participantRoomSession);
    }

    private boolean isSecretValid(String roomKey) {
        if (roomKey == null) {
            return false;
        }
        return getRoomKey().equals(roomKey);
    }

    private boolean isUserAuthorized(String userName, String login) {
        if (Strings.isBlank(login)) {
            return false;
        }
        final Pattern compile = Pattern.compile(TOKEN_REGEX);
        final Matcher matcher = compile.matcher(login);

        if (!matcher.matches()) {
            return false;
        }
        DecodedJWT decode = JWT.decode(login);

        Claim sub = decode.getClaim(SUBJECT_KEY);
        if (sub == null) {
            return false;
        }
        if (!sub.asString().equals(userName)) {
            return false;
        }
        return decode.getClaim(AUTHORITIES_KEY) != null;
    }
}