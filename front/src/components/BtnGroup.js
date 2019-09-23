import React from "react";

const BtnGroup = (props) => {
    return (
        <>
            <button className="btn_menu"/>
            <button className="btn_map" onClick={props.showMap}/>
            <button className="btn_msg"/>
            <button className="btn_edit"/>
            <button className="btn_group"/>
            <button className="btn_volume"/>
            <button className="btn_sign_in" onClick={props.showSignIn}/>
        </>
    )
};
export default BtnGroup;