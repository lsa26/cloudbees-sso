# Jenkins Pipeline to Test SSO Authentication

This Jenkins pipeline script automates the testing of Single Sign-On (SSO) connectivity after a restart. It checks if the SSO service is working properly by sending a request to a specified endpoint and verifying the response.

## Prerequisites
- Jenkins or CloudBees CI instance.
- The **curl** command must be available on the Jenkins agent where this job will run.
- An SSO endpoint for testing the authentication service.
- Optional: A Bearer token for authentication if the endpoint requires it.

## Parameters
- **SSO_ENDPOINT**: The URL of the SSO endpoint to check. This is the endpoint you will query to validate the authentication status.
- **AUTH_TOKEN** (optional): The Bearer token for authorization if required by the SSO service.
- **ENABLE_VERBOSE**: A boolean parameter to enable verbose output for the `curl` request. This is helpful for debugging.

## Usage

### Steps to Set Up the Jenkins Job
1. **Create a new Pipeline Job**:
   - In your Jenkins or CloudBees CI dashboard, create a new job of type **Pipeline**.
   
2. **Add the Pipeline Script**:
   - Copy the contents of `Jenkinsfile.groovy` (or the Groovy script provided) and paste it into the Pipeline Script section of your job configuration.

3. **Configure the Parameters**:
   - Specify the **SSO_ENDPOINT** (the URL to the endpoint for authentication check).
   - If your SSO requires a Bearer Token, add the **AUTH_TOKEN** parameter.
   - Set **ENABLE_VERBOSE** to `true` if you need to see detailed logs of the HTTP request.

4. **Run the Job**:
   - Trigger the pipeline job to test the SSO connectivity.

### Example Pipeline Script

```groovy
pipeline {
    agent any

    parameters {
        string(name: 'SSO_ENDPOINT', defaultValue: 'https://your-sso-server.com/api/check-auth', description: 'SSO Endpoint to check the authentication status')
        string(name: 'AUTH_TOKEN', defaultValue: '', description: 'Optional Bearer Token for authentication')
        booleanParam(name: 'ENABLE_VERBOSE', defaultValue: false, description: 'Enable verbose output for curl')
    }

    stages {
        stage('Test SSO Connection') {
            steps {
                script {
                    // Fetch the parameters
                    def ssoEndpoint = params.SSO_ENDPOINT
                    def authToken = params.AUTH_TOKEN
                    def enableVerbose = params.ENABLE_VERBOSE

                    // Prepare the curl command
                    def curlCommand = "curl -s -o /dev/null -w '%{http_code}' ${enableVerbose ? '-v' : ''} ${ssoEndpoint}"

                    // Add authorization header if token is provided
                    if (authToken) {
                        curlCommand = "curl -s -o /dev/null -w '%{http_code}' -H 'Authorization: Bearer ${authToken}' ${enableVerbose ? '-v' : ''} ${ssoEndpoint}"
                    }

                    // Run the curl command
                    def response = sh(script: curlCommand, returnStdout: true).trim()

                    // Check HTTP response code
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
```

## Script details

### Parameters:
You define three parameters:

- **SSO_ENDPOINT**: The URL to the SSO service endpoint. This parameter is required to specify the endpoint for authentication testing.
- **AUTH_TOKEN** (optional): An authentication token (e.g., Bearer token). This token is added to the request header if the SSO service requires authentication.
- **ENABLE_VERBOSE**: A boolean parameter that, when set to true, enables more detailed logs of the HTTP request. This is useful for debugging.

### Curl Command:
The script constructs a `curl` command to test the SSO endpoint. If an authentication token (`AUTH_TOKEN`) is provided, it is added to the request header as an `Authorization` header using the Bearer token.

### Response Handling:
After the request is sent, the script checks the HTTP response. If the response code is `200`, it indicates that the SSO service is functioning properly. Any other response code will result in a failure, and the script will notify the user accordingly.

## Troubleshooting

- **HTTP Code Other Than 200**: If the response code is not `200`, verify the SSO endpoint URL and ensure that the SSO service is running correctly.
- **Verbose Logs**: Enable the `ENABLE_VERBOSE` option to get more detailed logs of the `curl` request for better debugging. This can help identify issues in the request or response.

## Additional Resources

- [CloudBees CI Documentation: Authentication and SSO](https://docs.cloudbees.com/docs/cloudbees-ci/latest/secure/using-sso)
- [curl Documentation](https://curl.se/docs/)
