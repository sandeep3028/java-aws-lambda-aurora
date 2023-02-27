package org.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rdsdata.RdsDataClient;
import software.amazon.awssdk.services.rdsdata.model.ExecuteStatementRequest;

/*

Instances of Lambdas are added and removed dynamically.
When a new instance handles its first request, the response time increases, which is called a cold start.(when a new container will be spinned up)
After that request is processed, the instance stays alive (≈10 m) to be reused for subsequent requests.

A “cold start” is the 1st request that a new Lambda worker handles.
This request takes longer to process because the Lambda service needs to:
- find a space in its EC2 fleet to allocate the worker.
- initialize the worker.
- initialize your function module before it can pass the request to your handler function.

How long function remains warm?
- Depend on usage pattern and parallel requests landing on your function - this is why they don't disclose an hard number.

 */

//Core Java
//Duration: 11675.28 ms	Billed Duration: 11676 ms	Memory Size: 512 MB	Max Memory Used: 138 MB	Init Duration: 420.03 ms
//Duration: 343.78 ms	Billed Duration: 344 ms	Memory Size: 512 MB	Max Memory Used: 139 MB
//Duration: 444.97 ms	Billed Duration: 445 ms	Memory Size: 512 MB	Max Memory Used: 141 MB

//Spring Cloud
//Duration: 23384.11 ms	Billed Duration: 23385 ms	Memory Size: 512 MB	Max Memory Used: 192 MB	Init Duration: 774.94 ms
//Duration: 401.29 ms	Billed Duration: 402 ms	Memory Size: 512 MB	Max Memory Used: 193 MB
//Duration: 300.34 ms	Billed Duration: 301 ms	Memory Size: 512 MB	Max Memory Used: 194 MB

//Micronaut
//Duration: 11623.53 ms	Billed Duration: 11624 ms	Memory Size: 512 MB	Max Memory Used: 196 MB	Init Duration: 3266.90 ms
//Duration: 460.85 ms	Billed Duration: 461 ms	Memory Size: 512 MB	Max Memory Used: 196 MB

public class CustomerHandler implements RequestHandler<Object, String> {
    @Override
    public String handleRequest(Object s, Context context) {
        RdsDataClient client = RdsDataClient.builder().region(Region.US_EAST_1).build();

        String resourceArn = "arn:aws:rds:us-east-1:231909950768:cluster:my-retail-database";
        String secretArn = "arn:aws:secretsmanager:us-east-1:231909950768:secret:rds-db-credentials/cluster-SW6JGVVX2IGVBHAM53CKQAVHKA/postgres/1677243823204-b4e5or";
        String database = "my_retail_database_app";
        String sqlStatement = "SELECT * FROM Customers";
        ExecuteStatementRequest sqlRequest = ExecuteStatementRequest.builder()
                .resourceArn(resourceArn)
                .secretArn(secretArn)
                .database(database)
                .sql(sqlStatement)
                .build();
        return client.executeStatement(sqlRequest).toString();
    }
}
