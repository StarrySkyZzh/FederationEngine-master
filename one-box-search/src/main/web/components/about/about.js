import React from 'react'

/**
 * About page component
 */
const About = React.createClass({

    /**
     * Renders the About page
     * @returns {XML} the page content
     */
    render() {
        console.log("test");
        return (
            <div>
                <h3>README</h3>
                <h5>The law enforcement environment has been dramatically changed in the past 20 years. In order to catch up with these changes and meet the challenges in the era of big data, the objective of the Integrated Law Enforcement project is to develop, integrate and demonstrate state-of-the-art capabilities in federated data management, entity resolution and federated analytics to provide law enforcement agencies and analysts with uniform and timely access to integrated information and analytical outcomes. To meet the objective, a set of cutting edge technologies will be comprised to effectively link all available data among internal information systems and external data sources and reveals the relationships among data segments of various natures and types, so that an overall image around the topic of interest can be quickly formed.</h5>
                <br/>
                <h4>What is this repository for?</h4>
                <ul>
                    <li>This is the repository for codes that can facilitate a quickstart for building the federated data platform.</li>
                    <li>Version 1.0POC</li>
                </ul>
                <br/>
                <h4>How do I get set up?</h4>
                <ul>
                    <li>Set up, Configuration & Deployment instructions can be found in the following document:
                        <a href="https://d2dcrc.atlassian.net/login"> Federated Process Infrastructure Volume 1 version 1.0POC - Testing Platform Installation</a></li>
                </ul>
                <br/>
                <h4>Contact</h4>
                <h5>ACRC, UniSA</h5>
                <h5>Mawson lakes, SA 5067</h5>
                <h5>Ph: +61 (08) 8302 3582</h5>
                <h5>ACRC.Enquiries@unisa.edu.au</h5>
                <h5><a href="http://www.unisa.edu.au">www.unisa.edu.au</a></h5>
            </div>



        );
    }
});

export default About;
