import React from 'react';
import {connect} from "react-redux";
import Menu from "./Menu";
import {setLocation} from "../../redux/reducers/roomReducer";

const MenuContainer = props => (<Menu {...props}/>);
export default connect(null, {setLocation})(MenuContainer);