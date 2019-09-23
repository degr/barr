import React from 'react';


const SitPosition = (props) => {
    const data = props.data;
    const left = data.x;
    const top = data.z;
    const translatedLeft = left + 5.25;
    const translatedTop = top + 2.91;
    return <div style={{top: translatedTop * 80, left: translatedLeft * 80}}
                className={"sit-position sit-position-" + props.index}
                onClick={() => props.changePosition(props.data)}/>
};

export default SitPosition;