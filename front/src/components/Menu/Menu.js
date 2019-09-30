import React from 'react';
import '../style/Map.scss';
import BtnGroupContainer from "../BtnGroup/BtnGroupContainer";
import PositionMap from "../PositionMap";

const emptyTag = <></>;

class Menu extends React.Component {
    state = {
        showMap: false,
    };

    render() {
        return (
            <div>
                <BtnGroupContainer showMap={() => this.setState({showMap: !this.state.showMap})}/>
                {this.state.showMap ? <PositionMap changePosition={this.changePosition}/> : emptyTag}
            </div>
        )
    }

    changePosition = (data) => {
        let payload = {
            location: data,
            type: data.type,
            login: this.props.login
        };
        this.setState(
            {showMap: false},
            () => this.props.setRoom(payload),
        );
    }
}

export default Menu;