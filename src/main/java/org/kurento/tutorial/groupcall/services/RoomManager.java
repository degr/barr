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
package org.kurento.tutorial.groupcall.services;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.kurento.client.KurentoClient;
import org.kurento.tutorial.groupcall.websocket.PrivateRoom;
import org.kurento.tutorial.groupcall.websocket.Room;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class RoomManager {
    private KurentoClient kurento;

    public RoomManager(KurentoClient kurento) {
        this.kurento = kurento;
    }

    private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();

    public Room createRoom(String roomName, int participantLimit) {
        Room room = new Room(roomName, participantLimit, kurento.createMediaPipeline());
        rooms.put(roomName, room);
        return room;
    }

    public Room createPrivateRoom(String roomName, String secretKey) {
        PrivateRoom privateRoom = new PrivateRoom(roomName, kurento.createMediaPipeline(), secretKey);
        if (Strings.isBlank(roomName)) {
            throw new UnsupportedOperationException("Unable to create unnamed private room");
        }
        if (Strings.isBlank(secretKey)) {
            throw new UnsupportedOperationException("Unable to create unsecured private room with empty key");
        }
        rooms.put(roomName, privateRoom);
        return privateRoom;
    }

    public Room getRoom(String roomName) {
        return rooms.get(roomName);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room.getName());
        room.close();
    }
}