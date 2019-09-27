import React from 'react';
import {connect} from "react-redux";
import BtnGroup from "./BtnGroup";
import {signOut} from "../../redux/reducers/authReducer";

const BtnGroupContainer = props => (<BtnGroup {...props}/>);

const mapStateToProps = (state) => ({
    login: state.authPage.login,
});
export default connect(mapStateToProps, {signOut})(BtnGroupContainer);