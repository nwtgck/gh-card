const webpack = require('webpack');

module.exports = {
  // (from: https://cli.vuejs.org/config/#publicpath)
  publicPath: "./",
  configureWebpack: {
    plugins: [
      // (base: https://medium.com/curofy-engineering/a-guide-to-inject-variable-into-your-code-using-webpack-36c49fcc1dcd)
      new webpack.DefinePlugin({
        IMAGE_SERVER_URL: process.env.NODE_ENV === 'production' ? "location.origin" : "'http://localhost:8080'"
      })
    ]
  }
};
