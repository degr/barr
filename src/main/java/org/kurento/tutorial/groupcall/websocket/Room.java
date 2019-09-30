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
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String DATA = "data";
    private static final String LOCATION = "location";
    private static final String NEW_PARTICIPANT_ARRIVED_COMMAND = "newParticipantArrived";
    private static final String PARTICIPANT_LEFT_COMMAND = "participantLeft";
    private static final String EXISTING_PARTICIPANTS_COMMAND = "existingParticipants";
    private final Map<SitPosition, UserSession> roomParticipantsSessions;
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

    public void join(SitPosition sitPosition, UserSession participantRoomSession) throws IOException {
        if (roomParticipantsSessions.size() + 1 > participantLimit) {
            throw new IllegalArgumentException("Unable to join room, User overhead. Room limit is " + participantLimit);
        }
        notifyRoomUsers(sitPosition, participantRoomSession);
        putToMap(sitPosition, participantRoomSession);
        sendParticipant(participantRoomSession);
    }

    private void putToMap(SitPosition newPosition, UserSession session) {
        boolean contains = roomParticipantsSessions.containsValue(session);
        if (contains) {
            SitPosition oldSitPosition = null;
            for (Map.Entry<SitPosition, UserSession> sitPositionUserSessionEntry : roomParticipantsSessions.entrySet()) {
                if (sitPositionUserSessionEntry.getValue().equals(session)) {
                    oldSitPosition = sitPositionUserSessionEntry.getKey();
                    break;
                }
            }
            roomParticipantsSessions.remove(oldSitPosition);
            roomParticipantsSessions.put(newPosition, session);
        } else {
            roomParticipantsSessions.put(newPosition, session);
        }
    }

    public void leave(UserSession user) {
        log.debug("PARTICIPANT {}: Leaving room {}", user.getLogin(), this.roomKey);
        this.removeParticipant(user.getLogin());
        user.close();
    }

    private void notifyRoomUsers(SitPosition sitPosition, UserSession newParticipantSession) {
        final JsonObject newParticipantMsg = new JsonObject();
        JsonElement jsonLocation = getJsonLocation(sitPosition);
        newParticipantMsg.addProperty(ID, NEW_PARTICIPANT_ARRIVED_COMMAND);
        newParticipantMsg.addProperty(NAME, newParticipantSession.getLogin());
        newParticipantMsg.add(LOCATION, jsonLocation);

        for (final UserSession userSession : roomParticipantsSessions.values()) {
            try {
                userSession.sendMessage(newParticipantMsg);
            } catch (final IOException e) {
                log.debug("ROOM {}: participant {} could not be notified", roomKey, userSession.getLogin(), e);
            }
        }
    }

    private void removeParticipant(String userSessionName) {
        roomParticipantsSessions.values()
                .removeIf(userSession -> userSession.getLogin().equals(userSessionName));

        final JsonObject participantLeftJson = new JsonObject();


        participantLeftJson.addProperty(ID, PARTICIPANT_LEFT_COMMAND);
        participantLeftJson.addProperty(NAME, userSessionName);

        for (final UserSession participant : roomParticipantsSessions.values()) {
            try {
                participant.cancelVideoFrom(userSessionName);
                participant.sendMessage(participantLeftJson);
            } catch (final IOException e) {
                log.debug(e.getMessage());
            }
        }
    }

    private void sendParticipant(UserSession userSession) throws IOException {
        final JsonArray participantsArray = new JsonArray();
        for (Map.Entry<SitPosition, UserSession> entry : roomParticipantsSessions.entrySet()) {
            UserSession value = entry.getValue();
            if (!value.equals(userSession)) {
                final JsonElement participantName = new JsonPrimitive(value.getLogin());
                JsonObject jsonObject = new JsonObject();
                jsonObject.add(NAME, participantName);
                jsonObject.add(LOCATION, getJsonLocation(entry.getKey()));
                participantsArray.add(jsonObject);
            }
        }
        final JsonObject existingParticipantsMsg = new JsonObject();
        existingParticipantsMsg.addProperty(ID, EXISTING_PARTICIPANTS_COMMAND);
        existingParticipantsMsg.add(DATA, participantsArray);
        userSession.sendMessage(existingParticipantsMsg);
    }

    private JsonElement getJsonLocation(SitPosition sitPosition) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("x", sitPosition.getAxisX());
        jsonObject.addProperty("y", sitPosition.getAxisY());
        jsonObject.addProperty("z", sitPosition.getAxisZ());
        jsonObject.addProperty("a", sitPosition.getRotationAngle());
        jsonObject.addProperty("type", roomKey);
        return jsonObject;
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