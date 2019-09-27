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
                <BtnGroupContainer showMap={() => this.setState({showMap: !this.state.showMap, showSignIn: false})}/>
                {this.state.showMap ? <PositionMap changePosition={this.changePosition}/> : emptyTag}
            </div>
        )
    }

    changePosition = (data) => {
        this.setState(
            {showMap: false},
            () => this.props.setLocation(data),
        );
    }
}

export default Menu;