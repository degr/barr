import {roomApi} from '../../kurrento/conferenceroom';

const SET_ROOM_DATA = 'SET_ROOM_DATA';
const SET_ROOM_USERS = 'SET_ROOM_USERS';
const SET_LOCATION = 'SET_LOCATION';
let initialState = {
    roomKey: null,
    isPrivate: false,
    mainUser: null,
    location: null,
    users: []
};

const roomReducer = (state = initialState, action) => {
    let stateCopy = {...state};
    switch (action.type) {
        case SET_ROOM_DATA: {
            stateCopy.roomKey = action.payload.roomKey;
            stateCopy.isPrivate = action.payload.isPrivate;
            stateCopy.users = action.payload.users;
            break;
        }
        case SET_ROOM_USERS: {
            stateCopy.mainUser = action.mainUser;
            [...stateCopy.users] = action.users;
            break;
        }
        case SET_LOCATION: {
            stateCopy.location = action.location;
            break;
        }
        default: {
            stateCopy = state;
            break;
        }
    }
    return stateCopy;
};

export const setRoomData = (roomKey, isPrivate) => ({
    type: SET_ROOM_DATA,
    payload: {roomKey, isPrivate}
});
export const setRoomUsers = (mainUser, users) => ({
    type: SET_ROOM_USERS,
    mainUser,
    users
});
export const setLocation = (location) => ({
    type: SET_LOCATION,
    location
})
export const joinPrivateRoom = (login, token, roomKey) => {
    return (dispatch) => {
        roomApi.joinPrivateRoom(login, token, roomKey);
    }
};
export const joinPublicRoom = (login, roomKey) => {
    return (dispatch) => {
        roomApi.joinPublicRoom(login, roomKey)
    }
};
export default roomReducer;