# AWS Lambda function in Java with S3 events integration

<p align="center">
  <img alt="Architecture" src="docs/images/s3-lambda-s3.png" />
</p>

---

The project source includes function code and supporting resources:

> - `src/main` - A Java Lambda function that processes files from one S3 bucket to another.
> - `src/test` - A unit test and helper classes.
> - `build.gradle` - A Gradle build file.
> - `localstack/docker-compose.yml` - A localstack docker-compose file to run the application locally.
> - `localstack/terraform/main.tf` - A terraform file to create all the infrastructure required for the application.

Use the following instructions to deploy the sample application.

# Requirements

>
> - [Java 11 runtime environment (SE JRE)](https://www.oracle.com/java/technologies/javase-downloads.html)
> - [Gradle 5](https://gradle.org/releases/)
> - The Bash shell. For Linux and macOS, this is included by default. In Windows 10, you can install the [Windows Subsystem for Linux](https://docs.microsoft.com/en-us/windows/wsl/install-win10) to get a Windows-integrated version of Ubuntu and Bash.
> - [The AWS CLI](https://docs.aws.amazon.com/cli/latest/userguide/cli-chap-install.html) v1.17 or newer.
> - [Docker](https://docs.docker.com/get-docker/) and [Docker Compose](https://docs.docker.com/compose/install/) to run the application locally with LocalStack.
> - [Terraform](https://www.terraform.io/downloads) (codifies cloud APIs into declarative configuration files.)

[LocalStack](https://localstack.cloud/) Is a fully functional local cloud stack to develop and test your cloud and serverless apps offline.
We are going to use it with Docker to run the application locally.

### Optional:
> - [awscli-local](https://github.com/localstack/awscli-local) A wrapper around the AWS CLI that allows you to use the AWS CLI to interact with LocalStack.
> - [LocalStack Cockpit](https://localstack.cloud/products/cockpit/) A web-based UI for LocalStack that allows you to manage your local AWS environment.

# Setup

* This is a list item
* This is another list item

1. This is a numbered list item
2. This is another numbered list item

# Notes

* **IAM security enforcement** is not supported by LocalStack Community edition. Make sure to properly set up and test these configurations while deploying the application to AWS.