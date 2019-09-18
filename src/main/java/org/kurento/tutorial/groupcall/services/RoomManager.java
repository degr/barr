/* (C) Copyright 2014 Kurento (http://kurento.org/) Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.kurento.tutorial.groupcall.services;

import lombok.extern.slf4j.Slf4j;
import org.kurento.client.KurentoClient;
import org.kurento.tutorial.groupcall.websocket.PrivateRoom;
import org.kurento.tutorial.groupcall.websocket.Room;

import javax.annotation.PostConstruct;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class RoomManager {
    private final ConcurrentMap<String, Room> rooms;
    private KurentoClient kurento;

    public RoomManager(KurentoClient kurento) {
        this.kurento = kurento;
        rooms = new ConcurrentHashMap<>();
    }

    @PostConstruct
    private void init() {
        createRoom("bar", 10);
        createRoom("table1", 4);
        createRoom("table2", 4);
        createRoom("sofa", 6);
    }

    private void createRoom(String roomKey, int participantLimit) {
        Room room = new Room(roomKey, participantLimit, kurento.createMediaPipeline());
        rooms.put(roomKey, room);
    }

    public Room createPrivateRoom(String privateRoomKey) {
        if (!rooms.containsKey(privateRoomKey)) {
            rooms.put(privateRoomKey, new PrivateRoom(privateRoomKey, kurento.createMediaPipeline()));
        }
        return rooms.get(privateRoomKey);
    }

    public Room getRoom(String roomKey) {
        return rooms.get(roomKey);
    }

    public void removeRoom(Room room) {
        Optional.ofNullable(room)
                .map(Room::getRoomKey)
                .map(rooms::remove)
                .ifPresent(Room::close);
    }
}