import {getStore} from "../index";
import {setRoomData} from "../redux/reducers/roomReducer";
import {sendMessage} from "../kurrento/messageHandler";
import {setAuthUserData} from "../redux/reducers/authReducer";

export const roomApi = {
    joinPublicRoom(login, roomKey) {
        sendMessage({
            id: 'joinPublicRoom',
            login: login,
            roomKey: roomKey
        });
        setRoomInfo(roomKey, false);
    },

    joinPrivateRoom(login, token, roomKey) {
        sendMessage({
            id: 'joinPrivateRoom',
            login: login,
            token: token,
            roomKey: roomKey,
        });
        setRoomInfo(roomKey, true);
    },


};

function setRoomInfo(roomKey, isPrivate) {
    getStore().dispatch(setRoomData(roomKey, isPrivate));
}

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
    },
    authorize(payload) {
        getStore().dispatch(setAuthUserData(payload.id, payload.login, payload.token, payload.permissions, true));
    }
};