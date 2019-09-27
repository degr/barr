import React, {Component} from 'react';
import './App.css';
import {Route} from "react-router-dom";
import Login from "./components/auth/Login/Login";
import SignUp from "./components/auth/SignUp/SignUp";
import JoinPrivateRoom from "./components/BtnGroup/JoinPrivateRoomForm/JoinPrivateRoomForm";
import MenuContainer from "./components/Menu/MenuContainer";
import CanvasContainer from "./canvas/CanvasContainer";

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            location: null
        }
    }

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