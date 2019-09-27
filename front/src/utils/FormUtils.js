import React from "react";

export const Input = ({input, meta, ...props}) => {
    const hasError = meta.touched && meta.error;
    let hasValStyle = input.value ? " has-val" : "";
    props.className = props.className + hasValStyle;

    let error = meta.error;
    return (
        <div>
            <input  {...input}{...props}/>
            {hasError ? <div className={"alert-validation"}>{error}</div> : ""}

        </div>
    )
};