/**
 * Created by fengz on 1/11/2016.
 */

import React from 'react'


const Search = React.createClass({




    render (){

        return (

            <form>

                <div className="form-group row">


                    <label for="text" className="col-lg-6 col-form-label">Back</label>



                    <div className="col-lg-6">

                        <div className="input-group">

                            <span className="input-group-btn">

                                <button className="btn btn-secondary" type="button">Go!</button>

                            </span>

                            <input type="text" className="form-control" id="person" placeholder="Search" ></input>


                        </div>

                    </div>

                </div>

            </form>

        );

    }

});

module.exports = Search;