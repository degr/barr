import React from 'react';
import {connect} from "react-redux";
import Canvas from "./Canvas";

const CanvasContainer = props => (<Canvas {...props}/>);
const mapStateToProps = (state) => ({
    location: state.roomPage.location,
});
export default connect(mapStateToProps, {})(CanvasContainer);