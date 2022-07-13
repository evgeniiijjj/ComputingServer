import java.io.IOException;
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
            String input = read();
            if (!input.equalsIgnoreCase("end")) {
                key.interestOps(SelectionKey.OP_WRITE);
                ByteBuffer output = ByteBuffer.wrap(removingSpaces(input).getBytes(StandardCharsets.UTF_8));
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

    private String read() throws IOException {
        ByteBuffer input = ByteBuffer.allocate(2 << 10);
        StringBuilder sb = new StringBuilder();
        int readBytes = socket.read(input);
        while (readBytes > 0) {
            sb.append(new String(input.array(), 0, readBytes, StandardCharsets.UTF_8));
            input.clear();
            readBytes = socket.read(input);
        }
        return sb.toString();
    }

    private String removingSpaces(String string) {
        return string.replaceAll("\\s+", "");
    }
}
