import {Participant} from "./Participant";
import {getStore} from "../index";
import {setAuthUserData} from "../redux/reducers/authReducer";

let kurentoUtils = require('kurento-utils');


let ws = new WebSocket('wss://localhost/groupcall');
let participants = {};
let name;

window.onbeforeunload = function () {
    ws.close();
};

ws.onmessage = function (message) {
    let parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);
    switch (parsedMessage.id) {
        case 'existingParticipants':
            onExistingParticipants(parsedMessage);
            break;
        case 'newParticipantArrived':
            onNewParticipant(parsedMessage);
            break;
        case 'participantLeft':
            onParticipantLeft(parsedMessage);
            break;
        case 'receiveVideoAnswer':
            receiveVideoResponse(parsedMessage);
            break;
        case 'iceCandidate':
            addIceCandidate(parsedMessage);
            break;
        case 'signIn':
            authorize(parsedMessage.payload);
            break;
        case 'signUp':
            authorize(parsedMessage.payload);
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
};

function addIceCandidate(parsedMessage) {
    participants[parsedMessage.name].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
        if (error) {
            console.error("Error adding candidate: " + error);
        }
    });
}

function authorize(payload) {
    getStore().dispatch(setAuthUserData(payload.id, payload.login, payload.token, payload.permissions, true));
}

export const joinRoomApi = {
    joinPublicRoom(login, roomKey) {
        sendMessage({
            id: 'joinPublicRoom',
            login: login,
            roomKey: roomKey
        });
    },

    joinPrivateRoom(login, token, roomKey) {
        sendMessage({
            id: 'joinPrivateRoom',
            login: login,
            token: token,
            roomKey: roomKey,
        });
    }
};
export const authApi = {

    signIn(login, password) {
        sendMessage({
            id: 'signIn',
            login: login,
            password: password
        })
    },

    signUp(login, password) {
        sendMessage({
            id: 'signUp',
            login: login,
            password: password
        })
    }
};

function onNewParticipant(parsedMessage) {
    receiveVideo(parsedMessage.name);
}

function receiveVideoResponse(parsedMessage) {
    participants[parsedMessage.name].rtcPeer.processAnswer(parsedMessage.sdpAnswer, function (error) {
        if (error) return console.error(error);
    });
}

function onExistingParticipants(parsedMessage) {
    let constraints = {
        audio: true,
        video: false/*{
            mandatory : {
                maxWidth : 320,
                maxFrameRate : 15,
                minFrameRate : 15
            }
        }*/
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
}

function leaveRoom() {
    sendMessage({
        id: 'leaveRoom'
    });

    for (let key in participants) {
        participants[key].dispose();
    }

    document.getElementById('join').style.display = 'block';
    document.getElementById('room').style.display = 'none';

    ws.close();
}

function receiveVideo(sender) {
    let participant = new Participant(sender);
    participants[sender] = participant;
    let video = participant.getVideoElement();

    let options = {
        remoteVideo: video,
        mediaConstraints: {
            audio: true,
            video: false/*{
            mandatory : {
                maxWidth : 320,
                maxFrameRate : 15,
                minFrameRate : 15
            }
        }*/
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

function onParticipantLeft(parsedMessage) {
    console.log('Participant ' + parsedMessage.name + ' left');
    let participant = participants[parsedMessage.name];
    participant.dispose();
    delete participants[parsedMessage.name];
}

export function sendMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Senging message: ' + jsonMessage);
    ws.send(jsonMessage);
}