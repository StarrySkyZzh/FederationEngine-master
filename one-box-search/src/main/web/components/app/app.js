import React from 'react'
import Header from 'components/header/header'

/**
 * Top-level application component.
 * The common page elements are rendered here (menus, logos etc).
 * react-router passes the active page contents in 'props.children'.
 */

class App extends React.Component{
    render() {
        return (
            <div>
                <Header/>
                {React.cloneElement(this.props.children, {/** pass state common to all pages here **/})}
            </div>
        )
    }
}


export default App;