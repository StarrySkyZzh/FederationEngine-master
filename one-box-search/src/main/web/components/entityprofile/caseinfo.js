/**
 * Created by Zaiwen Feng on 3/2/2017.
 */
import React from 'react'

/**
 * This React component displays the detailed info for a location
 * **/

const CaseInfo = React.createClass({

    /**Initialize this component**/
    getInitialState(){


        return{


        };
    },

    render: function () {


        return(
            <form>

                <h2>{this.props.incident_type}</h2>

                <div>Case ID: {this.props.case_id}</div>

                <div>Involvement Type: {this.props.involvement_type}</div>

            </form>


        )

    }



});

module.exports = CaseInfo;

