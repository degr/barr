import {getStore} from "../index";
import {sendMessage} from "../kurrento/messageHandler";
import {setAuthUserData} from "../redux/reducers/authReducer";

export const roomApi = {
    joinPublicRoom(login, roomKey, location) {
        sendMessage({
            id: 'joinPublicRoom',
            login,
            roomKey,
            location
        });
    },

    joinPrivateRoom(login, token, roomKey, location) {
        sendMessage({
            id: 'joinPrivateRoom',
            login,
            token,
            roomKey,
            location
        });
    },


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
    },
    authorize(payload) {
        getStore().dispatch(setAuthUserData(payload.id, payload.login, payload.token, payload.permissions, true));
    }
};