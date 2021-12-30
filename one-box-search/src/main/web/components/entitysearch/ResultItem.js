import React from 'react'

class ResultItem extends React.Component{

    constructor(props) {
        super(props);
        this.state = {};
    }

    searchFromJSONArray(array, key){
        for(let i = 0; i<array.length; i++){
            if (array[i].hasOwnProperty(key)){
                return array[i][key];
            }
        }
        return undefined;
    }

    render(){

        var warningStyle = {
            color: 'Tomato'
        };
        var successStyle = {
            color: 'Green'
        };

        console.log("rendering ResultItem.js");

        let entityTypeImage="";
        let contentsToShow = null;
        if(this.props.info.type === 'person'){
            entityTypeImage = "person.png";
            if (this.props.info.source === 'poler'){
                contentsToShow = (
                    <div>
                        <h2>{this.props.payload.personNames[0].personalName + " " + this.props.payload.personNames[0].familyName}</h2>
                        <div>Gender: {this.props.payload.genderCd}</div>
                        <div>DoB: {this.props.payload.birthDate}</div>
                        <div>Height: {this.props.payload.personPhysicalDescriptions[0].height}</div>
                        <div>Eye: {this.props.payload.eyeColourCd}</div>
                        <div>Weight: {this.props.payload.personPhysicalDescriptions[0].weight}</div>
                        <div>Build: {this.props.payload.personPhysicalDescriptions[0].buildCd}</div>
                        <div>Citizenship: {this.props.payload.personCitizenships[0].citizenshipCd}</div>
                        <div>Alerts:
                            <span style= {warningStyle}>
                                {this.this.props.payload.warningInd}
                            </span>
                        </div>
                        <div>Reason:
                            <span style= {warningStyle}>
                                {this.this.props.payload.warningReason}
                                </span>
                        </div>
                    </div>
                );
            } else if (this.props.info.source === 'lei'){
                contentsToShow = (
                    <div>
                        <h2>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'given_name1')}&nbsp;</span>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'given_name2')}&nbsp;</span>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'last_name')}</span>
                        </h2>
                        <div>Gender: {this.searchFromJSONArray(this.props.payload.profile,'gender')}</div>
                        <div>DoB: {this.searchFromJSONArray(this.props.payload.profile,'dob')}</div>
                        <div>Height: {this.searchFromJSONArray(this.props.payload.profile,'height')}</div>
                        <div>Eye: {this.searchFromJSONArray(this.props.payload.profile,'colour_eyes_desc')}</div>
                        <div>Weight: {this.searchFromJSONArray(this.props.payload.profile,'weight')}</div>
                        <div>Build: {this.searchFromJSONArray(this.props.payload.profile,'build_desc')}</div>
                        <div>Citizenship:
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'country_citizen_desc')}&nbsp;</span>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'citizenship_status_desc')}&nbsp;</span>
                        </div>
                        <div>Alerts:
                            <span style= {warningStyle}>
                                {this.searchFromJSONArray(this.props.payload.profile,'alerts_count')}
                            </span>
                        </div>
                        <div>Crime History:
                            <span style= {warningStyle}>
                                {this.searchFromJSONArray(this.props.payload.profile,'crim_histories_count')}
                            </span>
                        </div>
                    </div>
                );
            } else if (this.props.info.source === 'promis'){
                contentsToShow = (
                    <div>
                        <h2>
                            <span>{this.props.payload.first_name}&nbsp;</span>
                            <span>{this.props.payload.given_name2}&nbsp;</span>
                            <span>{this.props.payload.last_name}</span>
                        </h2>
                        <div>Gender: {this.props.payload.gender}</div>
                        <div>DoB: {this.props.payload.dob}</div>
                        <div>Height: {this.props.payload.height}</div>
                        <div>Eye: {this.props.payload.colour_eyes}</div>
                        <div>Weight: {this.props.payload.weight}</div>
                        <div>Build: {this.props.payload.build}</div>
                        <div>Citizenship:
                            <span>{this.props.payload.country_citizen}&nbsp;</span>
                            <span>{this.props.payload.citizenship_status}&nbsp;</span>
                        </div>
                    </div>
                );
            } else if (this.props.info.source === 'es'){
                contentsToShow = (
                    <div>
                        <h2>
                            <span>{this.props.payload.first_name}&nbsp;</span>
                            <span>{this.props.payload.given_name2}&nbsp;</span>
                            <span>{this.props.payload.last_name}</span>
                        </h2>
                        <div>Gender: {this.props.payload.gender}</div>
                        <div>DoB: {this.props.payload.dob}</div>
                        <div>Height: {this.props.payload.height}</div>
                        <div>Eye: {this.props.payload.colour_eyes}</div>
                        <div>Weight: {this.props.payload.weight}</div>
                        <div>Build: {this.props.payload.build}</div>
                        <div>Citizenship:
                            <span>{this.props.payload.country_citizen}&nbsp;</span>
                            <span>{this.props.payload.citizenship_status}&nbsp;</span>
                        </div>
                    </div>
                );
            }
        }

        if(this.props.info.type === 'location'){
            entityTypeImage = "location.png";
            if (this.props.info.source === 'lei'){
                contentsToShow = (
                    <div>
                        <h2>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'street_no')}&nbsp;</span>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'street_name1')}&nbsp;</span>
                            <span>{this.searchFromJSONArray(this.props.payload.profile,'city_town')}</span>
                        </h2>
                        <div>Country: {this.searchFromJSONArray(this.props.payload.profile,'country_code_desc')}</div>
                        <div>PCode: {this.searchFromJSONArray(this.props.payload.profile,'pcode')}</div>
                        <div>City/Town: {this.searchFromJSONArray(this.props.payload.profile,'city_town')}</div>
                        <div>Street Type: {this.searchFromJSONArray(this.props.payload.profile,'street_type')}</div>
                        <div>State: {this.searchFromJSONArray(this.props.payload.profile,'state')}</div>
                    </div>
                );
            } else if (this.props.info.source === 'promis'){
                contentsToShow = (
                    <div>
                        <h2>
                            <span>{this.props.payload.state}&nbsp;</span>
                        </h2>
                        <div>Country: {this.props.payload.city_flag}&nbsp;</div>
                        <div>PCode: {this.props.payload.source_type_desc}&nbsp;</div>
                        <div>City/Town: {this.props.payload.date_time_last_modified}&nbsp;</div>
                        <div>Street Type: {this.props.payload.street_type}&nbsp;</div>
                        <div>State: {this.props.payload.flat_no_type}&nbsp;</div>
                    </div>
                );
            } else if (this.props.info.source === 'es'){
                contentsToShow = (
                    <div>
                        <h2>{this.props.payload.title}</h2>
                        <div>Location ID: {this.props.payload.location_id}</div>
                        <div>Street Type: {this.props.payload.street_type}</div>
                    </div>
                );
            }
        }

        if(this.props.info.type === 'case'){
            entityTypeImage = "case.png";
            if (this.props.info.source === 'es'){
                contentsToShow = (
                    <div>
                        <h2>{this.props.payload.id}</h2>
                        <div>Incident Type: {this.props.payload.incident_type}</div>
                        <div>Involvement Type: {this.props.payload.involvement_type}</div>
                    </div>
                );
            } else if (this.props.info.source === 'promis'){
                contentsToShow = (
                    <div>
                        <h2>{this.props.payload.case_id}</h2>
                        <div>Incident Type: {this.props.payload.incident_type}</div>
                        <div>Involvement Type: {this.props.payload.involvement_type}</div>
                        <div>Title: {this.props.payload.title}</div>
                        </div>
                );
            }
        }

        if(this.props.info.type === 'document content' || this.props.info.type === 'document binary'){
            entityTypeImage = "document.png";
            contentsToShow = (
                <div>
                    <div>Format: {this.props.payload.format}</div>
                </div>
            );
        }



        return(
            <div className="row">
                <div className="col-sm-1"/>
                <div className="col-sm-1">
                    <img src={require("components/entitysearch/entity_type_image/"+entityTypeImage)} className="logo" width="50" height="50"/>
                </div>
                <div className="col-sm-9">
                    <strong>{contentsToShow}</strong>
                    <div><br/></div>
                    <div>DETAILS : </div>
                    <div>ID : {this.props.payload.id === undefined? <span style= {warningStyle}>undefined</span>:<span style= {successStyle}>{this.props.payload.id}</span>}</div>
                    <div>TYPE : {this.props.info.type}</div>
                    <div>SOURCE : {this.props.info.source}</div>
                    <div><pre>{JSON.stringify(this.props.payload, null, 2) }</pre></div>
                </div>
            </div>
        )

    }
}

module.exports = ResultItem;