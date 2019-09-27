import React from "react";
import {NavLink} from "react-router-dom";

const BtnGroup = (props) => {
    return (
        <>
            <NavLink to={'/'}>
                <button className="btn_menu"/>
            </NavLink>
            <button className="btn_map" onClick={props.showMap}/>
            <button className="btn_msg"/>
            <button className="btn_edit"/>
            <NavLink to={'joinPrivateRoom'}>
                <button className="btn_group"/>
            </NavLink>
            <button className="btn_volume" onClick={props.signOut}/>
            <hr/>
            <NavLink to={'login'}>
                <button className="btn_sign_in"/>
            </NavLink>
            {props.login}
        </>
    )
};
export default BtnGroup;