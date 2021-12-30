import React from 'react'
import { Link } from 'react-router'


/**
 *
 * <img src={require("components/header/logo.png")} className="logo"/>
 * **/

const Header = React.createClass({
    render() {
        return (
            <div>
                <nav className="navbar navbar-default">
                    <ul className="nav navbar-nav">
                        <li><Link to="/">Home</Link></li>
                        <li><Link to="/about">About</Link></li>
                    </ul>
                </nav>
            </div>
        );
    }
});

export default Header;