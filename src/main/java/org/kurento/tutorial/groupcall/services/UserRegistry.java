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

import org.kurento.tutorial.groupcall.websocket.UserSession;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;


public class UserRegistry {

    private final ConcurrentHashMap<String, UserSession> userSessionsByName;
    private final ConcurrentHashMap<String, UserSession> userSessionsById;

    public UserRegistry() {
        userSessionsByName = new ConcurrentHashMap<>();
        userSessionsById = new ConcurrentHashMap<>();
    }

    public void register(UserSession user) {
        userSessionsByName.put(user.getLogin(), user);
        userSessionsById.put(user.getSession().getId(), user);
    }

    public UserSession getByName(String name) {
        return userSessionsByName.get(name);
    }

    public UserSession getBySession(WebSocketSession session) {
        return userSessionsById.get(session.getId());
    }

    public UserSession removeBySession(WebSocketSession session) {
        final UserSession user = getBySession(session);
        userSessionsByName.remove(user.getLogin());
        userSessionsById.remove(session.getId());
        return user;
    }
}
