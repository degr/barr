import React from 'react';
import {connect} from "react-redux";
import Canvas from "./Canvas";

const CanvasContainer = props => (<Canvas {...props}/>);


let mapStateToProps = (state) => {
    return {
        location: state.roomPage.location,
        participants: state.roomPage.participants
    }
};
export default connect(mapStateToProps, {})(CanvasContainer);