import {applyMiddleware, combineReducers, createStore} from "redux";
import authReducer from "./AuthReducer";
import thunkMiddleware from 'redux-thunk';

import {reducer as formReducer} from 'redux-form';

let reducers = combineReducers(
    {
        auth: authReducer,
        form: formReducer
    }
);

const store = createStore(reducers, applyMiddleware(thunkMiddleware));
export default store;