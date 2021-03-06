/*
 * (C) Copyright 2014 Kurento (http://kurento.org/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.kurento.tutorial.groupcall.websocket;

import com.google.gson.JsonObject;
import lombok.Getter;
import org.kurento.client.Continuation;
import org.kurento.client.IceCandidate;
import org.kurento.client.MediaPipeline;
import org.kurento.client.WebRtcEndpoint;
import org.kurento.jsonrpc.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class UserSession implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserSession.class);
    @Getter
    private final String login;
    @Getter
    private final String token;
    @Getter
    private final String roomKey;
    @Getter
    private final WebSocketSession session;
    @Getter
    private final WebRtcEndpoint outgoingWebRtcPeer;
    private final MediaPipeline pipeline;

    private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia;

    public UserSession(final String login, String token, String roomKey, final WebSocketSession webSocketSession, MediaPipeline mediaPipeline) {
        this.login = login;
        this.token = token;
        this.roomKey = roomKey;
        this.session = webSocketSession;
        this.pipeline = mediaPipeline;
        incomingMedia = new ConcurrentHashMap<>();

        this.outgoingWebRtcPeer = new WebRtcEndpoint.Builder(mediaPipeline).build();

        this.outgoingWebRtcPeer.addIceCandidateFoundListener(event -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", "iceCandidate");
            response.addProperty("name", login);
            response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
            try {
                synchronized (session) {
                    session.sendMessage(new TextMessage(response.toString()));
                }
            } catch (IOException e) {
                LOGGER.debug(e.getMessage());
            }
        });
    }

    public void receiveVideoFrom(UserSession sender, String sdpOffer) throws IOException {
        final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);
        final JsonObject scParams = new JsonObject();
        scParams.addProperty("id", "receiveVideoAnswer");
        scParams.addProperty("name", sender.getLogin());
        scParams.addProperty("sdpAnswer", ipSdpAnswer);

        this.sendMessage(scParams);
        this.getEndpointForUser(sender).gatherCandidates();
    }

    private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
        if (sender.getLogin().equals(login)) {
            return outgoingWebRtcPeer;
        }
        WebRtcEndpoint incoming = incomingMedia.get(sender.getLogin());
        if (incoming == null) {
            incoming = new WebRtcEndpoint.Builder(pipeline).build();

            incoming.addIceCandidateFoundListener(event -> {
                JsonObject response = new JsonObject();
                response.addProperty("id", "iceCandidate");
                response.addProperty("name", sender.getLogin());
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e) {
                    LOGGER.debug(e.getMessage());
                }
            });

            incomingMedia.put(sender.getLogin(), incoming);
        }
        sender.getOutgoingWebRtcPeer().connect(incoming);

        return incoming;
    }

    public void cancelVideoFrom(final UserSession sender) {
        this.cancelVideoFrom(sender.getLogin());
    }

    void cancelVideoFrom(final String senderName) {
        LOGGER.debug("PARTICIPANT {}: canceling video reception from {}", this.login, senderName);
        Optional.ofNullable(incomingMedia.remove(senderName))
                .ifPresent(webRtcEndpoint -> webRtcEndpoint.release(new Continuation<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        LOGGER.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                                UserSession.this.login, senderName);
                    }

                    @Override
                    public void onError(Throwable cause) {
                        LOGGER.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserSession.this.login,
                                senderName);
                    }
                }));
        LOGGER.debug("PARTICIPANT {}: removing endpoint for {}", this.login, senderName);

    }

    @Override
    public void close() {
        LOGGER.debug("PARTICIPANT {}: Releasing resources", this.login);
        for (Map.Entry<String, WebRtcEndpoint> remoteParticipantEntry : incomingMedia.entrySet()) {

            final String participantName = remoteParticipantEntry.getKey();

            LOGGER.trace("PARTICIPANT {}: Released incoming EP for {}", this.login, participantName);


            final WebRtcEndpoint ep = remoteParticipantEntry.getValue();

            ep.release(new Continuation<Void>() {

                @Override
                public void onSuccess(Void result) {
                    LOGGER.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                            UserSession.this.login, participantName);
                }

                @Override
                public void onError(Throwable cause) {
                    LOGGER.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserSession.this.login,
                            participantName);
                }
            });
        }

        outgoingWebRtcPeer.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) {
                LOGGER.trace("PARTICIPANT {}: Released outgoing EP", UserSession.this.login);
            }

            @Override
            public void onError(Throwable cause) {
                LOGGER.warn("USER {}: Could not release outgoing EP", UserSession.this.login);
            }
        });
    }

    void sendMessage(JsonObject message) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(message.toString()));
        }
    }

    public void addCandidate(IceCandidate candidate, String name) {
        if (this.login.compareTo(name) == 0) {
            outgoingWebRtcPeer.addIceCandidate(candidate);
        } else {
            WebRtcEndpoint webRtc = incomingMedia.get(name);
            if (webRtc != null) {
                webRtc.addIceCandidate(candidate);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UserSession)) {
            return false;
        }
        UserSession other = (UserSession) obj;
        boolean eq = login.equals(other.login);
        eq &= roomKey.equals(other.roomKey);
        eq &= token.equals(other.token);
        return eq;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + login.hashCode();
        result = 31 * result + roomKey.hashCode();
        result = 31 * result + token.hashCode();
        return result;
    }
}
