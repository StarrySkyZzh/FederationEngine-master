/**
 * Created by fengz on 2/11/2016.
 */
import React from 'react'

/**
 * This React component displays the detailed info for a person, including person name, address, hair color, etc...
 * **/

const PersonInfo = React.createClass({

    /**Initialize this component**/
    getInitialState(){


        return{


        };
    },

    render: function () {



        return(
            <form>

                <h2>{this.props.name}</h2>

                <div>Citizenship: {this.props.citizenship}</div>

                <div>DoB: {this.props.DoB}</div>

                <div>Hair: {this.props.hair}</div>

                <div>Eye: {this.props.eye}</div>

                <div>Height: {this.props.height}</div>

                <div>Weight: {this.props.weight}</div>

                <div>Build: {this.props.build}</div>

            </form>


        )

    }



});

module.exports = PersonInfo;

