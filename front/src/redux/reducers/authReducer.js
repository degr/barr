import {authApi} from "../../api/api";

const SET_USER_DATA = 'SET_USER_DATA';
let initialState = {
    id: null,
    login: null,
    token: null,
    permissions: [],
    isAuth: false,
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
        default: {
            stateCopy = state;
            break;
        }
    }
    return stateCopy;
};

/*export const setUserData = (id, login, token, permissions, isAuth) => {
    return (dispatch) => {
        dispatch(setAuthUserData(id, login, token, permissions, isAuth));
        dispatch(setFetching(false));
    }
};*/

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
export const signOut = () => {
    return (dispatch) => {
        dispatch(setAuthUserData(null, null, null, [], false));
    }
};

export default authReducer;