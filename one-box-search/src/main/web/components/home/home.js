import React from 'react';
import axios from 'axios'

import SearchResultDisplay from 'components/entitysearch/SearchResultDisplay'

/**
 * Home page component
 */

class Home extends React.Component{

    /**
     * Set the initial state for the Home page
     * @returns {Object} the initial state*/

    constructor(props) {
        super(props);
        this.state = {
            poler: false,
            promis: false,
            es: false,
            lei: false,
            hdfs: false,
            person: false,
            location: false,
            case: false,
            typeAll: false,
            service: '',
            update: false,
            criteria: {},
            data:null
        };
        this.handleChange = this.handleChange.bind(this);
        this.submit = this.submit.bind(this);
        this.choseAll = this.choseAll.bind(this);
    }

    handleChange(event) {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        const newState = {};
        newState[name] = value;
        this.setState(newState);
        console.log(name, ' -> ', value);
    }

    choseAll(event) {
        this.handleChange(event);
        let checked = event.target.checked;
        let names = ['person', 'location', 'case'];
        names.map((name)=>{
            this.setState({
            [name]:checked
            });
        })
    }

    /**
     * Renders the Home page
     * @returns {XML} the page content
     */

    submit(event) {
        this.doAxiosQuery(this.state);
    }

    doAxiosQuery(props) {
        console.log('doAxiosQuery');
        let query = this.createQuery(props);
        axios.post('http://103.61.226.11:8091/query', query)
            .then(function (response) {
                this.setState({
                                  update:true,
                                  data:response.data
                              });
                this.setState({update:false});
            }.bind(this)).catch(function (error) {
                console.log(error);
                alert(error);
            });
    }

    createQuery(props){
        let types = [];
        let allTypes = ['person', 'location', 'case'];
        allTypes.map((name)=>{
            if (this.state[name] === true) {
                types.push(name);
            }
        });
        console.log(types);

        let sources = [];
        let allSources = ['poler', 'promis', 'es', 'lei', 'hdfs'];
        allSources.map((name)=>{
            if (this.state[name] === true) {
                sources.push(name);
            }
        });
        console.log(sources);
        console.log('props.criteria');
        console.log(props.criteria);
        let criteriaJSON;
        try{
            criteriaJSON = JSON.parse('{'+props.criteria+'}');
        } catch(error) {
            console.log(error);
            alert(error);
        }

        if (props.service === 'Standard query'){
            let query = {
                "query":{
                    "scope":types,
                    "output":{"project":{}},
                    "window":{"limit":"100"},
                    "filter":criteriaJSON},
                "control":{
                    "operation":"findEntities",
                    "sources":sources},
                "credentials":{
                    "credentiallist":[
                        {"es":{"username":"unisaile","password":"unisaile"}},
                        {"lei":{"username":"unisaile","password":"unisaile"}},
                        {"promis":{"username":"unisa","password":"unisa"}},
                        {"poler":{"username":"poler","password":"nefUphuch!ahE"}}]
                }
            };
            return query;
        }
        if (props.service === 'Query by keywords'){
            let query = {
                "query":{
                    "scope":types,
                    "output":{
                        "project":{}},
                    "window":{"limit":"100"},
                    "filter":criteriaJSON},
                "control":{
                    "operation":"findEntitiesByKeyword",
                    "sources":sources},
                "credentials":{
                    "credentiallist":[
                        {"es":{"username":"unisaile","password":"unisaile"}},
                        {"promis":{"username":"unisa","password":"unisa"}},
                        {"lei":{"username":"unisa","password":"unisa"}},
                        {"poler":{"username":"poler","password":"nefUphuch!ahE"}}]}
        };
            return query;
        }
        if (props.service === 'Get text content'){
            let query = {
                "query":{
                    "filter":criteriaJSON},
                "control":{
                    "operation":"getTextContent",
                    "sources":["hdfs"]},
                "credentials":{
                    "credentiallist":[
                        {"es":{"username":"unisaile","password":"unisaile"}},
                        {"lei":{"username":"unisaile","password":"unisaile"}},
                        {"promis":{"username":"unisa","password":"unisa"}},
                        {"poler":{"username":"poler","password":"nefUphuch!ahE"}},
                        {"hdfs":{"username":"ubuntu","password":"ubuntu"}}
                    ]}
            };
            return query;
        }
        if (props.service === 'Get binary content'){
            let query = {
                "query":{
                    "filter":criteriaJSON},
                "control":{
                    "operation":"getBinaryContent",
                    "sources":["hdfs"]},
                "credentials":{
                    "credentiallist":[
                        {"es":{"username":"unisaile","password":"unisaile"}},
                        {"lei":{"username":"unisaile","password":"unisaile"}},
                        {"promis":{"username":"unisa","password":"unisa"}},
                        {"poler":{"username":"poler","password":"nefUphuch!ahE"}},
                        {"hdfs":{"username":"ubuntu","password":"ubuntu"}}
                    ]}
            };
            return query;
        }
    }

    render() {
        console.log('rendering home.js');
        return (
                <form>
                    <div>
                        <br/><br/><br/><br/>
                        <div className="col-sm-12 col-sm-offset-4">
                            <img src={require("components/home/unisa_logo.png")} className="logo"/>
                        </div>
                        <div className="col-sm-12">
                            <br/><br/>
                            <div className="col-sm-4"></div>
                            <div className="col-sm-1">
                                <strong>Sources:</strong>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="poler" onChange={this.handleChange} checked={this.state.poler}/>
                                    <strong>Poler</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="promis" onChange={this.handleChange} checked={this.state.promis}/>
                                    <strong>Promis</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="es" onChange={this.handleChange} checked={this.state.es}/>
                                    <strong>ES</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="lei" onChange={this.handleChange} checked={this.state.lei}/>
                                    <strong>LEI</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="hdfs" onChange={this.handleChange} checked={this.state.hdfs}/>
                                    <strong>HDFS</strong>
                                </label>
                            </div>
                        </div>

                        <div className="col-sm-12">
                            <div className="col-sm-4"></div>
                            <div className="col-sm-1">
                                <strong>Types:</strong>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="person" onChange={this.handleChange} checked={this.state.person}/>
                                    <strong>Person</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="location" onChange={this.handleChange} checked={this.state.location}/>
                                    <strong>Location</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="case" onChange={this.handleChange} checked={this.state.case}/>
                                    <strong>Case</strong>
                                </label>
                            </div>
                            <div className="col-sm-1">
                                <label className="checkbox-inline">
                                    <input type="checkbox" name="typeAll" onChange={this.choseAll} checked={this.state.typeAll}/>
                                    <strong>All</strong>
                                </label>
                            </div>
                        </div>

                        <div className="col-sm-12">
                            <div className="col-sm-2"/>
                            <div className="col-sm-2">
                                <select className="form-control" name="service" onChange={this.handleChange}>
                                    <option>Select service type</option>
                                    <option>Standard query</option>
                                    <option>Query by keywords</option>
                                    <option>Get text content</option>
                                    <option>Get binary content</option>
                                </select>
                            </div>
                            <div className="input-group col-sm-6">
                                <input type="text" className="form-control" placeholder="Input search criteria" name="criteria" onChange={this.handleChange}/>
                                <span className="input-group-btn">
                                    <button className="btn btn-primary" type="button" name="submit" onClick={this.submit}>Go!</button>
                                </span>
                            </div>
                            <br/><br/><br/><br/>
                        </div>
                    </div>
                    <div>
                        <div className="col-sm-12">
                            <SearchResultDisplay data = {this.state.data} update = {this.state.update}/>
                        </div>
                    </div>
                </form>
        );
    }
}

export default Home;