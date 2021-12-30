/**
 * Created by fengz on 2/11/2016.
 */
import React from 'react'

/**
 * This React component displays the detailed info for a location
 * **/

const LocationInfo = React.createClass({

    /**Initialize this component**/
    getInitialState(){


        return{


        };
    },

    render: function () {


        return(
            <form>

                <h2>{this.props.title}</h2>

                <div>Location ID: {this.props.location_id}</div>

                <div>Country: {this.props.country}</div>

                <div>PCode: {this.props.pcode}</div>

                <div>City/Town: {this.props.city_town}</div>

                <div>Created Date: {this.props.date_created}</div>

                <div>Street Number: {this.props.street_number}</div>

                <div>Street Name: {this.props.street_name}</div>

                <div>Street Type: {this.props.street_type}</div>

                <div>State: {this.props.state}</div>

            </form>


        )

    }



});

module.exports = LocationInfo;

