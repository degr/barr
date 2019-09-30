package org.kurento.tutorial.groupcall.websocket;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Builder
@Getter
@EqualsAndHashCode
public class SitPosition {
    private double axisX;
    private double axisY;
    private double axisZ;
    private double rotationAngle;
}
