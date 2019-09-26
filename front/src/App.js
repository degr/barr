import React, {Component} from 'react';
import './App.css';
import Canvas from "./canvas/Canvas";
import Map from "./components/Map";
import {Route} from "react-router-dom";
import Login from "./components/auth/Login/Login";
import SignUp from "./components/auth/SignUp/SignUp";

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
                <Route path='' render={() => <Map onLocationChange={location => this.setState({location: location})}/>}/>
                <Canvas location={this.state.location}/>
                <Route path='/login' render={() => <div className="bar-map"><Login/></div>}/>
                <Route path='/signUp' render={() => <div className="bar-map"><SignUp/></div>}/>
            </div>
        );
    }
}

export default App;