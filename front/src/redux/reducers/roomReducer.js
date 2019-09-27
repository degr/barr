import {roomApi} from "../../api/api";

const SET_ROOM_DATA = 'SET_ROOM_DATA';
const SET_ROOM_PARTICIPANTS = 'SET_ROOM_PARTICIPANTS';
const SET_LOCATION = 'SET_LOCATION';
const RESET_ROOM = 'RESET_ROOM';
const ADD_PARTICIPANT = 'ADD_PARTICIPANT';
const REMOVE_PARTICIPANT = 'REMOVE_PARTICIPANT';
const UPDATE_PARTICIPANT = 'UPDATE_PARTICIPANT';
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
            stateCopy.participants = [...action.participants];
            break;
        }
        case ADD_PARTICIPANT: {
            stateCopy.participants = [...state.participants, action.participant];
            break;
        }
        case UPDATE_PARTICIPANT: {
            let name = action.participant.name;
            stateCopy.participants[name] = action.participant;
            break;
        }
        case REMOVE_PARTICIPANT: {
            const list = state.participants.filter(item => item.name !== action.participant.name);
            stateCopy.participants = [...list];
            break;
        }
        case RESET_ROOM : {
            stateCopy.roomKey = null;
            stateCopy.participants = [];
            stateCopy.location = null;
            stateCopy.isPrivate = false;
            stateCopy.mainParticipant = null;
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

export const addParticipant = (participant) => ({
    type: ADD_PARTICIPANT,
    participant
});

export const updateParticipant = (participant) => ({
    type: UPDATE_PARTICIPANT,
    participant
});

export const removeParticipant = (participant) => ({
    type: REMOVE_PARTICIPANT,
    participant
});

export const setRoomData = (roomKey, isPrivate) => ({
    type: SET_ROOM_DATA,
    payload: {roomKey, isPrivate}
});
export const setRoomParticipants = (mainParticipant, participants) => ({
    type: SET_ROOM_PARTICIPANTS,
    mainParticipant,
    participants
});

export const setRoom = (payload) => {
    return (dispatch) => {
        dispatch(resetRoom());
        let roomKey = payload.type;
        dispatch(setRoomData(roomKey, false));
        dispatch(setLocation(payload.location));
        dispatch(joinPublicRoom(payload.login, roomKey));
    }
};
const setLocation = (location) => ({
    type: SET_LOCATION,
    location
});

export const resetRoom = () => ({
    type: RESET_ROOM
});

export const joinPrivateRoom = (login, token, roomKey) => {
    return (dispatch) => {
        roomApi.joinPrivateRoom(login, token, roomKey);
    }
};
export const joinPublicRoom = (login, roomKey) => {
    return (dispatch) => {
        roomApi.joinPublicRoom(login, roomKey);
    }
};
export default roomReducer;