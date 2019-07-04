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
import org.kurento.client.KurentoClient;
import org.kurento.tutorial.groupcall.websocket.Room;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class RoomManager {
    private KurentoClient kurento;

    public RoomManager(KurentoClient kurento) {
        this.kurento = kurento;
    }

    private final ConcurrentMap<String, Room> rooms = new ConcurrentHashMap<>();

    public Room getRoom(String roomName) {
        log.debug("Searching for room {}", roomName);
        Room room = Optional.ofNullable(rooms.get(roomName)).orElseGet(
                () -> {
                    log.debug("Room {} not existent. Will create now!", roomName);
                    Room room1 = new Room(roomName, kurento.createMediaPipeline());
                    rooms.put(roomName, room1);
                    return room1;
                });
        log.debug("Room {} found!", roomName);
        return room;
    }

    public void removeRoom(Room room) {
        this.rooms.remove(room.getName());
        room.close();
        log.info("Room {} removed and closed", room.getName());
    }
}