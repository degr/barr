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
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Room implements Closeable {
    private final Map<String, UserSession> roomParticipantsSessions;
    @Getter
    private final MediaPipeline mediaPipeline;

    @Getter
    private final String name;

    @Setter
    @Getter
    private AtomicBoolean isPrivate;

    public Room(String roomName, MediaPipeline mediaPipeline) {
        this.name = roomName;
        isPrivate = new AtomicBoolean(false);
        this.mediaPipeline = mediaPipeline;
        roomParticipantsSessions = new ConcurrentHashMap<>();
    }

    void join(UserSession participantRoomSession) throws IOException {
        boolean isPrivateRoom = this.isPrivate.get();
        if (isPrivateRoom && roomParticipantsSessions.size() >= 4) {
            throw new RuntimeException("Unable to join room");
        }
        if (isPrivateRoom&&participantRoomSession.getAuthorities().isEmpty()) {
            throw new RuntimeException("Not authorized user");
        }
        notifyRoomUsers(participantRoomSession);
        roomParticipantsSessions.put(participantRoomSession.getName(), participantRoomSession);
        sendParticipantNames(participantRoomSession);
    }

    void leave(UserSession user) {
        log.debug("PARTICIPANT {}: Leaving room {}", user.getName(), this.name);

        this.removeParticipant(user.getName());
        user.close();
    }

    private void notifyRoomUsers(UserSession newParticipantSession) {
        final JsonObject newParticipantMsg = new JsonObject();
        newParticipantMsg.addProperty("id", "newParticipantArrived");
        newParticipantMsg.addProperty("name", newParticipantSession.getName());

        for (final UserSession userSession : roomParticipantsSessions.values()) {
            try {
                userSession.sendMessage(newParticipantMsg);
            } catch (final IOException e) {
                log.debug("ROOM {}: participant {} could not be notified", name, userSession.getName(), e);
            }
        }
    }

    private void removeParticipant(String userSessionName) {
        roomParticipantsSessions.remove(userSessionName);
        final JsonObject participantLeftJson = new JsonObject();

        participantLeftJson.addProperty("id", "participantLeft");
        participantLeftJson.addProperty("name", userSessionName);

        for (final UserSession participant : roomParticipantsSessions.values()) {
            try {
                participant.cancelVideoFrom(userSessionName);
                participant.sendMessage(participantLeftJson);
            } catch (final IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void sendParticipantNames(UserSession userSession) throws IOException {
        final JsonArray participantsArray = new JsonArray();
        for (final UserSession participantSession : this.getRoomParticipantsSessions()) {
            if (!participantSession.equals(userSession)) {
                final JsonElement participantName = new JsonPrimitive(participantSession.getName());
                participantsArray.add(participantName);
            }
        }
        final JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty("id", "existingParticipants");
        existingParticipantsMsg.add("data", participantsArray);
        userSession.sendMessage(existingParticipantsMsg);
    }

    Collection<UserSession> getRoomParticipantsSessions() {
        return roomParticipantsSessions.values();
    }

    @Override
    public void close() {
        for (final UserSession user : roomParticipantsSessions.values()) {
            user.close();
        }
        roomParticipantsSessions.clear();
        mediaPipeline.release(new Continuation<Void>() {
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

    @PreDestroy
    private void shutdown() {
        this.close();
    }
}