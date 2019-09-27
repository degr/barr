import {roomApi} from "../../api/api";

const SET_ROOM_DATA = 'SET_ROOM_DATA';
const SET_ROOM_PARTICIPANTS = 'SET_ROOM_PARTICIPANTS';
const SET_LOCATION = 'SET_LOCATION';
let initialState = {
    roomKey: null,
    isPrivate: false,
    mainParticipant: null,
    participants: [],
    location: null
};

const roomReducer = (state = initialState, action) => {
    let stateCopy = {...state};
    switch (action.type) {
        case SET_ROOM_DATA: {
            stateCopy.roomKey = action.payload.roomKey;
            stateCopy.isPrivate = action.payload.isPrivate;
            break;
        }
        case SET_ROOM_PARTICIPANTS: {
            stateCopy.mainParticipant = action.mainParticipant;
            [...stateCopy.participants] = action.participants;
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
export const setRoomParticipants = (mainParticipant, participants) => ({
    type: SET_ROOM_PARTICIPANTS,
    mainParticipant,
    participants
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