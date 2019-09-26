import {applyMiddleware, combineReducers, createStore} from "redux";
import authReducer from "./reducers/authReducer";
import thunkMiddleware from 'redux-thunk';

import {reducer as formReducer} from 'redux-form';



 export const store = createStore(
    combineReducers(
        {
            auth: authReducer,
            form: formReducer
        }
    ),
    applyMiddleware(thunkMiddleware));
