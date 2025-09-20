const { composePlugins, withNx } = require("@nx/webpack");

// Nx plugins for webpack.
module.exports = composePlugins(withNx(), (config) => {
  // Handle ES modules
  config.externals = {
    '@sindresorhus/slugify': 'commonjs @sindresorhus/slugify'
  };
  
  return config;
});
