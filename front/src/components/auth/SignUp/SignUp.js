import React, {Component} from 'react'
import {Field, reduxForm} from "redux-form";
import {connect} from "react-redux";
import {NavLink, Redirect} from "react-router-dom";
import {signUp} from "../../../redux/reducers/authReducer";
import {maxLengthCreator, minLengthCreator, passwordsMustMatch, requiredField} from "../../../utils/validators";
import {Input} from "../../../utils/FormUtils";

class SignUpForm extends Component {
    render() {
        return (
            <div>
                <div className="limiter">
                    <div className="container-login100">
                        <div className="wrap-login100 p-t-85 p-b-20">
                            <form className="login100-form validate-form" onSubmit={this.props.handleSubmit}>
					        <span className="login100-form-title p-b-70">
                                Welcome
					        </span>

                                <div className="wrap-input100 validate-input m-t-85 m-b-35"
                                     data-validate="Enter username">

                                    <Field className="input100" type="text" name="username"
                                           component={Input}
                                           validate={[requiredField, minLength5, maxLength255]}/>
                                </div>

                                <div className="wrap-input100 validate-input m-b-50" data-validate="Enter password">
                                    <Field className="input100" type="password" name="password"
                                           component={Input}
                                           validate={[requiredField, minLength5, maxLength255]}/>
                                </div>
                                <div className="wrap-input100 validate-input m-b-50" data-validate="Enter password">
                                    <Field className="input100" type="password" name="confirmPassword"
                                           component={Input}
                                           validate={[requiredField, minLength5, maxLength255, passwordsMustMatch]}/>
                                </div>

                                <div className="container-login100-form-btn">
                                    <button className="login100-form-btn" onClick={this.props.submit}>
                                        Sign Up
                                    </button>
                                </div>
                                <ul className="login-more p-t-190">
                                    <li>
							        <span className="txt1">
                                        Already have an account?
							        </span>
                                        <NavLink to={'login'} className="txt2">
                                            To sign in
                                        </NavLink>
                                    </li>
                                </ul>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

const SignUpFormReduxForm = reduxForm({form: 'signUpForm'})(SignUpForm);
const minLength5 = minLengthCreator(5);
const maxLength255 = maxLengthCreator(255);
const SignUp = (props) => {
    const onSubmit = (formData) => {
        props.signUp(formData.username, formData.password)
    };

    return (
        props.token ? <Redirect to={"/users"}/> :
            <div>
                <SignUpFormReduxForm onSubmit={onSubmit}/>
            </div>
    )
};
const mapStateToProps = (state) => ({
    token: state.authPage.token
});
export default connect(mapStateToProps, {signUp})(SignUp);