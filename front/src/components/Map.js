import React from 'react';
import './style/Map.scss';
import SitPosition from './SitPosition';
import DefaultLocations from "../utils/DefaultLocations";
import SignIn from "./SignIn";
import BtnGroup from "./BtnGroup";

const emptyTag = <></>;
export default class Map extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showMap: false,
            showSignIn: false,
            login: null,
            password: null
        }
    }

    onChangeLogin = (event) => {
        this.setState({login: event.target.value});
    };
    onChangePassword = (event) => {
        this.setState({password: event.target.value});
    };

    render() {

        const barMap =
            <div className="bar-map">
                {DefaultLocations
                    .map((v, i) => <SitPosition key={i} data={v} changePosition={this.changePosition}/>)}
            </div>;

        const signIn =
            <div className="bar-map">
                <SignIn login={this.state.login} password={this.state.password}
                        onChangeLogin={this.onChangeLogin}
                        onChangePassword={this.onChangePassword}/>
            </div>;

        return (
            <div>
                <BtnGroup
                    showMap={() => this.setState({
                        showMap: !this.state.showMap, showSignIn: false
                    })}
                    showSignIn={() => this.setState({
                        showSignIn: !this.state.showSignIn, showMap: false
                    })}
                />
                {this.state.showMap ? barMap : emptyTag}
                {this.state.showSignIn ? signIn : emptyTag}
            </div>
        )
    }

    changePosition = (data) => {
        this.setState(
            //вызывается инициирование аватара. Передается индекс (индекс в нашем случаем - место в баре)
            {showMap: false},
            () => this.props.onLocationChange(data),
        );
    }
}