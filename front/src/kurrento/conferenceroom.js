let ws = new WebSocket('wss://' + window.location.host + '/groupcall');
let participants = {};
let name;

window.onbeforeunload = function () {
    ws.close();
};

ws.onmessage = function (message) {
    let parsedMessage = JSON.parse(message.data);
    console.info('Received message: ' + message.data);

    /*switch (parsedMessage.id) {
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
            participants[parsedMessage.name].rtcPeer.addIceCandidate(parsedMessage.candidate, function (error) {
                if (error) {
                    console.error("Error adding candidate: " + error);
                }
            });
            break;
        default:
            console.error('Unrecognized message', parsedMessage);
    }*/
};

export function register() {
    /*name = document.getElementById('name').value;
    let password = document.getElementById('password').value;
    let isPrivate = !!document.getElementById('isPrivateRoom').checked;
    let roomSelector = document.getElementById('roomSelector');

    let secretRoomKey = document.getElementById('secretRoomKey').value;
    let selectorValue = roomSelector.options[roomSelector.selectedIndex].value;
    let roomName;
    let secretKey;
    if (secretRoomKey === "") {
        secretKey = selectorValue;
        roomName = selectorValue;
    } else {
        secretKey = secretRoomKey;
        roomName = "Private room";
    }

    document.getElementById('room-header').innerText = roomName;
    document.getElementById('join').style.display = 'none';
    document.getElementById('room').style.display = 'block';*/

    sendMessage({
        id: 'joinRoom',
        name: 'name',
        password: 'password',
        roomKey: 'bar',
        isPrivateRoom: 'isPrivate',
    });
}

/*function onNewParticipant(request) {
    receiveVideo(request.name);
}

function receiveVideoResponse(result) {
    participants[result.name].rtcPeer.processAnswer(result.sdpAnswer, function (error) {
        if (error) return console.error(error);
    });
}

function onExistingParticipants(msg) {
    let constraints = {
        audio: true,
        video: false/!*{
            mandatory : {
                maxWidth : 320,
                maxFrameRate : 15,
                minFrameRate : 15
            }
        }*!/
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

    msg.data.forEach(receiveVideo);
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
            video: false/!*{
            mandatory : {
                maxWidth : 320,
                maxFrameRate : 15,
                minFrameRate : 15
            }
        }*!/
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

function onParticipantLeft(request) {
    console.log('Participant ' + request.name + ' left');
    let participant = participants[request.name];
    participant.dispose();
    delete participants[request.name];
}*/

function sendMessage(message) {
    let jsonMessage = JSON.stringify(message);
    console.log('Senging message: ' + jsonMessage);
    ws.send(jsonMessage);
}

/*
function showPrivateOptions() {
    let isPrivate = document.getElementById('isPrivateRoom');
    let userPassword = document.getElementById('password');

    let secret = document.getElementById('secretRoomKey');
    let selectors = document.getElementById('selectors');

    if (isPrivate.checked) {
        secret.style.visibility = "visible";
        selectors.style.visibility = "hidden";
        userPassword.style.visibility = "visible"
    } else {
        secret.style.visibility = "hidden";
        selectors.style.visibility = "visible";
        userPassword.style.visibility = "hidden"
    }
}*/
