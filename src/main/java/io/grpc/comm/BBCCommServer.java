package io.grpc.comm;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class BBCCommServer {
    private final ConcurrentHashMap<Integer, List<MSG>> messages = new ConcurrentHashMap<>();
    private final Server server;
    private final int id;

    public BBCCommServer(int id, int port) {
        this.id = id;
        server = ServerBuilder.forPort(port).addService(new BBCService()).build();
    }

    private class BBCService extends BBCCommGrpc.BBCCommImplBase {
        @Override
        public void sendMSG(MSG request, StreamObserver<Response> responseObserver) {
            addRequest(request);
            responseObserver.onNext(Response.newBuilder().setReturnStatus(200).build());
            responseObserver.onCompleted();
        }

        private void addRequest(MSG request) {


            // TODO Manor and Assaf logic.

            if (!messages.containsKey(request.getNodeId())) {
                messages.put(request.getNodeId(), Collections.synchronizedList(new ArrayList<>()));
            }
            messages.get(request.getNodeId()).add(request);
            System.out.println(String.valueOf(id) + ": received:" + String.valueOf(messages.get(request.getNodeId()).size())); // TODO delete

        }
    }

    public void start() throws IOException {
        server.start();
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public ConcurrentHashMap<Integer, List<MSG>> getMessages() {
        return messages;
    }
}
