# Spring Boot and React.js Template

Template web application using Spring Boot and React.js.

The project builds with maven, uses npm to install frontend dependencies and webpack to compile/transpile frontend assets (Javascript, CSS etc).

Frontend code is written in JavaScript (ES6) and React's JSX.

## Getting Started

### Required Dependencies

- Install Maven: `brew install maven`
- Install Java: `brew tap caskroom/cask`,`brew install brew-cask`,`brew cask install java`
- Install Node.js and npm: `brew install node`
- Install webpack: `npm install webpack -g`
- Install webpack: `npm install webpack-dev-server -g`

### Optional (but recommended) Development Tools

- Install the [React Developer Tools](https://chrome.google.com/webstore/detail/react-developer-tools/fmkadmapgofadopljbjfkapdkoienihi?hl=en) browser extension

### Front End Dev

Run `webpack-dev-server --content-base src/main/resources/static/ --port 8081 --inline`

The front-end will be available at http://localhost:8081 and will proxy all api requests to the backend at localhost:8080.

Javascript/CSS changes will automatically trigger a browser refresh.

### Back End Dev

Run the app in *Debug* mode in IntelliJ. Hot-deploy Java changes with `Build -> Make Project` from the IntelliJ menu.
Java changes will be hot deployed where possible, but may result in a "lightweight" server restart.

## Production

- Build both the web and server components with `mvn clean verify`. This automatically runs webpack and includes the front-end resources in the fat jar.
- Run the application using `java -jar {{ jar_name }}.jar`
