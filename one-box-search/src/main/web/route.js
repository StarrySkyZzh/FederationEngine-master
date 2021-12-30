import React from 'react'
import ReactDOM from 'react-dom'
import { Router, Route, IndexRoute, browserHistory } from 'react-router'
import App from 'components/app/app'
import About from 'components/about/about'
import Home from 'components/home/home'

// import EntitySearch from 'components/entitysearch/SearchResultDisplay'
// import EntityProfile from 'components/entityprofile/entityprofile'
// import PdfInfo from 'components/pdfinfo/pdfinfo'

/**
 * 'require("{resource_name}")' statements act as "imports" for assets.
 * Webpack will resolve the path and convert resources to embedded
 * scripts or URLs.
 */
require("main.styl");

/**
 * Route configuration for the Application
 *
 *
 */



ReactDOM.render((
    <Router history={browserHistory}>
        <Route path="/" component={App}>
            <IndexRoute component={Home}>
                /**This is the new added route from OBS page to Ranked Result Page**/
                {/*<Route path = "entitysearch/(:query)" component={EntitySearch} />*/}
                /**This is the new added route from Relevant Info Page to a pdf page**/
                {/*<Route path = "pdfinfo/(:keyword)" component={PdfInfo}></Route>*/}
            </IndexRoute>
            /**This is the new added route from Ranked Result Page to Entity (E.g. Person, Location, Case...) Profile Page**/
            {/*<Route path = "entityprofile/(:indexname)/(:typename)/(:entityid)" component={EntityProfile} />*/}
            <Route path="about" component={About} />
        </Route>
    </Router>
), document.getElementById('app'));
