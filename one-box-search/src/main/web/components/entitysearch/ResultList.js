import React from 'react'
import ResultItem from 'components/entitysearch/ResultItem'

class ResultList extends React.Component{

    constructor(props) {
        super(props);
        this.state = {};
    }

    render() {
        console.log('rendering ResultList.js');
        /**Define a React variable named 'allRecords', displaying all the record displaying component**/
        var allRecords = this.props.records.map((record) => {
            return <ResultItem info = {record.info} payload = {record.payload}/>;
        });

        return (
            <div className="list-group col-sm-12">
                {allRecords}
            </div>
        )
    }
}

export default ResultList;