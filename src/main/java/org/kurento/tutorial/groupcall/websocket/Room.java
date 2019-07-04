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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Room implements Closeable {
    private final Map<String, UserSession> participants;
    private final MediaPipeline pipeline;
    private final String name;

    public String getName() {
        return name;
    }

    public Room(String roomName, MediaPipeline pipeline) {
        this.name = roomName;
        this.pipeline = pipeline;
        participants = new ConcurrentHashMap<>();
        log.info("ROOM {} has been created", roomName);
    }

    @PreDestroy
    private void shutdown() {
        this.close();
    }

    UserSession join(String userName, WebSocketSession session) throws IOException {
        log.info("ROOM {}: adding participant {}", userName, userName);
        final UserSession participant = new UserSession(userName, this.name, session, this.pipeline);
        joinRoom(participant);
        participants.put(participant.getName(), participant);
        sendParticipantNames(participant);
        return participant;
    }

    void leave(UserSession user) {
        log.debug("PARTICIPANT {}: Leaving room {}", user.getName(), this.name);
        this.removeParticipant(user.getName());
        user.close();
    }

    private void joinRoom(UserSession newParticipant) {
        final JsonObject newParticipantMsg = new JsonObject();
        newParticipantMsg.addProperty("id", "newParticipantArrived");
        newParticipantMsg.addProperty("name", newParticipant.getName());
        log.debug("ROOM {}: notifying other participants of new participant {}", name, newParticipant.getName());
        for (final UserSession participant : participants.values()) {
            try {
                participant.sendMessage(newParticipantMsg);
            } catch (final IOException e) {
                log.debug("ROOM {}: participant {} could not be notified", name, participant.getName(), e);
            }
        }
    }

    private void removeParticipant(String name) {
        participants.remove(name);
        log.debug("ROOM {}: notifying all users that {} is leaving the room", this.name, name);
        List<String> unmodifiedParticipants = new ArrayList<>();
        final JsonObject participantLeftJson = new JsonObject();
        participantLeftJson.addProperty("id", "participantLeft");
        participantLeftJson.addProperty("name", name);
        for (final UserSession participant : participants.values()) {
            try {
                participant.cancelVideoFrom(name);
                participant.sendMessage(participantLeftJson);
            } catch (final IOException e) {
                unmodifiedParticipants.add(participant.getName());
            }
        }
        if (!unmodifiedParticipants.isEmpty()) {
            log.debug("ROOM {}: The users {} could not be notified that {} left the room",
                    this.name, unmodifiedParticipants, name);
        }
    }

    private void sendParticipantNames(UserSession user) throws IOException {
        final JsonArray participantsArray = new JsonArray();
        for (final UserSession participant : this.getParticipants()) {
            if (!participant.equals(user)) {
                final JsonElement participantName = new JsonPrimitive(participant.getName());
                participantsArray.add(participantName);
            }
        }
        final JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty("id", "existingParticipants");
        existingParticipantsMsg.add("data", participantsArray);
        log.debug("PARTICIPANT {}: sending a list of {} participants", user.getName(), participantsArray.size());
        user.sendMessage(existingParticipantsMsg);
    }

    Collection<UserSession> getParticipants() {
        return participants.values();
    }

    @Override
    public void close() {
        for (final UserSession user : participants.values()) {
            user.close();
        }
        participants.clear();
        pipeline.release(new Continuation<Void>() {
            @Override
            public void onSuccess(Void result) {
                log.trace("ROOM {}: Released Pipeline", Room.this.name);
            }

            @Override
            public void onError(Throwable cause) {
                log.warn("PARTICIPANT {}: Could not release Pipeline", Room.this.name);
            }
        });
        log.debug("Room {} closed", this.name);
    }
}