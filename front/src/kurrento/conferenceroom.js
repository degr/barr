import {Participant} from "./Participant";
import {getStore} from "../index";
import {setRoomParticipants} from "../redux/reducers/roomReducer";
import {closeConnection, sendMessage} from "./messageHandler";

let kurentoUtils = require('kurento-utils');


let participants = {};
let name;

export function addIceCandidate(parsedMessage) {
    participants[parsedMessage.name].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
        if (error) {
            console.error("Error adding candidate: " + error);
        }
    });
}

export function onNewParticipant(parsedMessage) {
    receiveVideo(parsedMessage.name);
}

export function receiveVideoResponse(parsedMessage) {
    participants[parsedMessage.name].rtcPeer.processAnswer(parsedMessage.sdpAnswer, function (error) {
        if (error) return console.error(error);
    });
}

export function onExistingParticipants(parsedMessage) {
    let constraints = {
        audio: true,
        video: false
    };
    let participant = new Participant(name);
    participants[name] = participant;
    let video = participant.getVideoElement();

    let options = {
        localVideo: video,
        mediaConstraints: constraints,
        configuration: {
            iceServers: [{urls: 'turn:134.209.199.255', username: 'test', credential: 'test'}],
            iceTransportPolicy: 'relay'
        },
        onicecandidate: participant.onIceCandidate.bind(participant)
    };
    participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerSendonly(options,
        function (error) {
            if (error) {
                return console.error(error);
            }
            this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        });

    parsedMessage.data.forEach(receiveVideo);
    getStore().dispatch(setRoomParticipants(name, parsedMessage.data))
}

export function leaveRoom() {
    sendMessage({
        id: 'leaveRoom'
    });

    for (let key in participants) {
        participants[key].dispose();
    }
    document.getElementById('join').style.display = 'block';
    document.getElementById('room').style.display = 'none';
    closeConnection();
}

function receiveVideo(sender) {
    let participant = new Participant(sender);
    participants[sender] = participant;
    let video = participant.getVideoElement();

    let options = {
        remoteVideo: video,
        mediaConstraints: {
            audio: true,
            video: false
        },
        configuration: {
            iceServers: [{urls: 'turn:134.209.199.255', username: 'test', credential: 'test'}],
            iceTransportPolicy: 'relay'
        },
        onicecandidate: participant.onIceCandidate.bind(participant)
    };
    participant.rtcPeer = new kurentoUtils.WebRtcPeer.WebRtcPeerRecvonly(options,
        function (error) {
            if (error) {
                return console.error(error);
            }
            this.generateOffer(participant.offerToReceiveVideo.bind(participant));
        });
}

export function onParticipantLeft(parsedMessage) {
    console.log('Participant ' + parsedMessage.name + ' left');
    let participant = participants[parsedMessage.name];
    participant.dispose();
    delete participants[parsedMessage.name];
}