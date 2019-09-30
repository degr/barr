import {authApi} from "../api/api";
import {
    addIceCandidate,
    onExistingParticipants,
    onNewParticipant,
    onParticipantLeft,
    receiveVideoResponse
} from "./conferenceroom";

let ws = new WebSocket('wss://10.10.9.18/groupcall');

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
            authApi.authorize(parsedMessage.payload);
            break;
        case 'signUp':
            authApi.authorize(parsedMessage.payload);
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }
};

export function closeConnection() {
    ws.close();
}

export function sendMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Senging message: ' + jsonMessage);
    ws.send(jsonMessage);
}