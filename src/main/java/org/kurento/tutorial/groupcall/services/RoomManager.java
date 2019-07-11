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

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class RoomManager {
    private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();
    private KurentoClient kurento;

    public RoomManager(KurentoClient kurento) {
        this.kurento = kurento;
    }

    @PostConstruct
    private void init() {
        String roomKey1 = "Room1";
        String roomKey2 = "Room2";
        String roomKey3 = "Room3";
        createRoom(roomKey1, 4);
        createRoom(roomKey2, 6);
        createRoom(roomKey3, 8);
    }

    private void createRoom(String roomKey, int participantLimit) {
        Room room = new Room(roomKey, participantLimit, kurento.createMediaPipeline());
        rooms.put(roomKey, room);
    }

    public Room createPrivateRoom(String privateRoomKey) {
        if (Strings.isBlank(privateRoomKey)) {
            throw new UnsupportedOperationException("Unable to create unsecured private room with empty key");
        }
        PrivateRoom privateRoom = new PrivateRoom(privateRoomKey, kurento.createMediaPipeline());
        rooms.put(privateRoom.getRoomKey(), privateRoom);
        return privateRoom;
    }

    public Room getRoom(String roomKey) {
        return rooms.get(roomKey);
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room.getRoomKey());
        room.close();
    }
}