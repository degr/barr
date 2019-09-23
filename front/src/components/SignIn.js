import React from 'react';

class SignIn extends React.Component {
    state = {
        isAnonymous: true
    };
    setAnonymous = () => {
        this.setState({isAnonymous: !this.state.isAnonymous});
        if (this.state.isAnonymous) {
            this.setState({password: null})
        }
    };

    render() {
        return <div id="container">
            <div id="wrapper">
                <div id="join" className="animate join">
                    <h1>Join LAPPA Bar</h1>
                    <p>
                        <label htmlFor="Anonymous">Anonymous</label>
                        <input type="checkbox" name="isRegistered" id="isRegistered"
                               checked={this.state.isAnonymous} onClick={this.setAnonymous}
                               placeholder="Is private?"/>
                    </p>
                    <p>
                        <input type="text" name="name" value={this.props.login} id="name" placeholder="Username"
                               onChange={this.props.onChangeLogin} required/>
                    </p>
                    {
                        this.state.isAnonymous ?
                            <></> :
                            <p>
                                <input type="password" name="password" value={this.props.password} id="password"
                                       placeholder="Password"
                                       onChange={this.props.onChangePassword}/></p>
                    }
                </div>
            </div>
        </div>
    }
}

export default SignIn;