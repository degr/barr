import {Participant} from "./Participant";
import {getStore} from "../index";
import {addParticipant, removeParticipant, setRoomParticipants} from "../redux/reducers/roomReducer";
import {closeConnection, sendMessage} from "./messageHandler";

let kurentoUtils = require('kurento-utils');

export function addIceCandidate(parsedMessage) {
    let participant = findParticipantByName(parsedMessage.name);
    participant.rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
        if (error) {
            console.error("Error adding candidate: " + error);
        }
    });
}

export function onNewParticipant(parsedMessage) {
    let participant = new Participant(parsedMessage.name, parsedMessage.location);
    dispatch(addParticipant(participant));
    receiveVideo(participant);
}

export function receiveVideoResponse(parsedMessage) {
    let participant = findParticipantByName(parsedMessage.name);
    participant.rtcPeer.processAnswer(parsedMessage.sdpAnswer, function (error) {
        if (error) return console.error(error);
    });
}

export function onExistingParticipants(parsedMessage) {
    let name = getStore().getState().authPage.login;
    let location = getStore().getState().roomPage.location;
    let participant = new Participant(name, location);
    let participants = parsedMessage.data.map(item => new Participant(item.name, item.location));
    dispatch(setRoomParticipants(participant, participants));
    let constraints = {
        audio: true,
        video: false
    };
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

    participants.forEach(receiveVideo);
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

function receiveVideo(participant) {
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
    let participant = findParticipantByName(parsedMessage.name);
    dispatch(removeParticipant(participant));
    participant.dispose();
}

function dispatch(command) {
    getStore().dispatch(command);
}

function findParticipantByName(name) {
    let participants = getParticipants();
    let all = [...participants, getStore().getState().roomPage.mainParticipant];
    return all.find(participant => participant.name === name);
}

function getParticipants() {
    let roomPage = getStore().getState().roomPage;
    return roomPage.participants;
}