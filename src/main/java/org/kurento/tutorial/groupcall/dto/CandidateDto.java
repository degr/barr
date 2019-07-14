package org.kurento.tutorial.groupcall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateDto {
    private String candidate;
    private String sdpMid;
    private int sdpMLineIndex;
}
