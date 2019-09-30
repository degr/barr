import {sendMessage} from "./messageHandler";

export function Participant(name, location) {
    this.name = name;
    this.location = location;
    let video = document.createElement('video');
    this.rtcPeer = null;
    video.id = 'video-' + this.name;
    video.autoplay = true;
    video.controls = false;

    this.getVideoElement = function () {
        return video;
    };
    this.getLocation = function () {
        return this.location;
    };

    this.offerToReceiveVideo = function (error, offerSdp) {
        if (error) return console.error("sdp offer error");
        console.log('Invoking SDP offer callback function');
        sendMessage({
            id: "receiveVideoFrom",
            sender: this.name,
            sdpOffer: offerSdp
        });
    };

    this.onIceCandidate = function (candidate) {
        console.log("Local candidate" + JSON.stringify(candidate));
        sendMessage({
            id: 'onIceCandidate',
            candidate: candidate,
            name: this.name
        });
    };

    Object.defineProperty(this, 'rtcPeer', {writable: true});

    this.dispose = function () {
        console.log('Disposing participant ' + this.name);
        this.rtcPeer.dispose();
    };
}