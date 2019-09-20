package org.kurento.tutorial.groupcall.websocket;

import org.kurento.tutorial.groupcall.websocket.command.RoomCommand;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommandManager {
    private final Map<String, RoomCommand> commandMap;
    private final ApplicationContext applicationContext;

    public CommandManager(ApplicationContext appContext) {
        this.applicationContext = appContext;
        commandMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    private void init() {
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(Collections.singletonList(ClasspathHelper.forClass(RoomCommand.class))));
        Set<Class<? extends RoomCommand>> subTypesOf = reflections.getSubTypesOf(RoomCommand.class);
        subTypesOf.stream()
                .map(aClass -> aClass.getDeclaredAnnotation(Component.class))
                .filter(Objects::nonNull)
                .map(Component::value)
                .forEach(s -> commandMap.put(s, (RoomCommand) applicationContext.getBean(s)));
    }

    Optional<RoomCommand> getCommand(String commandId) {
        return Optional.ofNullable(commandMap.get(commandId));
    }
}