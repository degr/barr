import React from 'react';
import './style/Map.scss';
import SitPosition from './SitPosition';
import DefaultLocations from "../utils/DefaultLocations";
import BtnGroupContainer from "./BtnGroup/BtnGroupContainer";

const emptyTag = <></>;

class Map extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showMap: false,
            showSignIn: false,
            login: null,
            password: null
        }
    }

    render() {
        const barMap =
            <div className="bar-map">
                {DefaultLocations
                    .map((v, i) => <SitPosition key={i} data={v} changePosition={this.changePosition}/>)}
            </div>;
        return (
            <div>
                <BtnGroupContainer
                    showMap={() => this.setState({
                        showMap: !this.state.showMap, showSignIn: false
                    })}
                />
                {this.state.showMap ? barMap : emptyTag}
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

export default Map;