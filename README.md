# SPAF

Stream Processing Abstraction Framework for Java.

This framework allows you to develop a stream processing application in a vendor-independent fashion.

Supported stream processing framework/engine vendors are: [Flink](https://flink.apache.org/), [Spark](https://spark.apache.org/), [Samza](https://samza.apache.org/) and [Storm](storm.apache.org/).\
Supported message broker vendors are: [Kafka](kafka.apache.org/) and [RabbitMQ](https://www.rabbitmq.com/).

SPAF lets you:
- define _source_ (input) and _sink_ (output) streams declaratively (message queues only);
- define the application business logic in terms of a linear topology composed by _processors_: each _processor_ takes a single element as input, performs arbitrary transformations, and returns zero, one or more elements as output;
- change the actual stream processing engine/framework, and/or the message broker, by replacing the dependencies of your project, without changing the application code;
- specify application configuration (e.g. the application name, third-party library settings);
- specify the stream processing engine/framework configurations.

This framework was developed in the context of [my work of thesis](https://amslaurea.unibo.it/25558/) (PDF available, italian only).

Contact: nicolo.scarpa@studio.unibo.it

## SPAF Examples

The `spaf-example` Maven project is the parent project for the actual example projects, listed below:
- `spaf-examples-simple`: a basic example where simple text transformations are performed in the streaming processors;
- `spaf-examples-alpr`: an example which uses the [OpenALPR](https://github.com/openalpr/openalpr) library to recognize license plates on the input images;
- `spaf-examples-face`: an example which uses the [OpenIMAJ](http://openimaj.org/) library to perform both face detection and face recognition tasks on input images;
- `spaf-examples-ocr`: an example which uses [Tesseract](https://github.com/tesseract-ocr/tesseract) (an open-source, neural net based, OCR engine) through the [Tess4J](https://github.com/nguyenq/tess4j) Java JNA wrapper for recognizing text on the input images.

> Statement by the author
>
> The `alpr`, `face` and `ocr` examples were not conceived in the context of my work of thesis. These were pre-existing application examples developed by other students and used another framework. These applications were refactored and adapted to use SPAF.

All the example projects inherit their dependencies from the parent project (`spaf-example`) by activating Maven profiles.\
Depending on the activated profiles, an example project can then be executed on a particular stream processing framework/engine and either on a distributed or local environment.\
For each stream processing engine (Flink, Samza, Spark and Storm) there are two profiles: `<stream-engine-name>` and `<stream-engine-name>_local`. The `<stream-engine-name>` profile is the main profile which is needed for executing the example with the corresponding framework; this profile supports the execution in a distributed environment. If you want to test the example on a local environment then **also** activate the `<steam-engine-name>_local` profile, since it overrides (and, sometimes, adds) some dependencies of the main profile.

For example, these are the two profiles for executing the examples on Apache Flink (remote or local):
- `flink-streaming`: profile for installing the dependencies needed to execute the example in a remote Flink cluster;
- `flink-streaming_local`: profile which overrides `flink-streaming` profile for executing the example in a local Flink cluster.

In other words, you should enable both profiles if you want to test the example locally. 

> **IntelliJ IDEA users**
> 
> When activating one or more Maven profiles (e.g. `flink-streaming` and `flink-streaming_local`) please hit the `Reload All Maven Projects` button to refresh the `spaf-examples` child projects dependencies.
> Without doing so, when launching the main class of an example project (e.g. `spaf-examples-simple`), you could get the following error:
> ```shell
> 23:16:02.909 [main] WARN  i.u.disi.spaf.api.StreamProcessing - No providers found.
> Exception in thread "main" it.unibo.disi.spaf.common.exceptions.StreamProcessingException: No Stream Processing provider for Context
> at it.unibo.disi.spaf.api.StreamProcessing.createContextFactory(StreamProcessing.java:36)
> at it.unibo.disi.spaf.examples.simple.SimpleApp.main(SimpleApp.java:19)
> ```

### SPAF Examples Utils

The `spaf-examples-utils` module provides some basic tools for sending and receiving images to/from Kafka and RabbitMQ clusters in order to feed images to the example applications.

For example, to send images contained in a folder to a RabbitMQ queue use the following command:
```shell
java -jar rabbitmq-image-sender.jar <host> <port> <username> <password> <queue name> <images dir>
```

And, to receive the images processed by the stream processing application, published in another RabbitMQ queue, and store them in a folder, use this other command:
```shell
java -jar rabbitmq-image-receiver.jar <host> <port> <username> <password> <queue name> <images dir>
```
