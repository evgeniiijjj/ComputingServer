import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Handler implements Runnable {
    private final SocketChannel socket;
    private final SelectionKey key;

    public Handler(Selector selector, SocketChannel socket) throws IOException {
        this.socket = socket;
        socket.configureBlocking(false);
        key = socket.register(selector, 0);
        key.attach(this);
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    @Override
    public void run() {
        try {
            ByteBuffer input = ByteBuffer.allocate(2 << 2);
            int readBytes = socket.read(input);
            String in = new String(input.array(), 0, readBytes, StandardCharsets.UTF_8).trim();
            if (!in.equalsIgnoreCase("end")) {
                key.interestOps(SelectionKey.OP_WRITE);
                ByteBuffer output = ByteBuffer.wrap(calculationFibonacciNumbers(Integer.parseInt(in)).getBytes(StandardCharsets.UTF_8));
                socket.write(output);
                key.interestOps(SelectionKey.OP_READ);
            } else {
                key.cancel();
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String calculationFibonacciNumbers(int sequenceNumber) {
        BigInteger prev = BigInteger.ZERO;
        BigInteger last = BigInteger.ZERO;
        BigInteger buff;
        for (int i = 0; i < sequenceNumber; i++) {
            buff = last.add(prev);
            prev = last;
            last = buff;
            if (i == 1) last = BigInteger.ONE;
        }
        return last.toString();
    }
}
