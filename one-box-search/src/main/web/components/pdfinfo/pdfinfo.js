/**
 * Created by Zaiwen Feng on 4/11/2016.
 */
import React from 'react';
import spdf from 'simple-react-pdf';
import elasticsearch from 'elasticsearch-browser'

/**This React component will display PDF info for one relevant information item**/

/**Create the client of ElasticSearch**/
let client = new elasticsearch.Client({
    host: "http://130.220.209.30:9200/document_store/",
    log: 'trace',

});


const PdfInfo = React.createClass({





    /**Initialize this pdf info component**/
    getInitialState(){

        console.log("welcome to the document page!");

        return{
            /**'results' represents the detailed description on ES **/
            results: "",

            loadingFlag:false //to avoid multiple render if data from ElasticSearch has not fetched
        };


    },

    componentWillMount(){

        console.log("This is the person profile page! ");

        console.log(this.props);

        const { params } = this.props;

        const { keyword } = params;

        /**Here is just the temporal code directly calling Elasticsearch. After the Federated Engine is installed,
         * this code should be replaced below**/

        client.search({

            q: keyword,

            size: 10

        }).then(function(body){

            console.log("prepare for getting the document from ES...");

            console.log(body);

            this.setState({results: body.hits.hits});

            if(!this.state.loadingFlag){

                this.setState({loadingFlag: true});
            }



        }.bind(this), function (error) {
            console.trace(error.message);


        });



    },


    /**
     * <spdf.SimplePDF file="./6059090_Alice_Smith.pdf"></spdf.SimplePDF>
     * **/

    render: function(){

        console.log("this is the page showing document!");
        if(this.state.loadingFlag==true){

            var doc = this.state.results[0]._source;

            console.log(doc);


            /**Prepare for showing 'doc' in the pdf file**/
            return(

                <div>

                    <h2>doc page...</h2>

                    <spdf.SimplePDF file="components/pdfinfo/6059090_Alice_Smith.pdf"></spdf.SimplePDF>

                </div>


            )


        }else{

            console.log("LOADING");

            /**this else branch should exist. If not, the 'If' branch would not execute. 2 Feb 2016**/
            return <div>Loading...</div>;

        }

    }



});

export default PdfInfo;