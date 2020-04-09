var pkgjson = require('./package.json');

var config = {
		pkg: pkgjson,
		src: 'node_modules',
		dist: 'dist',
		vendor: 'vendor'
}

module.exports = function (grunt) {

	// Configuration
	grunt.initConfig({
		config: config,
		pkg: config.pkg,
		bowercopy: {
			options: {
				srcPrefix: 'node_modules'
			},
			scripts: {
				options: {
					destPrefix: '<%= config.vendor %>/'
				},
				files: {
					'polyfill':			'@babel/polyfill/dist',
					'jquery': 				'jquery/dist/*.js',
					'lodash':				'lodash/lodash*.js',
					'bootstrap':			'bootstrap/dist/css/*.min.css'

				}
			}
		},
		copy: {
			dist: {
				files: [
				        {expand: true, cwd: '<%= config.src %>/', src: '**/*.js',
				        	dest: '<%= config.dist %>'
				        }
				        ]
			}
		}
	});

	grunt.loadNpmTasks('grunt-bowercopy');

	grunt.registerTask('default', [
	                               'bowercopy'
	                               ]);
};
