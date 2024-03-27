package com.performance.config;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import com.hudsonmx.billing.rules.service.endpoint.grpc.pricing.GetPricingDataFlowGrpc;
import com.hudsonmx.billing.rules.service.endpoint.grpc.pricing.GetPricingDataRequestProto;
import com.hudsonmx.billing.rules.service.endpoint.grpc.pricing.GetPricingDataResponseProto;
import com.performance.Main;
import io.grpc.ManagedChannelBuilder;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Run {

    Logger log = LogManager.getLogger(Run.class.getName());
    public GetPricingDataResponseProto run(String path) throws InvalidProtocolBufferException {


        StringBuilder jsonContent = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        var requestBuilder = GetPricingDataRequestProto.newBuilder();
        JsonFormat.parser().ignoringUnknownFields().merge(jsonContent.toString(), requestBuilder);

        var request = requestBuilder.build();
        final var startTime = LocalDateTime.now();

        log.info("Start get pricing data: {}", startTime);
        var channel = ManagedChannelBuilder.forAddress("192.168.2.28", 50102).usePlaintext().build();

        var result =  GetPricingDataFlowGrpc.newBlockingStub(channel).runGetPricingDataFlow(request);
        final var endTime = LocalDateTime.now();
        log.info("End get pricing data: {}", endTime);
        log.info("Execution time: {}", Duration.between(startTime, endTime));

        if (result == null || result.hasError()) {
            log.info("Error result: {}", result);
        }
        return result;
    }
}
