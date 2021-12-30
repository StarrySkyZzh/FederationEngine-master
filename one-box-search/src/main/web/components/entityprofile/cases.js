/**
 * Created by fengz on 3/11/2016.
 */
import React from 'react'

/**This React Component displays the cases that a person is involved in, which is shown in the second row of
 * the left part in the PERSON INFO PAGE (The third page)**/

const Cases = React.createClass({

    /**Firstly initialize this component**/
    getInitialState(){

        return{


        };

    },

    /**Here, we set props named 'case' to show the dynamic content**/
    render: function(){

        /**Define a React variable named 'allCases', showing all the case item component**/
        var createItem = function (itemText) {

            return <li>{itemText}</li>;
        };

        return(
            <form>

                <h4>Cases</h4>

                <ul>{this.props.cases.map(createItem)}</ul>

            </form>
        )

    }




});

module.exports = Cases;