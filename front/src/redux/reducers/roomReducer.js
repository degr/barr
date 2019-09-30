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
    location: null,
};

const roomReducer = (state = initialState, action) => {
    let stateCopy = state;
    switch (action.type) {
        case SET_ROOM_DATA: {
            return {
                ...state, roomKey: action.payload.roomKey, isPrivate: action.payload.isPrivate
            }
        }
        case SET_ROOM_PARTICIPANTS: {
            return {
                ...state, location: action.mainParticipant.location,
                mainParticipant: action.mainParticipant,
                participants: [...action.participants]
            };
        }
        case ADD_PARTICIPANT: {
            return {...state, participants: [...state.participants, action.participant]};
        }
        case UPDATE_PARTICIPANT: {
            stateCopy = {...state};
            let name = action.participant.name;
            stateCopy.participants[name] = action.participant;
            return stateCopy;
        }
        case REMOVE_PARTICIPANT: {
            return {...state, participants: state.participants.filter(item => item.name !== action.participant.name)};
        }
        case RESET_ROOM : {
            stateCopy = {...state};
            stateCopy.roomKey = null;
            stateCopy.participants = [];
            stateCopy.location = null;
            stateCopy.isPrivate = false;
            stateCopy.mainParticipant = null;
            return stateCopy;
        }
        case SET_LOCATION: {
            return {...state, location: action.location};
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
        let location = payload.location;
        dispatch(setLocation(location));
        dispatch(joinPublicRoom(payload.login, roomKey, location));
    }
};
const setLocation = (location) => ({
    type: SET_LOCATION,
    location
});

export const resetRoom = () => ({
    type: RESET_ROOM
});

export const joinPrivateRoom = (login, token, roomKey, location) => {
    return (dispatch) => {
        roomApi.joinPrivateRoom(login, token, roomKey, location);
    }
};
export const joinPublicRoom = (login, roomKey, location) => {
    return (dispatch) => {
        roomApi.joinPublicRoom(login, roomKey, location);
    }
};
export default roomReducer;