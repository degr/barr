import React from 'react';
import {connect} from "react-redux";
import BtnGroup from "./BtnGroup";

const BtnGroupContainer = props => (<BtnGroup {...props}/>);

const mapStateToProps = (state) => ({
        login: state.auth.login,
    })
;
export default connect(mapStateToProps, {})(BtnGroupContainer);