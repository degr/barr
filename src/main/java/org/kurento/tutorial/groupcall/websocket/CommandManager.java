package org.kurento.tutorial.groupcall.websocket;

import org.apache.commons.lang3.StringUtils;
import org.kurento.tutorial.groupcall.websocket.command.RoomCommand;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CommandManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(CommandManager.class);
    private final Map<String, RoomCommand> commandMap;
    private final ApplicationContext applicationContext;

    public CommandManager(ApplicationContext appContext) {
        this.applicationContext = appContext;
        commandMap = new ConcurrentHashMap<>();
    }

    @PostConstruct
    private void scan() {
        LOGGER.debug("Scanning commands...");
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(Collections.singletonList(ClasspathHelper.forClass(RoomCommand.class))));
        Set<Class<? extends RoomCommand>> subTypesOf = reflections.getSubTypesOf(RoomCommand.class);
        subTypesOf.stream()
                .filter(aClass -> aClass.isAnnotationPresent(Component.class))
                .map(aClass -> aClass.getDeclaredAnnotation(Component.class))
                .map(Component::value)
                .filter(StringUtils::isNotBlank)
                .forEach(s -> {
                    RoomCommand bean = (RoomCommand) applicationContext.getBean(s);
                    commandMap.put(s, bean);
                    LOGGER.debug("Available command: {} with id: {}", bean, s);
                });
        LOGGER.info("Found {} commands", commandMap.size());
    }

    Optional<RoomCommand> getCommand(String commandId) {
        return Optional.ofNullable(commandMap.get(commandId));
    }
}