import React from 'react'
import ResultList from 'components/entitysearch/ResultList'
import axios from 'axios'

/**Entity Search Ranked Result page Component**/
class SearchResultDisplay extends React.Component {

    constructor(props) {
        super(props);
        // console.log('constructor');
        this.state = {

        };
    }

    componentDidMount() {
        // console.log('componentDidMount');
    }

    shouldComponentUpdate(nextProps, nextState){
        let update = false;
        if (nextProps.update === true) {
            update = true;
            console.log('update enabled');
        }
        console.log('shouldComponentUpdate? ' + update);
        return update;
    }

    componentWillUpdate(nextProps, nextState){
        // console.log('componentWillUpdate');
    }

    render(){
        console.log('rendering SearchResultDisplay.js');

        if (this.props.data!==null) {

            return(
                <div>
                    <div><hr/></div>
                    <div>
                        <label>Found {this.props.data.payload.length} match(es)</label>
                    </div>
                    <div><br/></div>
                    <ResultList records={this.props.data.payload}/>
                    <div><br/><br/></div>
                    <div><hr/></div>
                    <div><br/><br/></div>
                </div>
            );
        } else {
            return (
                <div>
                    <div><hr/></div>
                </div>
            );
        }
    }

    componentDidUpdate(){
        // console.log('componentDidUpdate');
    }
}

// Warn if overriding existing method
if(Array.prototype.equals)
    console.warn("Overriding existing Array.prototype.equals. Possible causes: New API defines the method, there's a framework conflict or you've got double inclusions in your code.");
// attach the .equals method to Array's prototype to call it on any array
Array.prototype.equals = function (array) {
    // if the other array is a falsy value, return
    if (!array)
        return false;

    // compare lengths - can save a lot of time
    if (this.length != array.length)
        return false;

    for (var i = 0, l=this.length; i < l; i++) {
        // Check if we have nested arrays
        if (this[i] instanceof Array && array[i] instanceof Array) {
            // recurse into the nested arrays
            if (!this[i].equals(array[i]))
                return false;
        }
        else if (this[i] != array[i]) {
            // Warning - two different object instances will never be equal: {x:20} != {x:20}
            return false;
        }
    }
    return true;
}
// Hide method from for-in loops
Object.defineProperty(Array.prototype, "equals", {enumerable: false});


export default SearchResultDisplay;

