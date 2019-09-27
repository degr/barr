import {Participant} from "./Participant";
import {getStore} from "../index";
import {addParticipant, removeParticipant, setRoomParticipants} from "../redux/reducers/roomReducer";
import {closeConnection, sendMessage} from "./messageHandler";

let kurentoUtils = require('kurento-utils');

export function addIceCandidate(parsedMessage) {
    debugger;
    let participant = findParticipantByName(parsedMessage.name);
    participant.rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
        if (error) {
            console.error("Error adding candidate: " + error);
        }
    });
}

export function onNewParticipant(parsedMessage) {
    receiveVideo(parsedMessage.name);
}

export function receiveVideoResponse(parsedMessage) {
    debugger;
    let participant = findParticipantByName(parsedMessage.name);
    participant.rtcPeer.processAnswer(parsedMessage.sdpAnswer, function (error) {
        if (error) return console.error(error);
    });
}

export function onExistingParticipants(parsedMessage) {
    let name = getStore().getState().authPage.login;
    dispatch(setRoomParticipants(name, parsedMessage.data));
    let constraints = {
        audio: true,
        video: false
    };
    let participant = new Participant(name);
    dispatch(addParticipant(participant));

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
}

export function leaveRoom() {
    sendMessage({
        id: 'leaveRoom'
    });
    let participants = getParticipants();
    for (let key in participants) {
        let participant = participants[key];
        participant.dispose();
        dispatch(removeParticipant(participant))
    }
    closeConnection();
}

function receiveVideo(sender) {
    let participant = new Participant(sender);
    dispatch(addParticipant(participant));
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
    debugger;
    console.log('Participant ' + parsedMessage.name + ' left');
    let participant = findParticipantByName(parsedMessage.name);

    participant.dispose();
    dispatch(removeParticipant(participant));
}

function dispatch(command) {
    getStore().dispatch(command);
}

function findParticipantByName(name) {
    debugger;
    let participants = getParticipants();
    return participants.find(participant => participant.name === name);
}

function getParticipants() {
    let roomPage = getStore().getState().roomPage;
    return [...roomPage.participants];
}