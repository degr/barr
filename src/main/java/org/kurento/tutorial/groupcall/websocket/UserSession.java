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
import lombok.Setter;
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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Ivan Gracia (izanmail@gmail.com)
 * @since 4.3.1
 */
public class UserSession implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(UserSession.class);
    @Getter
    private final String name;
    @Getter
    private final String roomName;
    @Getter
    private final WebSocketSession session;
    @Getter
    private final WebRtcEndpoint outgoingWebRtcPeer;
    private final MediaPipeline pipeline;
    @Getter
    @Setter
    private Set<String> authorities;

    private final ConcurrentMap<String, WebRtcEndpoint> incomingMedia;

    UserSession(final String userName, String roomName, final WebSocketSession webSocketSession, MediaPipeline mediaPipeline) {
        this.name = userName;
        this.roomName = roomName;
        this.session = webSocketSession;
        this.pipeline = mediaPipeline;

        authorities = new HashSet<>();
        incomingMedia = new ConcurrentHashMap<>();

        this.outgoingWebRtcPeer = new WebRtcEndpoint.Builder(mediaPipeline).build();

        this.outgoingWebRtcPeer.addIceCandidateFoundListener(event -> {
            JsonObject response = new JsonObject();
            response.addProperty("id", "iceCandidate");
            response.addProperty("name", userName);
            response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
            try {
                synchronized (webSocketSession) {
                    webSocketSession.sendMessage(new TextMessage(response.toString()));
                }
            } catch (IOException e) {
                log.debug(e.getMessage());
            }
        });
    }

    void receiveVideoFrom(UserSession sender, String sdpOffer) throws IOException {
        final String ipSdpAnswer = this.getEndpointForUser(sender).processOffer(sdpOffer);
        final JsonObject scParams = new JsonObject();
        scParams.addProperty("id", "receiveVideoAnswer");
        scParams.addProperty("name", sender.getName());
        scParams.addProperty("sdpAnswer", ipSdpAnswer);

        this.sendMessage(scParams);
        this.getEndpointForUser(sender).gatherCandidates();
    }

    private WebRtcEndpoint getEndpointForUser(final UserSession sender) {
        if (sender.getName().equals(name)) {
            return outgoingWebRtcPeer;
        }
        WebRtcEndpoint incoming = incomingMedia.get(sender.getName());
        if (incoming == null) {
            incoming = new WebRtcEndpoint.Builder(pipeline).build();

            incoming.addIceCandidateFoundListener(event -> {
                JsonObject response = new JsonObject();
                response.addProperty("id", "iceCandidate");
                response.addProperty("name", sender.getName());
                response.add("candidate", JsonUtils.toJsonObject(event.getCandidate()));
                try {
                    synchronized (session) {
                        session.sendMessage(new TextMessage(response.toString()));
                    }
                } catch (IOException e) {
                    log.debug(e.getMessage());
                }
            });

            incomingMedia.put(sender.getName(), incoming);
        }
        sender.getOutgoingWebRtcPeer().connect(incoming);

        return incoming;
    }

    public void cancelVideoFrom(final UserSession sender) {
        this.cancelVideoFrom(sender.getName());
    }

    void cancelVideoFrom(final String senderName) {
        log.debug("PARTICIPANT {}: canceling video reception from {}", this.name, senderName);
        final WebRtcEndpoint incoming = incomingMedia.remove(senderName);

        log.debug("PARTICIPANT {}: removing endpoint for {}", this.name, senderName);
        incoming.release(new Continuation<Void>() {
            @Override
            public void onSuccess(Void result) {
                log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                        UserSession.this.name, senderName);
            }

            @Override
            public void onError(Throwable cause) {
                log.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserSession.this.name,
                        senderName);
            }
        });
    }

    @Override
    public void close() {
        log.debug("PARTICIPANT {}: Releasing resources", this.name);
        for (final String remoteParticipantName : incomingMedia.keySet()) {

            log.trace("PARTICIPANT {}: Released incoming EP for {}", this.name, remoteParticipantName);

            final WebRtcEndpoint ep = this.incomingMedia.get(remoteParticipantName);

            ep.release(new Continuation<Void>() {

                @Override
                public void onSuccess(Void result) {
                    log.trace("PARTICIPANT {}: Released successfully incoming EP for {}",
                            UserSession.this.name, remoteParticipantName);
                }

                @Override
                public void onError(Throwable cause) {
                    log.warn("PARTICIPANT {}: Could not release incoming EP for {}", UserSession.this.name,
                            remoteParticipantName);
                }
            });
        }

        outgoingWebRtcPeer.release(new Continuation<Void>() {

            @Override
            public void onSuccess(Void result) {
                log.trace("PARTICIPANT {}: Released outgoing EP", UserSession.this.name);
            }

            @Override
            public void onError(Throwable cause) {
                log.warn("USER {}: Could not release outgoing EP", UserSession.this.name);
            }
        });
    }

    void sendMessage(JsonObject message) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(message.toString()));
        }
    }
    void addCandidate(IceCandidate candidate, String name) {
        if (this.name.compareTo(name) == 0) {
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
        boolean eq = name.equals(other.name);
        eq &= roomName.equals(other.roomName);
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
        result = 31 * result + name.hashCode();
        result = 31 * result + roomName.hashCode();
        return result;
    }
}
