# s3mq
[![Build Status](https://travis-ci.org/uio-bmi/s3mq.svg?branch=master)](https://travis-ci.org/uio-bmi/s3mq)

## LocalEGA S3 AMQP proxy

This component converts AMQP message from S3 format to Local/Central EGA format.

S3 message sample:

```json
    {
   "Records":[
      {
         "eventVersion":"2.0",
         "eventSource":"aws:s3",
         "awsRegion":"",
         "eventTime":"2016-09-08T22:34:38.226Z",
         "eventName":"s3:ObjectCreated:Put",
         "userIdentity":{
            "principalId":"minio"
         },
         "requestParameters":{
            "sourceIPAddress":"10.1.10.150:44576"
         },
         "responseElements":{

         },
         "s3":{
            "s3SchemaVersion":"1.0",
            "configurationId":"Config",
            "bucket":{
               "name":"john",
               "ownerIdentity":{
                  "principalId":"minio"
               },
               "arn":"arn:aws:s3:::images"
            },
            "object":{
               "key":"kitty.jpg",
               "size":200436,
               "sequencer":"147279EAF9F40933"
            }
         }
      }
   ],
   "level":"info",
   "msg":"",
   "time":"2016-09-08T15:34:38-07:00"
}
```

Local/Central EGA message sample:

```json
{
   "encrypted_checksums":[
      {
         "type":"sha256",
         "value":"e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"
      }
   ],
   "file_last_modified":1473374078,
   "filepath":"/ega/inbox/john/kitty.jpg",
   "filesize":200436,
   "operation":"upload",
   "user":"john"
}
```

## Configuration

| Variable name            | Default value   | Mandatory | Description                                           |                                                                                                               
|--------------------------|-----------------|-----------|-------------------------------------------------------|
| INBOX_LOCATION           |                 | x         | Path to the Inbox folder on a filesystem              |                                                                                                               
| MQ_CONNECTION            |                 | x         | AMQP(s) URI of the RabbitMQ broker                    |                                                                                                               
| QUEUE_FROM               | inbox           |           | Queue name to consume messages from                   |                                                                                                               
| EXCHANGE_TO              | cega            |           | Exchange name to publish messages to                  |
| ROUTING_KEY              | files.inbox     |           | Routing key for messages (both incoming and outgoing) |
