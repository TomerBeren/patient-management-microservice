package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

/**
 * A Spring‐managed bean that acts as a gRPC client for the BillingService.
 *
 * This class:
 *   1. Reads billing.service.address and billing.service.grpc.port from application properties (or uses defaults).
 *   2. Builds a ManagedChannel to that host:port.
 *   3. Creates a blocking stub (BillingServiceGrpc.BillingServiceBlockingStub).
 *   4. Provides a simple wrapper method createBillingAccount(...) that remote‐calls the BillingService.
 */
@Service
public class BillingServiceGrpcClient {

    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final ManagedChannel channel;
    private final BillingServiceGrpc.BillingServiceBlockingStub blockingStub;

    /**
     * Constructor: Initializes the gRPC channel and stub.
     *
     * @param serverAddress the hostname or IP of the billing‐service (default = "localhost")
     * @param serverPort    the gRPC port on which billing‐service is listening (default = 9001)
     */
    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String serverAddress,
            @Value("${billing.service.grpc.port:9001}") int serverPort) {

        // Build a channel to the remote billing service
        this.channel = ManagedChannelBuilder
                .forAddress(serverAddress, serverPort)
                .usePlaintext()          // Disable TLS – use only if you run in a trusted network.
                .build();

        // Create a blocking (synchronous) stub from the generated BillingServiceGrpc class.
        this.blockingStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder().setName(name).setEmail(email).build();
        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received response from billing service via GRPC: {}", response);
        return response;
    }

    @PreDestroy
    public void shutdownChannel() {
        if (channel != null && !channel.isShutdown()) {
            channel.shutdown();
        }
    }
}
