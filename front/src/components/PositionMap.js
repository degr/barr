import React from "react";
import DefaultLocations from "../utils/DefaultLocations";
import SitPosition from "./SitPosition";

const PositionMap = (props) => {
    return (
        <div className="bar-map">
            {DefaultLocations.map((v, i) => <SitPosition key={i} data={v} changePosition={props.changePosition}/>)}
        </div>
    )
};
export default PositionMap;