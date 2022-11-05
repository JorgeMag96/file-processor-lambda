terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 4.0.0"
    }
  }

  required_version = ">= 0.13.4"
}

// Configured for LocalStack
provider "aws" {
  access_key                  = "test"
  secret_key                  = "test"
  region                      = "us-west-2"

  s3_use_path_style           = true  // so that it generates a request https://s3.example.com/my_bucket/foo instead of
                                      // https://my_bucket.s3.example.com/foo

  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true

  endpoints {
    apigateway     = "http://localhost:4566"
    apigatewayv2   = "http://localhost:4566"
    cloudformation = "http://localhost:4566"
    cloudwatch     = "http://localhost:4566"
    dynamodb       = "http://localhost:4566"
    ec2            = "http://localhost:4566"
    es             = "http://localhost:4566"
    elasticache    = "http://localhost:4566"
    firehose       = "http://localhost:4566"
    iam            = "http://localhost:4566"
    kinesis        = "http://localhost:4566"
    lambda         = "http://localhost:4566"
    rds            = "http://localhost:4566"
    redshift       = "http://localhost:4566"
    route53        = "http://localhost:4566"
    s3             = "http://localhost:4566"
    secretsmanager = "http://localhost:4566"
    ses            = "http://localhost:4566"
    sns            = "http://localhost:4566"
    sqs            = "http://localhost:4566"
    ssm            = "http://localhost:4566"
    stepfunctions  = "http://localhost:4566"
    sts            = "http://localhost:4566"
  }
}

resource "aws_s3_bucket" "input_bucket" {
  bucket = "input-bucket"
  force_destroy = true
  tags = {
    Name        = "Input Bucket"
    Environment = "Dev"
  }
}

resource "aws_s3_bucket" "output_bucket" {
  bucket = "output-bucket"
  force_destroy = true
  tags = {
    Name        = "Output Bucket"
    Environment = "Dev"
  }
}

resource "aws_lambda_function" "file_processor_lambda" {
  function_name = "file_processor_lambda"
  role          = aws_iam_role.file_processor_lambda_iam_role.arn
  handler       = "com.example.LambdaHandler::handleRequest"
  runtime       = "java11"
  filename      = "../../build/distributions/file-processor-lambda.zip"
  source_code_hash = filebase64sha256("../../build/distributions/file-processor-lambda.zip")
  tags = {
    Name        = "File Processor Lambda Function"
    Environment = "Dev"
  }
}

resource "aws_iam_role" "file_processor_lambda_iam_role" {
  name = "file_processor_lambda_iam_role"
  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}

resource "aws_lambda_permission" "allow_bucket" {
  statement_id  = "AllowExecutionFromS3Bucket"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.file_processor_lambda.arn
  principal     = "s3.amazonaws.com"
  source_arn    = aws_s3_bucket.input_bucket.arn
}

resource "aws_s3_bucket_notification" "input-bucket-notification" {
  bucket = aws_s3_bucket.input_bucket.id

  lambda_function {
    lambda_function_arn = aws_lambda_function.file_processor_lambda.arn
    events              = ["s3:ObjectCreated:*"]
  }

  depends_on = [aws_lambda_permission.allow_bucket]
}
