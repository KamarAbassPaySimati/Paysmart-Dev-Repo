package com.afrimax.paymaart.util

import com.afrimax.paymaart.BuildConfig
import org.json.JSONObject

class AmplifyConfigCreator {

    private val auth = "auth"
    private val plugins = "plugins"
    private val awsCognitoAuthPlugin = "awsCognitoAuthPlugin"
    private val credentialsProvider = "CredentialsProvider"
    private val cognitoUserPool = "CognitoUserPool"
    private val cognitoIdentity = "CognitoIdentity"
    private val default = "Default"
    private val poolId = "PoolId"
    private val regionCaps = "Region"
    private val regionSmall = "region"
    private val appClientId = "AppClientId"
    private val storage = "storage"
    private val awsS3StoragePlugin = "awsS3StoragePlugin"
    private val bucket = "bucket"

    fun createJson(): JSONObject {
        val rootObject = JSONObject()
        val authObject = JSONObject()
        val storageObject = JSONObject()
        val pluginAuthObject = JSONObject()
        val pluginStorageObject = JSONObject()
        val awsCognitoAuthPluginObject = JSONObject()
        val awsS3StoragePluginObject = JSONObject()

        val credentialsProviderObject = JSONObject()
        val cognitoIdentityObject = JSONObject()
        val defaultObject1 = JSONObject()

        val cognitoUserPoolObject = JSONObject()
        val defaultObject2 = JSONObject()

        //Create Auth object
        defaultObject1.put(poolId, BuildConfig.CUSTOMER_COGNITO_IDENTITY_POOL_ID)
        defaultObject1.put(regionCaps, BuildConfig.REGION)
        cognitoIdentityObject.put(default, defaultObject1)
        credentialsProviderObject.put(cognitoIdentity, cognitoIdentityObject)

        defaultObject2.put(poolId, BuildConfig.CUSTOMER_COGNITO_USERPOOL_ID)
        defaultObject2.put(appClientId, BuildConfig.CUSTOMER_COGNITO_CLIENT_ID)
        defaultObject2.put(regionCaps, BuildConfig.REGION)
        cognitoUserPoolObject.put(default, defaultObject2)

        awsCognitoAuthPluginObject.put(credentialsProvider, credentialsProviderObject)
        awsCognitoAuthPluginObject.put(cognitoUserPool, cognitoUserPoolObject)

        pluginAuthObject.put(awsCognitoAuthPlugin, awsCognitoAuthPluginObject)
        authObject.put(plugins, pluginAuthObject)

        //Create Storage object
        awsS3StoragePluginObject.put(bucket, BuildConfig.CUSTOMER_S3_BUCKET_NAME)
        awsS3StoragePluginObject.put(regionSmall, BuildConfig.REGION)
        pluginStorageObject.put(awsS3StoragePlugin, awsS3StoragePluginObject)
        storageObject.put(plugins, pluginStorageObject)

        rootObject.put(auth, authObject)
        rootObject.put(storage, storageObject)

        /**
         * Example JSON output
         * {
         *     "auth": {
         *         "plugins": {
         *             "awsCognitoAuthPlugin": {
         *                 "CredentialsProvider": {
         *                     "CognitoIdentity": {
         *                         "Default": {
         *                             "PoolId": "****",
         *                             "Region": "****"
         *                         }
         *                     }
         *                 },
         *                 "CognitoUserPool": {
         *                     "Default": {
         *                         "PoolId": "****",
         *                         "AppClientId": "****",
         *                         "Region": "****"
         *                     }
         *                 }
         *             }
         *         }
         *     },
         *     "storage": {
         *     "plugins": {
         *       "awsS3StoragePlugin": {
         *         "bucket": "****",
         *         "region": "****"
         *       }
         *     }
         *   }
         * }
         */


        return rootObject

    }
}