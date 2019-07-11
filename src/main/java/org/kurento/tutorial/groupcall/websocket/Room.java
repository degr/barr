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
import lombok.extern.slf4j.Slf4j;
import org.kurento.client.Continuation;
import org.kurento.client.MediaPipeline;

import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Room implements Closeable {
    private final Map<String, UserSession> roomParticipantsSessions;
    @Getter
    private final MediaPipeline mediaPipeline;

    @Getter
    private final String roomKey;

    private final int participantLimit;

    public Room(String roomKey, int participantLimit, MediaPipeline mediaPipeline) {
        this.roomKey = roomKey;
        this.participantLimit = participantLimit;
        this.mediaPipeline = mediaPipeline;
        roomParticipantsSessions = new ConcurrentHashMap<>();
    }

    public void join(UserSession participantRoomSession) throws IOException {
        if (roomParticipantsSessions.size() + 1 > participantLimit) {
            throw new IllegalArgumentException("Unable to join room, User overhead. Room limit is " + participantLimit);
        }
        notifyRoomUsers(participantRoomSession);
        roomParticipantsSessions.put(participantRoomSession.getLogin(), participantRoomSession);
        sendParticipantNames(participantRoomSession);
    }

    public void leave(UserSession user) {
        log.debug("PARTICIPANT {}: Leaving room {}", user.getLogin(), this.roomKey);

        this.removeParticipant(user.getLogin());
        user.close();
    }

    private void notifyRoomUsers(UserSession newParticipantSession) {
        final JsonObject newParticipantMsg = new JsonObject();
        newParticipantMsg.addProperty("id", "newParticipantArrived");
        newParticipantMsg.addProperty("name", newParticipantSession.getLogin());

        for (final UserSession userSession : roomParticipantsSessions.values()) {
            try {
                userSession.sendMessage(newParticipantMsg);
            } catch (final IOException e) {
                log.debug("ROOM {}: participant {} could not be notified", roomKey, userSession.getLogin(), e);
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
                final JsonElement participantName = new JsonPrimitive(participantSession.getLogin());
                participantsArray.add(participantName);
            }
        }
        final JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty("id", "existingParticipants");
        existingParticipantsMsg.add("data", participantsArray);
        userSession.sendMessage(existingParticipantsMsg);
    }

    public Collection<UserSession> getRoomParticipantsSessions() {
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
                log.trace("ROOM {}: Released Pipeline", Room.this.roomKey);
            }

            @Override
            public void onError(Throwable cause) {
                log.warn("PARTICIPANT {}: Could not release Pipeline", Room.this.roomKey);
            }
        });
        log.debug("Room {} closed", this.roomKey);
    }

    @PreDestroy
    private void shutdown() {
        this.close();
    }
}