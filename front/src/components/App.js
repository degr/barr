import React, {Component} from 'react';
import '../App.css';
import {Route} from "react-router-dom";
import Login from "./auth/Login/Login";
import SignUp from "./auth/SignUp/SignUp";
import JoinPrivateRoom from "./BtnGroup/JoinPrivateRoomForm/JoinPrivateRoomForm";
import MenuContainer from "./Menu/MenuContainer";
import CanvasContainer from "./../canvas/CanvasContainer";

class App extends Component {
    render() {
        return (
            <div>
                <Route path='' render={() => <MenuContainer/>}/>
                <CanvasContainer/>
                <Route path='/login' render={() => <div className="bar-map"><Login/></div>}/>
                <Route path='/signUp' render={() => <div className="bar-map"><SignUp/></div>}/>
                <Route path='/joinPrivateRoom' render={() => <div className="bar-map"><JoinPrivateRoom/></div>}/>
            </div>
        );
    }
}

export default App;