import React from "react";
import {NavLink} from "react-router-dom";

const BtnGroup = (props) => {
    return (
        <>
            <button className="btn_menu"/>
            <button className="btn_map" onClick={props.showMap}/>
            <button className="btn_msg"/>
            <button className="btn_edit"/>
            <button className="btn_group"/>
            <button className="btn_volume"/>
            <NavLink to={'login'}>
                <button className="btn_sign_in"/>
            </NavLink>
            {props.login}
        </>
    )
};
export default BtnGroup;