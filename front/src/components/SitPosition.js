import React from 'react';
import DefaultLocations from '../utils/DefaultLocations';

export default class SitPosition extends React.Component {
    render() {
        const data = this.props.data;
        //return <div style={{left: Math.abs(data.x*200)}}
        const left = data.x;
        const top = data.z;
        const translatedLeft = left + 5.25;
        const translatedTop = top + 2.91;

        return <div style={{top: translatedTop*80, left: translatedLeft*80}}
                    className={"sit-position sit-position-" + this.props.index}
                    onClick={this.onClick}/>

            }

    onClick = () => {
        this.props.onClick(this.props.data);
    }
}
