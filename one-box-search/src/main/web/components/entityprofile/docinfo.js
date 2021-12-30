/**
 * Created by Zaiwen Feng on 15/02/2017.
 */
import React from 'react'

/**
 * This React component displays the detailed info for a document
 * **/

const DocInfo = React.createClass({

    /**Initialize this component**/
    getInitialState(){


        return{


        };
    },

    render: function () {

        /**Define the var shown on the title**/
        var type = this.props.type;
        var docid = this.props.docid;

        var fullTitle = type+ ' ' + docid;


        return(
            <form>

                <h2>{fullTitle}</h2>

                <div>Subject: {this.props.subject}</div>

                <div>Relation: {this.props.relation}</div>

                <div>Object: {this.props.object}</div>

                <div>Parsed Date: {this.props.date_parsed}</div>

                <div>Confirmed By: {this.props.confirmedBy}</div>

                <div>Source: {this.props.source}</div>

                <div>Confirmed Date: {this.props.dateConfirmed}</div>

                <div>Status: {this.props.status}</div>

            </form>


        )

    }



});

module.exports = DocInfo;

