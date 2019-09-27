import React from 'react'
import {Field, reduxForm} from "redux-form";
import {connect} from "react-redux";
import {NavLink, Redirect} from "react-router-dom";
import {maxLengthCreator, minLengthCreator, requiredField} from "../../../utils/validators";
import {Input} from "../../../utils/FormUtils";
import {joinPrivateRoom} from "../../../redux/reducers/roomReducer";

const JoinPrivateRoomForm = (props) => {
    return (
        <div className="limiter">
            <div className="container-login100">
                <div className="wrap-login100 p-t-85 p-b-20">
                    <form className="login100-form validate-form" onSubmit={props.handleSubmit}>
                        <div className="wrap-input100 validate-input m-b-50" data-validate="Enter password">
                            <Field className="input100" type="password" name="roomKey"
                                   component={Input}
                                   textVal='password'
                                   validate={[requiredField, minLength5, maxLength255]}/>
                        </div>
                        <div className="container-login100-form-btn">
                            <button className="login100-form-btn" onClick={props.submit}>
                                Join private room
                            </button>
                        </div>

                        <ul className="login-more p-t-190">
                            <li>
							        <span className="txt1">
                                        don't authorized?
							        </span>
                                <NavLink to={'login'} className="txt2">
                                    Sign In
                                </NavLink>
                            </li>
                        </ul>
                    </form>
                </div>
            </div>
        </div>
    )
};

const JoinPrivateRoomReduxForm = reduxForm({form: 'joinPrivateRoom'})(JoinPrivateRoomForm);

const minLength5 = minLengthCreator(5);
const maxLength255 = maxLengthCreator(255);

const JoinPrivateRoom = (props) => {
    const onSubmit = (formData) => {
        props.joinPrivateRoom(props.login, props.token, formData.roomKey)
    };
    return (
        //TODO redirect to private room location
        props.token ? <Redirect to={"/"}/> :
            <div>
                <JoinPrivateRoomReduxForm onSubmit={onSubmit}/>
            </div>
    )
};
const mapStateToProps = (state) => ({
    login: state.auth.login,
    token: state.auth.token
});
export default connect(mapStateToProps, {joinPrivateRoom})(JoinPrivateRoom);