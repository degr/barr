import {authApi} from '../../kurrento/conferenceroom';

const SET_FETCHING = 'SET_FETCHING';
const SET_USER_DATA = 'SET_USER_DATA';
let initialState = {
    id: null,
    login: null,
    token: null,
    permissions: [],
    isAuth: false,
    isFetching: false
};

const authReducer = (state = initialState, action) => {
    let stateCopy = {...state};
    switch (action.type) {
        case SET_USER_DATA: {
            stateCopy.id = action.payload.id;
            stateCopy.login = action.payload.login;
            stateCopy.token = action.payload.token;
            stateCopy.isAuth = action.payload.isAuth;
            stateCopy.permissions = action.payload.permissions;
            break;
        }
        case SET_FETCHING: {
            stateCopy.isFetching = action.isFetching;
            break;
        }
        default: {
            stateCopy = state;
            break;
        }
    }
    return stateCopy;
};

export const setAuthUserData = (id, login, token, permissions, isAuth) => ({
    type: SET_USER_DATA,
    payload: {id, login, token, permissions, isAuth}
});

export const signIn = (login, password) => {
    return () => {
        authApi.signIn(login, password);
    }
};
export const signUp = (login, password) => {
    return () => {
        authApi.signUp(login, password)
    }
};
export default authReducer;