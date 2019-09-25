import {authAPI} from "../api/api";

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
    return (dispatch) => {
        authAPI.signIn(login, password)
            .then(response => response.data.resultCode === 0 ? response.data.data : null)
            .then(data => {
                if (data) {
                    let id = data.id;
                    let login = data.login;
                    let token = data.token;
                    let permissions = data.permissions;
                    dispatch(setAuthUserData(id, login, token, permissions, true))
                }
            });
    }
};
export const signUp = (login, password) => {
    return (dispatch) => {
        authAPI.signUp(login, password)
            .then(response => response.data.resultCode === 0 ? response.data.data : null)
            .then(data => {
                if (data) {
                    let id = data.id;
                    let login = data.login;
                    let token = data.token;
                    let permissions = data.permissions;
                    dispatch(setAuthUserData(id, login, token, permissions, true))
                }
            });
    }
};
export const signOut = () => {
    return (dispatch) => {
        authAPI.signOut()
            .then(response => {
                if (response.data.resultCode === 0) {
                    dispatch(setAuthUserData(null, null, null, false))
                }
            })
    }
};
export default authReducer;