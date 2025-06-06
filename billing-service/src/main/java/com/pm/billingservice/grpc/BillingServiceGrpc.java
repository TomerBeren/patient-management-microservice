package com.pm.billingservice.grpc;

import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class BillingServiceGrpc extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpc.class);

    @Override
    public void createBillingAccount(billing.BillingRequest request,
                                    StreamObserver<BillingResponse> responseObserver) {

        log.info("createBillingAccount request received {}", request.toString());

        //business logic - e.g save to db, perform calculates etc

        BillingResponse response = BillingResponse.newBuilder()
                .setAccountId("12345")
                .setStatus("ACTIVE")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
