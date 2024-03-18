#!/bin/bash

API_ENDPOINT="https://manual-api.lambdatest.com/app/upload/realDevice"

APK_FILE="$1"

response=$(curl -X POST "https://manual-api.lambdatest.com/app/upload/realDevice" \
 -H "accept: application/json" \
 -H "Content-Type: multipart/form-data" \
 -F "name=Paymaart-Customer-$BUILD_NUMBER" \
 -F "visibility=team" \
 -F "appFile=@$APK_FILE"\
 -u "$LT_USERNAME:$LT_ACCESS_KEY" \
 --http1.1 )
# Print response from LambdaTest
echo "Response: $response"
