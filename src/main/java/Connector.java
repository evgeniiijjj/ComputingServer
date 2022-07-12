import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Connector implements Runnable {
    private final ServerSocketChannel socketChannel;
    private final Selector selector;

    public Connector(ServerSocketChannel socketChannel, Selector selector) {
        this.socketChannel = socketChannel;
        this.selector = selector;
    }

    @Override
    public void run() {
        try {
            SocketChannel channel = socketChannel.accept();
            if (channel != null) {
                new Handler(selector, channel);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
