package io.grpc.comm;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.comm.BBCCommGrpc.BBCCommBlockingStub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BBCCommClient {
    private int id;
    private final HashMap<String, BBCCommBlockingStub> blockingStubs;

    public BBCCommClient(int id, List<String> hosts, int port) {
        this.id = id;
        // usePlaintext() - testing only.
        blockingStubs = new HashMap<>();
        for (String host : hosts) {
            Channel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
            blockingStubs.put(host, BBCCommGrpc.newBlockingStub(channel));
        }
    }

    public void sendMSG(String reciever, String msgStr) {
        MSG msg = MSG.newBuilder().setContent(msgStr).setNodeId(this.id).build();

        try {
            Response returnCode = blockingStubs.get(reciever).sendMSG(msg);
        } catch (StatusRuntimeException e) {
            assert (false);

        }

    }


}
