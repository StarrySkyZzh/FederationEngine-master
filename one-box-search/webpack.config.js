let CopyWebpackPlugin = require('copy-webpack-plugin');
let path = require('path');

module.exports = {
    entry: './src/main/web/route.js',
    devtool: 'source-map',
    cache: true,
    debug: true,
    output: {
        path: path.join(__dirname, 'src/main/resources/static'),
        filename: 'bundle.js'
    },
    plugins: [
        new CopyWebpackPlugin([
                                  {from: 'src/main/web/index.html', to: 'index.html'},
                                  {from: 'src/main/web/favicon.ico', to: 'favicon.ico'}
                              ])
    ],
    resolve: {
        extensions: ['', '.js'],
        root: [
            path.join(__dirname, 'src/main/web')
        ]
    },
    module: {
        loaders: [
            {test: path.join(__dirname, 'src/main/web'), loader: 'babel-loader'},
            {test: /\.styl$/, loaders: ['style-loader', 'css-loader', 'stylus-loader']},
            {test: /\.(png|jpg|svg|ttf|woff|woff2|eot)$/, loader: 'url-loader?limit=8192'},
            {test: /\.json$/, loader: "json-loader"}
            /**this row is added on 20 Feb 17 in order to import 'node-webhdfs'**/
        ]
    },
    devServer: {
        proxy: {
            '*': {
                target: 'http://localhost:8080'
            }
        },
        headers: {
            "Access-Control-Allow-Origin": "*",
            "Access-Control-Allow-Methods": "GET, PUT, POST",
            "Access-Control-Allow-Headers": "true"
        }
    },
    /**Below is added on 20 Feb 17 in order to import 'node-webhdfs/webhdfs'**/
    node: {
        fs: 'empty',
        net: 'empty',
        tls: 'empty'
    }
    /**Above is added on 20 Feb 17 in order to import 'node-webhdfs/webhdfs'**/
};