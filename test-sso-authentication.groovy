pipeline {
    agent any

    parameters {
        string(name: 'SSO_ENDPOINT', defaultValue: 'https://your-sso-server.com/api/check-auth', description: 'SSO Endpoint to check authentication status')
        string(name: 'AUTH_TOKEN', defaultValue: '', description: 'Optional Bearer Token for authentication')
        booleanParam(name: 'ENABLE_VERBOSE', defaultValue: false, description: 'Enable verbose output for curl')
    }

    stages {
        stage('Test SSO Connection') {
            steps {
                script {
                    // Fetch parameters
                    def ssoEndpoint = params.SSO_ENDPOINT
                    def authToken = params.AUTH_TOKEN
                    def enableVerbose = params.ENABLE_VERBOSE

                    // Prepare the curl command
                    def curlCommand = "curl -s -o /dev/null -w '%{http_code}' ${enableVerbose ? '-v' : ''} ${ssoEndpoint}"

                    // Add Authorization header if a token is provided
                    if (authToken) {
                        curlCommand = "curl -s -o /dev/null -w '%{http_code}' -H 'Authorization: Bearer ${authToken}' ${enableVerbose ? '-v' : ''} ${ssoEndpoint}"
                    }

                    // Execute the curl command
                    def response = sh(script: curlCommand, returnStdout: true).trim()

                    // Handle HTTP response
                    if (response == '200') {
                        echo "SSO is working correctly. HTTP response code: ${response}"
                    } else {
                        error "SSO test failed. HTTP response code: ${response}"
                    }
                }
            }
        }
    }
}
