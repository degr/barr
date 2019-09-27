import React from 'react';
import {connect} from "react-redux";
import Menu from "./Menu";
import {setRoom} from "../../redux/reducers/roomReducer";

const MenuContainer = props => (<Menu {...props}/>);

const mapStateToProps = (state) => ({
    login: state.authPage.login,
});

export default connect(mapStateToProps, {setRoom})(MenuContainer);