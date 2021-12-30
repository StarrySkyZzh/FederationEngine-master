/**
 * Created by Zaiwen Feng on 3/11/2016.
 */
import React from 'react'
import { Link } from 'react-router'

/**This component will display the section of 'Relevant Information' in the page of person profile
 *
 * This component is also responsible for calling WebHDFS in order to open a .txt file hosted on the HDFS server*
 *
 *
 * <Link to="/entityprofile/pdfinfo/">{this.props.relevantinfo}</Link>
 * */

const RelevantInformation = React.createClass({

    /**Firstly we initialize this component**/
    getInitialState(){

        return{

            /**Text for downloading**/
            text: ""

        };

    },

    componentWillMount(){




        /**Get the HDFS source name**/
        var hdfs_source_name = this.props.source_name;

        console.log(hdfs_source_name);

        /**Include webhdfs module**/
        var WebHDFS = require('webhdfs');
        //
        /**Create a new HDFS Client**/
        var hdfs = WebHDFS.createClient({

            user: 'webuser', //Hadoop user
            host: '130.220.210.127', //Namenode host
            port: 50070, //Namenode port
            path: '/webhdfs/v1'

        });

        console.log("creating HDFS client successfully....");

        var fs = require('fs');

        /**Initialize readable stream from HDFS source**/
        var remoteFileStream = hdfs.createReadStream("/user/ile/document/" + this.props.source_name);

        console.log("creating HDFS remoteFileStream successfully....");

        /**Variable for storing data**/
        var data = new Buffer(255);

        console.log(data.toString());

        console.log("data has been defined well...");

        remoteFileStream.on('error', function onError (err) {

            console.log("test.....error");
            /**Do something with the error**/
            console.log("The error information is below: " + err.toString());

        });

        remoteFileStream.on('data', function onChunk (chunk){


            /**The variable 'chunk' is actually the text received from Web HDFS **/

            /**Concat 'chunk' into 'data'**/
            const buf = Buffer.from(chunk);

            data = Buffer.concat([data, buf]);

            /**Assign Buffer Stream to the variable 'text'**/
            this.setState({text: data.toString()});


        }.bind(this));//**Need to bind to this in order to have access inside the callback, very IMPORTANT! 24 Feb 17**//

        remoteFileStream.on('finish', function onFinish(){

            console.log("test.....finish");
            /**Print received data**/
            //console.log(data.toString());
        });

        console.log("finishing invoking HDFS...");

    },

    render: function () {


        /** <Link to={"/pdfinfo" + "/" + this.props.source_name}>{this.props.source_name}</Link>**/

        return(
            <form id="relevantInfo">

                <h4>Relevant information</h4>

                <div>{this.props.source_name}</div>

                <button id="btn-save" type="button" class="btn btn-primary" onClick={this.saveFile}>Save to file</button>

            </form>


        );

    },


    /**This function is used to save files from Buffer Stream fetched from HDFS (or Federated Engine in the future)**/
    saveFile(){


        var FileSaver = require('file-saver');

        /**Get the data to be saved**/
        var text = this.state.text;

        /**Define the file name that is to be downloaded. This file name is equal to the source name after trimming '.txt'**/
        var fileName = this.props.source_name.replace(".txt","");

        var blob = new Blob([text], {type: "text/plain; charset=utf=8"});

        FileSaver.saveAs (blob, fileName + ".txt");
    }




});

module.exports = RelevantInformation;