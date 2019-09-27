import {applyMiddleware, combineReducers, createStore} from "redux";
import authReducer from "./reducers/authReducer";
import thunkMiddleware from 'redux-thunk';
import {reducer as formReducer} from 'redux-form';
import roomReducer from "./reducers/roomReducer";

let reducers = combineReducers(
    {
        auth: authReducer,
        roomPage:roomReducer,
        form: formReducer
    }
);
export const store = createStore(reducers, applyMiddleware(thunkMiddleware));
