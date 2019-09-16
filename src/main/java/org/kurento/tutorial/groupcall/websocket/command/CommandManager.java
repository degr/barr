package org.kurento.tutorial.groupcall.websocket.command;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CommandManager {
    private final Map<String, RoomCommand> commandMap;

    public CommandManager(List<RoomCommand> commandList) {
        commandMap = new HashMap<>();
        commandList.forEach(command -> {
            Component componentAnnotation = command.getClass().getDeclaredAnnotation(Component.class);
            String commandId = Optional.ofNullable(componentAnnotation)
                    .map(Component::value)
                    .map(String::toUpperCase)
                    .orElseGet(() -> command.getClass().getSimpleName());
            commandMap.put(commandId, command);
        });
    }

    public Optional<RoomCommand> getCommand(String commandId) {
        String key = commandId.toUpperCase();
        return Optional.ofNullable(commandMap.get(key));
    }
}