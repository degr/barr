package org.kurento.tutorial.groupcall.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    private String id;
    private String sender;
    private String sdpOffer;
    private String name;
    private String room;
    private CandidateDto candidate;

}
