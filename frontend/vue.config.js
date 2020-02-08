const webpack = require('webpack');

module.exports = {
  // (from: https://cli.vuejs.org/config/#publicpath)
  publicPath: "./",
  configureWebpack: {
    plugins: [
      // (base: https://medium.com/curofy-engineering/a-guide-to-inject-variable-into-your-code-using-webpack-36c49fcc1dcd)
      new webpack.DefinePlugin({
        IMAGE_SERVER_URL: (() => {
          if (process.env.GH_CARD_IMAGE_SERVER_URL) {
            // NOTE: Add quotes
            return JSON.stringify(process.env.GH_CARD_IMAGE_SERVER_URL)
          } else if (process.env.NODE_ENV === 'production') {
            return "location.origin";
          } else {
            return "'http://localhost:8080'";
          }
        })(),
      })
    ]
  }
};
