/**
 * Created by Zaiwen Feng on 1/11/2016.
 */

/**This page displays profile of each person**/
import React from 'react'
import Search from 'components/entityprofile/search'
import PersonInfo from 'components/entityprofile/personinfo'
import RelevantInfo from 'components/entityprofile/relevantinfo'
import CaseInfo from 'components/entityprofile/caseinfo'
import LocationInfo from 'components/entityprofile/locationinfo'
import DocInfo from 'components/entityprofile/docinfo'

/**import axios from 'axios'**/
import elasticsearch from 'elasticsearch-browser'



/**Entity profile Page Component**/
const EntityProfile = React.createClass({

    /**Initialize the Person Profile Page**/
    getInitialState() {

        console.log("Prepare for routing to person profile page! ");

        return{

            /**The variable below records an array from Elasticsearch/Federated Engine queried by 'person_id' or 'location_id' or 'case_id'...**/
            results: [

            ],

            /**These variables are for person info section**/
            id : "",
            given_name1 : "",
            last_name : "",
            country_citizen : "",
            dob : "",
            colour_hair : "",
            colour_eyes : "",
            height : "",
            weight : "",
            build : "",
            /**image_base64_str : "",**/

            /**Those variables below are for location info section**/
            title : "",
            country : "",
            pcode : "",
            city_town : "",
            date_created : "",
            street_number : "",
            street_type : "",
            state : "",
            id : "",
            street_name : "",


            /**Those variables below are for case info section**/
            case_id : "",
            incident_type: "",
            involvement_type: "",

            /**This variables are for cases info section. However, this section can not be displayed because there is not relationship data on ElasticSearch now @ 6 Feb 2017**/
            case_info : [],

            /**This variable is used for relevant information section**/
            relevant_info : "",
            document_url : "",

            /**This variable below are for document info section**/
            subject : "",
            relation : "",
            object : "",
            date_parsed : "",
            confirmedBy : "",
            source : "",
            dateConfirmed : "",
            status : "",

            /**define the index of fetched data, e.g. entity_store on elasticsearch**/
            index : "",

            /**define the type of fetched data, i.e. person, location, case., document,..**/
            type : "",

            /**define the id of the fetched data, i.e. person, location, case, document,...**/
            entityid : "",

            /**set a loading flag to prevent multiple render**/
            loadingFlag : false,

            /**set a random number to fetch an image**/
            random_number: ""

        };

    },





    componentWillMount(){

        console.log("This is the person profile page! ");

        console.log(this.props);

        const { params } = this.props;

        const { entityid } = params; // get the entity id (e.g. case id, person id, location id...) parameter on ElasticSearch from URL

        const { typename } = params; //get the type name parameter on ElasticSearch from URL

        const { indexname} = params; //get the index name parameter on ElasticSearch from URL

        this.setState({index: indexname});

        this.setState({type: typename});

        this.setState({entityid: entityid});

        /**Create the client of ElasticSearch**/
        let client = new elasticsearch.Client({
            host: "http://130.220.209.30:9200/",
            log: 'trace'
        });

        /**The code below are temporarily added on 24 Jan 2017 in order to fetch data from Elasticsearch directly.
         * However, these codes should be replaced by calling Federated Engine after Federated Engine is finished**/
        console.log("Prepare for calling ElasticSearch to fetch data in terms of 'entityid'");
        console.log(entityid);

        /**Here is just the temporal code directly calling Elasticsearch. After the Federated Engine is installed,
         * this code should be replaced below**/

        client.get({

            index: indexname,
            type: typename,
            id: entityid

        }).then(function(body){

            console.log("prepare for getting all the records from ES by using indexname, typename, and entityid...");

            console.log(body);

            /**Get the query result from ElasticSearch with id**/
            this.setState({results: body._source});

            /**Change the data with regard to a PERSON**/
            if((indexname == "entity_store")&&(typename == 'person')){

                /**Here, we would like to deal the array named 'results', getting the useful attributes, merging three attributes named 'case_id',
                 * 'incident_type' and 'incident_type', and return a single data object**/
                /**Suppose these attributes below have the same value**/
                this.setState({id: this.state.results.id});
                this.setState({given_name1: this.state.results.given_name1});
                this.setState({last_name: this.state.results.last_name});
                this.setState({country_citizen: this.state.results.country_citizen});
                this.setState({dob: this.state.results.dob});
                this.setState({colour_hair: this.state.results.colour_hair});
                this.setState({colour_eyes: this.state.results.colour_eyes});
                this.setState({height: this.state.results.height});
                this.setState({weight: this.state.results.weight});
                this.setState({build: this.state.results.build});

                /**set a random number from 1 to 10 to fetch an image**/
                var rand = Math.floor((Math.random() * 10)) + 1; //Math.floor(Math.random() * y) + x  // x: start number, y: end number

                this.setState({random_number: rand});


            }

            /**Change the data state with regard to a LOCATION**/
            else if((indexname == 'entity_store') && (typename == 'location')){

                this.setState({title: this.state.results.title});
                this.setState({country : this.state.results.country});
                this.setState({pcode : this.state.results.pcode});
                this.setState({city_town: this.state.results.city_town});
                this.setState({date_created: this.state.results.date_created});
                this.setState({street_number: this.state.results.street_number});
                this.setState({state: this.state.results.state});
                this.setState({id: this.state.results.id});
                this.setState({street_name: this.state.results.street_name});
                this.setState({street_type: this.state.results.street_type});

            }

            /**Change the data state with regard to a CASE**/
            else if ((indexname == 'entity_store') && (typename == 'case')){

                this.setState({case_id: this.state.results.id});
                this.setState({involvement_type: this.state.results.involvement_type});
                this.setState({incident_type: this.state.results.incident_type});
            }

            /***Change the state of data with regard to DOCUMENT*/
            else if (indexname == 'extracts'){

                this.setState({subject: this.state.results.subject});
                this.setState({relation: this.state.results.relation});
                this.setState({object: this.state.results.object});
                this.setState({date_parsed: this.state.results.date_parsed});
                this.setState({confirmedBy: this.state.results.confirmedBy});
                this.setState({source: this.state.results.source});
                this.setState({dateConfirmed: this.state.results.dateConfirmed});
                this.setState({status: this.state.results.status});
            }


            if(!this.state.loadingFlag){


                this.setState({loadingFlag: true});//it means that the state of variables, e.g. person id, case id,... have been really changed...
            }


        }.bind(this), function (error) {
            console.trace(error.message);
        });

    },


    /**Render the Generic Entity Profile Result Page*
     * @returns {XML} the page content*/
    render(){


        if(this.state.loadingFlag==true){

            console.log("RESULTS");

            /**Return every component of profile page*
             */

            /**set up the full name of the person**/
            var givenName = this.state.given_name1;
            var lastName = this.state.last_name;
            var fullName = givenName + ' ' + lastName;

            /**render 'person' profile**/
            if((this.state.index == 'entity_store') && (this.state.type == 'person')){

                console.log(this.state.country_citizen);

                return(

                    /**render the person profile*
                     *
                     * */

                    <div>

                        <Search></Search>

                        <div className="col-lg-6">

                            <PersonInfo citizenship = {this.state.country_citizen} name = {fullName} weight = {this.state.weight} build = {this.state.build}

                                        DoB = {this.state.dob} eye = {this.state.colour_eyes} hair = {this.state.colour_hair} height = {this.state.height}></PersonInfo>




                        </div>

                        <div className="col-lg-6">

                            <img src={require("components/entityprofile/person_image/image_" + this.state.random_number + ".png")} className="logo"/>


                        </div>


                    </div>





                );

            }
            /**render 'location' profile**/
            else if((this.state.index == 'entity_store') && (this.state.type == 'location')){


                return(

                    /**render the person profile**/

                    <div>

                        <Search></Search>

                        <div className="col-lg-6">

                            <LocationInfo title = {this.state.title} location_id = {this.state.id} country = {this.state.country} pcode = {this.state.pcode} city_town = {this.state.city_town}
                                          date_created = {this.state.date_created} street_number = {this.state.street_number} street_name = {this.state.street_name} street_type = {this.state.street_type}
                                          state = {this.state.state}></LocationInfo>


                        </div>


                    </div>

                );
            }

            /**render 'case' profile**/
            else if((this.state.index == 'entity_store') && (this.state.type == 'case')){


                return(

                    /**render the person profile**/

                    <div>

                        <Search></Search>

                        <div className="col-lg-6">

                            <CaseInfo case_id = {this.state.case_id} incident_type = {this.state.incident_type} involvement_type = {this.state.involvement_type} ></CaseInfo>


                        </div>


                    </div>

                );


            }

            /**render 'document' profile**/
            else if(this.state.index == 'extracts'){

                return(

                    <div>

                        <DocInfo index = {this.state.index} type = {this.state.type} docid = {this.state.entityid}
                                 subject = {this.state.subject} relation = {this.state.relation} object = {this.state.object}
                                 date_parsed = {this.state.date_parsed} confirmedBy = {this.state.confirmedBy}
                                 source = {this.state.source} dateConfirmed = {this.state.dateConfirmed}
                                 status = {this.state.status}></DocInfo>

                        <RelevantInfo source_name = {this.state.source}></RelevantInfo>

                    </div>


                );
            }


        }else{

            console.log("LOADING");

            /**this else branch should exist. If not, the 'If' branch would not execute. 2 Feb 2016**/
            return <div>Loading...</div>;


        }



    }


});

export default EntityProfile;

